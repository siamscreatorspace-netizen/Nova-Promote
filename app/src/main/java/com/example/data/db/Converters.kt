package com.example.data.db

import androidx.room.TypeConverter
import com.example.data.model.*

class Converters {
    @TypeConverter
    fun fromUserRole(value: UserRole): String = value.name

    @TypeConverter
    fun toUserRole(value: String): UserRole = runCatching { UserRole.valueOf(value) }.getOrDefault(UserRole.CUSTOMER)

    @TypeConverter
    fun fromPlatformType(value: PlatformType): String = value.name

    @TypeConverter
    fun toPlatformType(value: String): PlatformType = runCatching { PlatformType.valueOf(value) }.getOrDefault(PlatformType.INSTAGRAM)

    @TypeConverter
    fun fromServiceCategory(value: ServiceCategory): String = value.name

    @TypeConverter
    fun toServiceCategory(value: String): ServiceCategory = runCatching { ServiceCategory.valueOf(value) }.getOrDefault(ServiceCategory.AD_CAMPAIGN_MANAGEMENT)

    @TypeConverter
    fun fromServiceStatus(value: ServiceStatus): String = value.name

    @TypeConverter
    fun toServiceStatus(value: String): ServiceStatus = runCatching { ServiceStatus.valueOf(value) }.getOrDefault(ServiceStatus.APPROVED)

    @TypeConverter
    fun fromOrderStatus(value: OrderStatus): String = value.name

    @TypeConverter
    fun toOrderStatus(value: String): OrderStatus = runCatching { OrderStatus.valueOf(value) }.getOrDefault(OrderStatus.PLACED)

    @TypeConverter
    fun fromTransactionType(value: TransactionType): String = value.name

    @TypeConverter
    fun toTransactionType(value: String): TransactionType = runCatching { TransactionType.valueOf(value) }.getOrDefault(TransactionType.DEPOSIT)

    @TypeConverter
    fun fromTransactionStatus(value: TransactionStatus): String = value.name

    @TypeConverter
    fun toTransactionStatus(value: String): TransactionStatus = runCatching { TransactionStatus.valueOf(value) }.getOrDefault(TransactionStatus.COMPLETED)

    @TypeConverter
    fun fromAuditSeverity(value: AuditSeverity): String = value.name

    @TypeConverter
    fun toAuditSeverity(value: String): AuditSeverity = runCatching { AuditSeverity.valueOf(value) }.getOrDefault(AuditSeverity.INFO)
}
