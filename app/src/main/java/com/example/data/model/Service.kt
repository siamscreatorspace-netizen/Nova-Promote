package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class PlatformType(val displayName: String) {
    INSTAGRAM("Instagram"),
    FACEBOOK("Facebook"),
    YOUTUBE("YouTube"),
    TIKTOK("TikTok"),
    TELEGRAM("Telegram"),
    MULTI_PLATFORM("Cross-Platform")
}

enum class ServiceCategory(val displayName: String, val description: String) {
    AD_CAMPAIGN_MANAGEMENT(
        "Ad Campaign Management",
        "Meta Ads Manager, TikTok Ads, YouTube TrueView & Telegram Official Ads setup and ROI optimization"
    ),
    CONTENT_PROMOTION(
        "Content Promotion",
        "Organic syndication, niche media coverage, verified newsletter feature & newsletter shoutouts"
    ),
    INFLUENCER_MARKETING(
        "Creator & Influencer PR",
        "Direct creator sponsorship deals, unboxing reviews, podcast mentions & brand ambassadorship"
    ),
    VIDEO_PROMOTION(
        "Official Video Ad Promotion",
        "In-feed video ads, discovery placement & compliant sponsor integrations"
    ),
    PAGE_MANAGEMENT(
        "Social Page Management",
        "Content scheduling, caption copywriting, audience community moderation & growth analytics"
    ),
    CREATIVE_PRODUCTION(
        "Creative & Design Services",
        "High-converting video ads, YouTube thumbnails, carousel infographics & brand kits"
    ),
    COMMUNITY_MANAGEMENT(
        "Community & Customer Care",
        "Telegram group moderation, direct message support, anti-spam filters & live event hosting"
    ),
    BRAND_STRATEGY(
        "Brand Strategy & PR",
        "Market positioning, competitor research, PR press releases & audience segment analysis"
    )
}

enum class ServiceStatus {
    PENDING_APPROVAL,
    APPROVED,
    REJECTED,
    FLAGGED
}

@Entity(tableName = "services")
data class ServiceEntity(
    @PrimaryKey val id: String,
    val providerId: String,
    val providerName: String,
    val providerAgency: String,
    val isProviderVerified: Boolean,
    val title: String,
    val description: String,
    val category: ServiceCategory,
    val platform: PlatformType,
    val price: Double,
    val deliveryDays: Int,
    val deliverables: String, // Comma or newline separated list
    val proofRequirement: String, // Description of proof (e.g., Ads Manager dashboard export, live link)
    val status: ServiceStatus = ServiceStatus.APPROVED,
    val rating: Double = 4.9,
    val reviewCount: Int = 12,
    val complianceGuarantee: String = "100% Platform ToS Compliant — Zero Bots or Metric Spoofing",
    val flagReason: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
