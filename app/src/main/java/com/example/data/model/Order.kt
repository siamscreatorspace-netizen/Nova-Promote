package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class OrderStatus(val label: String) {
    PLACED("Order Placed (Escrow Held)"),
    IN_PROGRESS("Campaign In Progress"),
    PROOF_SUBMITTED("Proof Delivered for Review"),
    REVISION_REQUESTED("Revision Requested"),
    COMPLETED("Completed & Released"),
    DISPUTED("In Dispute (Admin Review)"),
    REFUNDED("Refunded to Wallet"),
    CANCELLED("Cancelled")
}

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val id: String,
    val serviceId: String,
    val serviceTitle: String,
    val category: ServiceCategory,
    val platform: PlatformType,
    val buyerId: String,
    val buyerName: String,
    val providerId: String,
    val providerName: String,
    val amount: Double,
    val status: OrderStatus = OrderStatus.PLACED,
    val requirementsText: String = "",
    val targetLink: String = "",
    val proofDescription: String = "",
    val proofLink: String = "",
    val revisionNotes: String = "",
    val disputeReason: String = "",
    val disputeResolution: String = "",
    val complianceChecked: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
