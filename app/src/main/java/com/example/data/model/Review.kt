package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reviews")
data class ReviewEntity(
    @PrimaryKey val id: String,
    val serviceId: String,
    val orderId: String,
    val buyerId: String,
    val buyerName: String,
    val rating: Int,
    val comment: String,
    val verifiedPurchase: Boolean = true,
    val timestamp: Long = System.currentTimeMillis()
)

enum class AuditSeverity {
    INFO,
    WARNING,
    SECURITY_ALERT
}

@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey val id: String,
    val eventType: String,
    val details: String,
    val initiatedBy: String,
    val severity: AuditSeverity = AuditSeverity.INFO,
    val timestamp: Long = System.currentTimeMillis()
)
