package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TransactionType(val label: String) {
    DEPOSIT("Wallet Deposit"),
    ESCROW_HOLD("Order Escrow Hold"),
    ESCROW_RELEASE("Escrow Payout"),
    REFUND("Dispute Refund"),
    WITHDRAWAL("Bank Payout")
}

enum class TransactionStatus {
    COMPLETED,
    HELD_IN_ESCROW,
    PENDING_PAYOUT,
    REFUNDED
}

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val orderId: String? = null,
    val type: TransactionType,
    val amount: Double,
    val status: TransactionStatus = TransactionStatus.COMPLETED,
    val paymentGateway: String = "Nova Secure Escrow Gateway",
    val referenceCode: String,
    val note: String,
    val timestamp: Long = System.currentTimeMillis()
)
