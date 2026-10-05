package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.repository.ComplianceCheckResult
import com.example.data.repository.NovaRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class CreateServiceForm(
    val title: String = "",
    val description: String = "",
    val category: ServiceCategory = ServiceCategory.AD_CAMPAIGN_MANAGEMENT,
    val platform: PlatformType = PlatformType.INSTAGRAM,
    val priceString: String = "95.0",
    val deliveryDaysString: String = "4",
    val deliverables: String = "• Verified Ad Setup & Demographic Targeting\n• Weekly Performance Dashboard PDF",
    val proofRequirement: String = "Official Ad Manager Dashboard Export"
)

class NovaViewModel(
    private val repository: NovaRepository
) : ViewModel() {

    // Current active user ID (defaults to customer)
    private val _currentUserId = MutableStateFlow("usr_customer_1")
    val currentUserId: StateFlow<String> = _currentUserId.asStateFlow()

    // Observe active user
    val currentUser: StateFlow<UserEntity?> = _currentUserId
        .flatMapLatest { id -> repository.getUser(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // All registered users for switching roles / admin view
    val allUsers: StateFlow<List<UserEntity>> = repository.getAllUsers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filter & Search states
    val searchQuery = MutableStateFlow("")
    val selectedPlatform = MutableStateFlow<PlatformType?>(null)
    val selectedCategory = MutableStateFlow<ServiceCategory?>(null)

    // Services
    val approvedServices: StateFlow<List<ServiceEntity>> = repository.getApprovedServices()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allServicesForAdmin: StateFlow<List<ServiceEntity>> = repository.getAllServices()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Combined filtered services
    val filteredServices = combine(
        approvedServices,
        searchQuery,
        selectedPlatform,
        selectedCategory
    ) { services, query, platform, category ->
        services.filter { s ->
            val matchesQuery = query.isBlank() ||
                s.title.contains(query, ignoreCase = true) ||
                s.description.contains(query, ignoreCase = true) ||
                s.providerName.contains(query, ignoreCase = true)

            val matchesPlatform = platform == null || s.platform == platform
            val matchesCategory = category == null || s.category == category

            matchesQuery && matchesPlatform && matchesCategory
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Orders for active user
    val customerOrders: StateFlow<List<OrderEntity>> = _currentUserId
        .flatMapLatest { id -> repository.getOrdersByBuyer(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val providerOrders: StateFlow<List<OrderEntity>> = _currentUserId
        .flatMapLatest { id -> repository.getOrdersByProvider(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allOrdersForAdmin: StateFlow<List<OrderEntity>> = repository.getAllOrders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Transactions for active user
    val userTransactions: StateFlow<List<TransactionEntity>> = _currentUserId
        .flatMapLatest { id -> repository.getTransactions(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTransactionsForAdmin: StateFlow<List<TransactionEntity>> = repository.getAllTransactions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Audit logs for Admin / Security monitor
    val auditLogs: StateFlow<List<AuditLogEntity>> = repository.getAuditLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI Feedback Message
    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    // Form state for creating services
    val createForm = MutableStateFlow(CreateServiceForm())

    fun clearUserMessage() {
        _userMessage.value = null
    }

    fun switchUser(userId: String) {
        _currentUserId.value = userId
    }

    fun setSearch(query: String) {
        searchQuery.value = query
        // Live search warning if user types prohibited terms
        val check = repository.checkCompliance(query)
        if (check is ComplianceCheckResult.Violation) {
            _userMessage.value = "⚠️ Policy Warning: Nova Promote prohibits fake followers, bots, or artificial vanity metrics. Search filtered to legal ad services."
        }
    }

    fun setPlatformFilter(platform: PlatformType?) {
        selectedPlatform.value = if (selectedPlatform.value == platform) null else platform
    }

    fun setCategoryFilter(category: ServiceCategory?) {
        selectedCategory.value = if (selectedCategory.value == category) null else category
    }

    // Place order via Escrow
    fun placeOrder(
        service: ServiceEntity,
        requirements: String,
        targetLink: String,
        onSuccess: (OrderEntity) -> Unit
    ) {
        viewModelScope.launch {
            val user = currentUser.value ?: return@launch
            val result = repository.placeOrder(user, service, requirements, targetLink)
            result.onSuccess { order ->
                _userMessage.value = "✓ Order placed! $${service.price} held safely in Nova Escrow until delivery."
                onSuccess(order)
            }.onFailure { err ->
                _userMessage.value = "Error: ${err.message}"
            }
        }
    }

    // Deposit to wallet via simulated legitimate payment gateway
    fun depositToWallet(amount: Double, gateway: String) {
        viewModelScope.launch {
            val user = currentUser.value ?: return@launch
            val result = repository.depositFunds(user.id, amount, gateway)
            result.onSuccess {
                _userMessage.value = "✓ Added $$amount to wallet via $gateway (Encrypted Escrow Vault)"
            }.onFailure { err ->
                _userMessage.value = "Deposit failed: ${err.message}"
            }
        }
    }

    // Provider accepts order
    fun acceptOrder(orderId: String) {
        viewModelScope.launch {
            val user = currentUser.value ?: return@launch
            repository.acceptOrder(orderId, user.id)
                .onSuccess { _userMessage.value = "✓ Order accepted. Work marked in progress." }
        }
    }

    // Provider submits proof of delivery
    fun submitProof(orderId: String, proofDescription: String, proofLink: String) {
        viewModelScope.launch {
            val user = currentUser.value ?: return@launch
            repository.submitOrderProof(orderId, user.id, proofDescription, proofLink)
                .onSuccess { _userMessage.value = "✓ Proof of work submitted! Customer notified for review." }
        }
    }

    // Customer completes order and releases escrow
    fun approveProofAndReleaseEscrow(orderId: String) {
        viewModelScope.launch {
            val user = currentUser.value ?: return@launch
            repository.completeOrderAndReleaseEscrow(orderId, user.id)
                .onSuccess { _userMessage.value = "✓ Order confirmed. Escrow payout released to provider." }
        }
    }

    // Customer requests revision
    fun requestRevision(orderId: String, notes: String) {
        viewModelScope.launch {
            val user = currentUser.value ?: return@launch
            repository.requestRevision(orderId, user.id, notes)
                .onSuccess { _userMessage.value = "Revision request sent to provider." }
        }
    }

    // Customer raises dispute
    fun raiseDispute(orderId: String, reason: String) {
        viewModelScope.launch {
            val user = currentUser.value ?: return@launch
            repository.openDispute(orderId, user.id, reason)
                .onSuccess { _userMessage.value = "⚠️ Dispute escalated to Nova Compliance Team for investigation." }
        }
    }

    // Customer writes review
    fun submitReview(serviceId: String, orderId: String, rating: Int, comment: String) {
        viewModelScope.launch {
            val user = currentUser.value ?: return@launch
            repository.addReview(serviceId, orderId, user.id, user.name, rating, comment)
                .onSuccess { _userMessage.value = "✓ Thank you for your verified client review!" }
        }
    }

    // Provider creates service
    fun submitNewService(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val user = currentUser.value ?: return@launch
            val form = createForm.value
            val price = form.priceString.toDoubleOrNull() ?: 50.0
            val deliveryDays = form.deliveryDaysString.toIntOrNull() ?: 3

            val newService = ServiceEntity(
                id = "srv_${System.currentTimeMillis() % 100000}",
                providerId = user.id,
                providerName = user.name,
                providerAgency = user.agencyName.ifEmpty { "Independent Creator" },
                isProviderVerified = user.isVerified,
                title = form.title,
                description = form.description,
                category = form.category,
                platform = form.platform,
                price = price,
                deliveryDays = deliveryDays,
                deliverables = form.deliverables,
                proofRequirement = form.proofRequirement,
                rating = 5.0,
                reviewCount = 0
            )

            val result = repository.createService(newService, user)
            result.onSuccess { created ->
                _userMessage.value = "✓ Service '${created.title}' submitted! Status: ${created.status.name}"
                createForm.value = CreateServiceForm()
                onSuccess()
            }.onFailure { err ->
                _userMessage.value = "⚠️ Policy Rejection: ${err.message}"
            }
        }
    }

    // Admin resolves dispute
    fun adminResolveDispute(orderId: String, refundBuyer: Boolean, notes: String) {
        viewModelScope.launch {
            repository.adminResolveDispute(orderId, refundBuyer, notes)
                .onSuccess {
                    val outcome = if (refundBuyer) "Refunded to Buyer" else "Released to Provider"
                    _userMessage.value = "✓ Dispute resolved: $outcome"
                }
        }
    }

    // Admin updates service status
    fun adminModerateService(serviceId: String, status: ServiceStatus, reason: String = "") {
        viewModelScope.launch {
            repository.updateServiceStatus(serviceId, status, reason)
            _userMessage.value = "Service status set to ${status.name}"
        }
    }

    // Admin toggles verification
    fun adminToggleVerification(userId: String, isVerified: Boolean) {
        viewModelScope.launch {
            repository.setVerification(userId, isVerified)
            _userMessage.value = "User verification updated to $isVerified"
        }
    }
}
