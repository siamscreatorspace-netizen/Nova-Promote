package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class UserRole {
    CUSTOMER,
    PROVIDER,
    ADMIN
}

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val role: UserRole,
    val walletBalance: Double = 0.0,
    val escrowBalance: Double = 0.0,
    val isVerified: Boolean = false,
    val providerBio: String = "",
    val rating: Double = 5.0,
    val completedOrdersCount: Int = 0,
    val agencyName: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
