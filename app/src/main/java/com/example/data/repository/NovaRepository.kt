package com.example.data.repository

import com.example.data.db.NovaDatabase
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import java.util.UUID

sealed class ComplianceCheckResult {
    object Clean : ComplianceCheckResult()
    data class Violation(val reason: String, val matchedTerm: String) : ComplianceCheckResult()
}

class NovaRepository(private val db: NovaDatabase) {

    private val userDao = db.userDao()
    private val serviceDao = db.serviceDao()
    private val orderDao = db.orderDao()
    private val transactionDao = db.transactionDao()
    private val reviewDao = db.reviewDao()
    private val auditLogDao = db.auditLogDao()

    // Banned keyword blacklist for strict compliance with Facebook, Instagram, YouTube, TikTok, Telegram
    private val prohibitedKeywords = listOf(
        "fake follower", "fake like", "fake view", "fake account", "bot", "bots",
        "auto-follow", "autofollow", "auto-like", "autolike", "auto view",
        "mass follow", "mass unfollow", "scrape", "scraping", "bypass rules",
        "guaranteed followers", "guaranteed likes", "guaranteed views", "synthetic",
        "click farm", "click-farm", "artificial traffic", "password", "login credentials",
        "bypass 2fa", "hack account", "ghost follower", "view bot"
    )

    fun checkCompliance(text: String): ComplianceCheckResult {
        val lower = text.lowercase()
        for (banned in prohibitedKeywords) {
            if (lower.contains(banned)) {
                return ComplianceCheckResult.Violation(
                    reason = "Content contains prohibited phrase '$banned'. Nova Promote strictly bans fake metrics, automation bots, scraping, and platform ToS violations.",
                    matchedTerm = banned
                )
            }
        }
        return ComplianceCheckResult.Clean
    }

    // --- Users ---
    fun getUser(userId: String): Flow<UserEntity?> = userDao.getUserById(userId)
    fun getAllUsers(): Flow<List<UserEntity>> = userDao.getAllUsers()
    suspend fun getUserSync(userId: String): UserEntity? = userDao.getUserSync(userId)
    suspend fun insertUser(user: UserEntity) = userDao.insertUser(user)
    suspend fun setVerification(userId: String, isVerified: Boolean) {
        userDao.setVerification(userId, isVerified)
        auditLogDao.insertLog(
            AuditLogEntity(
                id = "log_${System.currentTimeMillis()}",
                eventType = "USER_VERIFICATION_UPDATE",
                details = "User ID $userId verification status set to $isVerified",
                initiatedBy = "ADMIN",
                severity = AuditSeverity.INFO
            )
        )
    }

    // --- Wallet & Payments ---
    fun getTransactions(userId: String): Flow<List<TransactionEntity>> = transactionDao.getTransactionsByUser(userId)
    fun getAllTransactions(): Flow<List<TransactionEntity>> = transactionDao.getAllTransactions()

    suspend fun depositFunds(
        userId: String,
        amount: Double,
        gateway: String = "Compliant Payment Gateway (Stripe/Card)",
        paymentReference: String = "TX_SECURE_${System.currentTimeMillis() % 1000000}"
    ): Result<Unit> {
        if (amount <= 0) return Result.failure(IllegalArgumentException("Deposit amount must be positive"))
        userDao.adjustWallet(userId, amount)
        transactionDao.insertTransaction(
            TransactionEntity(
                id = "tx_${UUID.randomUUID().toString().take(8)}",
                userId = userId,
                type = TransactionType.DEPOSIT,
                amount = amount,
                status = TransactionStatus.COMPLETED,
                paymentGateway = gateway,
                referenceCode = paymentReference,
                note = "Funds deposited to verified marketplace wallet"
            )
        )
        auditLogDao.insertLog(
            AuditLogEntity(
                id = "log_${System.currentTimeMillis()}",
                eventType = "WALLET_DEPOSIT",
                details = "Deposited $$amount via $gateway (Ref: $paymentReference) for user $userId",
                initiatedBy = userId,
                severity = AuditSeverity.INFO
            )
        )
        return Result.success(Unit)
    }

    // --- Marketplace Services ---
    fun getApprovedServices(): Flow<List<ServiceEntity>> = serviceDao.getApprovedServices()
    fun getAllServices(): Flow<List<ServiceEntity>> = serviceDao.getAllServices()
    fun getServicesByProvider(providerId: String): Flow<List<ServiceEntity>> = serviceDao.getServicesByProvider(providerId)
    suspend fun getServiceById(serviceId: String): ServiceEntity? = serviceDao.getServiceById(serviceId)

    suspend fun createService(
        service: ServiceEntity,
        provider: UserEntity
    ): Result<ServiceEntity> {
        val check = checkCompliance("${service.title} ${service.description} ${service.deliverables}")
        return if (check is ComplianceCheckResult.Violation) {
            val flaggedService = service.copy(
                status = ServiceStatus.FLAGGED,
                flagReason = check.reason
            )
            serviceDao.insertService(flaggedService)
            auditLogDao.insertLog(
                AuditLogEntity(
                    id = "log_${System.currentTimeMillis()}",
                    eventType = "PROHIBITED_SERVICE_INTERCEPTED",
                    details = "Provider ${provider.name} attempted to list non-compliant service. Reason: ${check.reason}",
                    initiatedBy = provider.id,
                    severity = AuditSeverity.SECURITY_ALERT
                )
            )
            Result.failure(IllegalArgumentException(check.reason))
        } else {
            // Clean service enters admin review queue or instant approval if verified
            val initialStatus = if (provider.isVerified) ServiceStatus.APPROVED else ServiceStatus.PENDING_APPROVAL
            val savedService = service.copy(status = initialStatus)
            serviceDao.insertService(savedService)
            auditLogDao.insertLog(
                AuditLogEntity(
                    id = "log_${System.currentTimeMillis()}",
                    eventType = "SERVICE_CREATED",
                    details = "New service created: '${savedService.title}' by ${provider.name}. Status: $initialStatus",
                    initiatedBy = provider.id,
                    severity = AuditSeverity.INFO
                )
            )
            Result.success(savedService)
        }
    }

    suspend fun updateServiceStatus(serviceId: String, status: ServiceStatus, reason: String = "") {
        serviceDao.updateStatus(serviceId, status, reason)
        auditLogDao.insertLog(
            AuditLogEntity(
                id = "log_${System.currentTimeMillis()}",
                eventType = "SERVICE_MODERATION",
                details = "Service $serviceId status updated to $status. Reason: ${reason.ifEmpty { "Compliance verification" }}",
                initiatedBy = "ADMIN",
                severity = if (status == ServiceStatus.REJECTED || status == ServiceStatus.FLAGGED) AuditSeverity.WARNING else AuditSeverity.INFO
            )
        )
    }

    // --- Orders & Escrow Lifecycle ---
    fun getOrdersByBuyer(buyerId: String): Flow<List<OrderEntity>> = orderDao.getOrdersByBuyer(buyerId)
    fun getOrdersByProvider(providerId: String): Flow<List<OrderEntity>> = orderDao.getOrdersByProvider(providerId)
    fun getAllOrders(): Flow<List<OrderEntity>> = orderDao.getAllOrders()

    suspend fun placeOrder(
        buyer: UserEntity,
        service: ServiceEntity,
        requirementsText: String,
        targetLink: String
    ): Result<OrderEntity> {
        // 1. Anti-fraud safety check on requirements
        val complianceCheck = checkCompliance("$requirementsText $targetLink")
        if (complianceCheck is ComplianceCheckResult.Violation) {
            auditLogDao.insertLog(
                AuditLogEntity(
                    id = "log_${System.currentTimeMillis()}",
                    eventType = "ORDER_FRAUD_PREVENTED",
                    details = "Buyer ${buyer.name} attempted non-compliant order: ${complianceCheck.reason}",
                    initiatedBy = buyer.id,
                    severity = AuditSeverity.SECURITY_ALERT
                )
            )
            return Result.failure(IllegalArgumentException(complianceCheck.reason))
        }

        // 2. Wallet & Escrow check
        if (buyer.walletBalance < service.price) {
            return Result.failure(IllegalStateException("Insufficient wallet balance. Please add funds via the secure checkout."))
        }

        val orderId = "ord_nov_${System.currentTimeMillis() % 100000}"
        
        // Deduct from buyer wallet and put into buyer escrow
        userDao.adjustWallet(buyer.id, -service.price)
        userDao.adjustEscrow(buyer.id, service.price)

        val order = OrderEntity(
            id = orderId,
            serviceId = service.id,
            serviceTitle = service.title,
            category = service.category,
            platform = service.platform,
            buyerId = buyer.id,
            buyerName = buyer.name,
            providerId = service.providerId,
            providerName = service.providerName,
            amount = service.price,
            status = OrderStatus.PLACED,
            requirementsText = requirementsText,
            targetLink = targetLink,
            complianceChecked = true,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        orderDao.insertOrder(order)

        transactionDao.insertTransaction(
            TransactionEntity(
                id = "tx_${UUID.randomUUID().toString().take(8)}",
                userId = buyer.id,
                orderId = orderId,
                type = TransactionType.ESCROW_HOLD,
                amount = service.price,
                status = TransactionStatus.HELD_IN_ESCROW,
                paymentGateway = "Nova Escrow Vault",
                referenceCode = "ESCROW_${orderId}",
                note = "Held in trust for compliant service: ${service.title}"
            )
        )

        auditLogDao.insertLog(
            AuditLogEntity(
                id = "log_${System.currentTimeMillis()}",
                eventType = "ORDER_PLACED_IN_ESCROW",
                details = "Order $orderId placed for $${service.price}. Escrow locked securely.",
                initiatedBy = buyer.id,
                severity = AuditSeverity.INFO
            )
        )

        return Result.success(order)
    }

    suspend fun acceptOrder(orderId: String, providerId: String): Result<Unit> {
        val order = orderDao.getOrderById(orderId) ?: return Result.failure(IllegalArgumentException("Order not found"))
        val updated = order.copy(
            status = OrderStatus.IN_PROGRESS,
            updatedAt = System.currentTimeMillis()
        )
        orderDao.updateOrder(updated)
        auditLogDao.insertLog(
            AuditLogEntity(
                id = "log_${System.currentTimeMillis()}",
                eventType = "ORDER_ACCEPTED",
                details = "Provider accepted order $orderId and launched campaign management.",
                initiatedBy = providerId,
                severity = AuditSeverity.INFO
            )
        )
        return Result.success(Unit)
    }

    suspend fun submitOrderProof(
        orderId: String,
        providerId: String,
        proofDescription: String,
        proofLink: String
    ): Result<Unit> {
        val order = orderDao.getOrderById(orderId) ?: return Result.failure(IllegalArgumentException("Order not found"))
        val updated = order.copy(
            status = OrderStatus.PROOF_SUBMITTED,
            proofDescription = proofDescription,
            proofLink = proofLink,
            updatedAt = System.currentTimeMillis()
        )
        orderDao.updateOrder(updated)
        auditLogDao.insertLog(
            AuditLogEntity(
                id = "log_${System.currentTimeMillis()}",
                eventType = "PROOF_DELIVERED",
                details = "Proof of legitimate delivery submitted for order $orderId. Review timer initiated.",
                initiatedBy = providerId,
                severity = AuditSeverity.INFO
            )
        )
        return Result.success(Unit)
    }

    suspend fun completeOrderAndReleaseEscrow(orderId: String, buyerId: String): Result<Unit> {
        val order = orderDao.getOrderById(orderId) ?: return Result.failure(IllegalArgumentException("Order not found"))
        val buyer = userDao.getUserSync(order.buyerId) ?: return Result.failure(IllegalArgumentException("Buyer not found"))

        // Release escrow: decrease buyer escrow, increase provider wallet
        userDao.adjustEscrow(buyer.id, -order.amount)
        userDao.adjustWallet(order.providerId, order.amount)

        val updated = order.copy(
            status = OrderStatus.COMPLETED,
            updatedAt = System.currentTimeMillis()
        )
        orderDao.updateOrder(updated)

        transactionDao.insertTransaction(
            TransactionEntity(
                id = "tx_${UUID.randomUUID().toString().take(8)}",
                userId = order.providerId,
                orderId = orderId,
                type = TransactionType.ESCROW_RELEASE,
                amount = order.amount,
                status = TransactionStatus.COMPLETED,
                paymentGateway = "Nova Escrow Vault",
                referenceCode = "RELEASE_${orderId}",
                note = "Payout released for completed order $orderId"
            )
        )

        auditLogDao.insertLog(
            AuditLogEntity(
                id = "log_${System.currentTimeMillis()}",
                eventType = "ESCROW_PAYOUT_RELEASED",
                details = "Customer verified proof. $${order.amount} released to provider ${order.providerName}.",
                initiatedBy = buyerId,
                severity = AuditSeverity.INFO
            )
        )

        return Result.success(Unit)
    }

    suspend fun requestRevision(orderId: String, buyerId: String, notes: String): Result<Unit> {
        val order = orderDao.getOrderById(orderId) ?: return Result.failure(IllegalArgumentException("Order not found"))
        val updated = order.copy(
            status = OrderStatus.REVISION_REQUESTED,
            revisionNotes = notes,
            updatedAt = System.currentTimeMillis()
        )
        orderDao.updateOrder(updated)
        auditLogDao.insertLog(
            AuditLogEntity(
                id = "log_${System.currentTimeMillis()}",
                eventType = "REVISION_REQUESTED",
                details = "Buyer requested deliverable revision on order $orderId: $notes",
                initiatedBy = buyerId,
                severity = AuditSeverity.INFO
            )
        )
        return Result.success(Unit)
    }

    suspend fun openDispute(orderId: String, buyerId: String, reason: String): Result<Unit> {
        val order = orderDao.getOrderById(orderId) ?: return Result.failure(IllegalArgumentException("Order not found"))
        val updated = order.copy(
            status = OrderStatus.DISPUTED,
            disputeReason = reason,
            updatedAt = System.currentTimeMillis()
        )
        orderDao.updateOrder(updated)
        auditLogDao.insertLog(
            AuditLogEntity(
                id = "log_${System.currentTimeMillis()}",
                eventType = "DISPUTE_OPENED",
                details = "Dispute opened for order $orderId. Escalated to Nova Admin Mediation. Reason: $reason",
                initiatedBy = buyerId,
                severity = AuditSeverity.WARNING
            )
        )
        return Result.success(Unit)
    }

    suspend fun adminResolveDispute(
        orderId: String,
        refundBuyer: Boolean,
        resolutionNotes: String
    ): Result<Unit> {
        val order = orderDao.getOrderById(orderId) ?: return Result.failure(IllegalArgumentException("Order not found"))

        if (refundBuyer) {
            // Refund: take back from buyer escrow, give back to buyer wallet
            userDao.adjustEscrow(order.buyerId, -order.amount)
            userDao.adjustWallet(order.buyerId, order.amount)

            val updated = order.copy(
                status = OrderStatus.REFUNDED,
                disputeResolution = "Admin Refunded: $resolutionNotes",
                updatedAt = System.currentTimeMillis()
            )
            orderDao.updateOrder(updated)

            transactionDao.insertTransaction(
                TransactionEntity(
                    id = "tx_${UUID.randomUUID().toString().take(8)}",
                    userId = order.buyerId,
                    orderId = orderId,
                    type = TransactionType.REFUND,
                    amount = order.amount,
                    status = TransactionStatus.REFUNDED,
                    paymentGateway = "Nova Escrow Vault",
                    referenceCode = "REFUND_${orderId}",
                    note = "Dispute refund issued: $resolutionNotes"
                )
            )
        } else {
            // Release to provider
            userDao.adjustEscrow(order.buyerId, -order.amount)
            userDao.adjustWallet(order.providerId, order.amount)

            val updated = order.copy(
                status = OrderStatus.COMPLETED,
                disputeResolution = "Admin Payout: $resolutionNotes",
                updatedAt = System.currentTimeMillis()
            )
            orderDao.updateOrder(updated)

            transactionDao.insertTransaction(
                TransactionEntity(
                    id = "tx_${UUID.randomUUID().toString().take(8)}",
                    userId = order.providerId,
                    orderId = orderId,
                    type = TransactionType.ESCROW_RELEASE,
                    amount = order.amount,
                    status = TransactionStatus.COMPLETED,
                    paymentGateway = "Nova Escrow Vault",
                    referenceCode = "RESOLVED_RELEASE_${orderId}",
                    note = "Admin dispute resolution payout: $resolutionNotes"
                )
            )
        }

        auditLogDao.insertLog(
            AuditLogEntity(
                id = "log_${System.currentTimeMillis()}",
                eventType = "DISPUTE_RESOLVED",
                details = "Order $orderId resolved by Admin. Refund buyer: $refundBuyer. Notes: $resolutionNotes",
                initiatedBy = "ADMIN",
                severity = AuditSeverity.INFO
            )
        )
        return Result.success(Unit)
    }

    // --- Reviews ---
    fun getReviews(serviceId: String): Flow<List<ReviewEntity>> = reviewDao.getReviewsForService(serviceId)

    suspend fun addReview(
        serviceId: String,
        orderId: String,
        buyerId: String,
        buyerName: String,
        rating: Int,
        comment: String
    ): Result<Unit> {
        reviewDao.insertReview(
            ReviewEntity(
                id = "rev_${System.currentTimeMillis()}",
                serviceId = serviceId,
                orderId = orderId,
                buyerId = buyerId,
                buyerName = buyerName,
                rating = rating,
                comment = comment
            )
        )
        return Result.success(Unit)
    }

    // --- Audit Logs ---
    fun getAuditLogs(): Flow<List<AuditLogEntity>> = auditLogDao.getAllLogs()
}
