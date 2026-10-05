package com.example.data.db

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        ServiceEntity::class,
        OrderEntity::class,
        TransactionEntity::class,
        ReviewEntity::class,
        AuditLogEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class NovaDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun serviceDao(): ServiceDao
    abstract fun orderDao(): OrderDao
    abstract fun transactionDao(): TransactionDao
    abstract fun reviewDao(): ReviewDao
    abstract fun auditLogDao(): AuditLogDao

    companion object {
        @Volatile
        private var INSTANCE: NovaDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): NovaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NovaDatabase::class.java,
                    "nova_promote_database"
                )
                .addCallback(NovaDatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class NovaDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateInitialData(database)
                }
            }
        }

        private suspend fun populateInitialData(db: NovaDatabase) {
            val userDao = db.userDao()
            val serviceDao = db.serviceDao()
            val orderDao = db.orderDao()
            val transactionDao = db.transactionDao()
            val reviewDao = db.reviewDao()
            val auditLogDao = db.auditLogDao()

            // 1. Users
            val customer = UserEntity(
                id = "usr_customer_1",
                name = "Alex Morgan (Brand Founder)",
                email = "alex@luminafashion.com",
                phone = "+1 (555) 234-8901",
                role = UserRole.CUSTOMER,
                walletBalance = 380.0,
                escrowBalance = 120.0,
                isVerified = true,
                providerBio = "E-commerce founder scaling ethically.",
                completedOrdersCount = 4
            )

            val provider1 = UserEntity(
                id = "usr_provider_1",
                name = "Apex Growth Agency",
                email = "campaigns@apexgrowth.agency",
                phone = "+1 (555) 876-5432",
                role = UserRole.PROVIDER,
                walletBalance = 1650.0,
                escrowBalance = 240.0,
                isVerified = true,
                agencyName = "Apex Performance LLC (Meta & Google Certified)",
                providerBio = "Certified Meta & Google Media Buyers. We specialize in legally compliant paid ads and influencer partnerships with zero synthetic traffic.",
                rating = 4.96,
                completedOrdersCount = 48
            )

            val provider2 = UserEntity(
                id = "usr_provider_2",
                name = "Elena Rostova",
                email = "elena@creatorpr.co",
                phone = "+1 (555) 432-9087",
                role = UserRole.PROVIDER,
                walletBalance = 920.0,
                escrowBalance = 0.0,
                isVerified = true,
                agencyName = "Rostova Creative PR",
                providerBio = "Authentic creator outreach & TikTok Spark Ad curation. Helping brands collaborate with verified, engaged humans.",
                rating = 4.91,
                completedOrdersCount = 31
            )

            val admin = UserEntity(
                id = "usr_admin_1",
                name = "Nova Compliance Officer",
                email = "compliance@novapromote.com",
                phone = "+1 (800) 555-NOVA",
                role = UserRole.ADMIN,
                walletBalance = 0.0,
                escrowBalance = 0.0,
                isVerified = true,
                providerBio = "Nova Promote Legal & Security Operations Team."
            )

            userDao.insertUser(customer)
            userDao.insertUser(provider1)
            userDao.insertUser(provider2)
            userDao.insertUser(admin)

            // 2. Verified Compliant Services
            val services = listOf(
                ServiceEntity(
                    id = "srv_meta_ads_1",
                    providerId = "usr_provider_1",
                    providerName = "Apex Growth Agency",
                    providerAgency = "Meta Certified Partner",
                    isProviderVerified = true,
                    title = "Meta Ads Manager Compliant Campaign Setup & ROI Scaling",
                    description = "Full-funnel official Instagram & Facebook ad campaign setup using official Meta Ads Manager. Includes conversion pixel auditing, target audience building (demographic, interests, lookalikes), 3 ad variations copywriting, and automated spend pacing. All traffic is 100% genuine human reach delivered via Meta's auction.",
                    category = ServiceCategory.AD_CAMPAIGN_MANAGEMENT,
                    platform = PlatformType.INSTAGRAM,
                    price = 120.0,
                    deliveryDays = 4,
                    deliverables = "• Meta Ads Manager Campaign Setup\n• Conversion Pixel & CAPI verification\n• 3 Custom High-Converting Ad Creatives\n• In-depth Demographic & Behavioral Targeting\n• Live Meta Ads Dashboard Export & Performance Report",
                    proofRequirement = "Exported Meta Ads Manager campaign summary PDF + live ad preview links from Facebook Ad Library",
                    status = ServiceStatus.APPROVED,
                    rating = 4.98,
                    reviewCount = 34
                ),
                ServiceEntity(
                    id = "srv_yt_trueview_2",
                    providerId = "usr_provider_1",
                    providerName = "Apex Growth Agency",
                    providerAgency = "Google Partner",
                    isProviderVerified = true,
                    title = "YouTube Official TrueView / In-Feed Video Ad Management",
                    description = "Professional Google Ads setup for legitimate YouTube video discovery. We run certified In-Feed and Skippable Video Ads to target exact audience keywords, competitor channels, and relevant topics. Guaranteed compliance with YouTube Advertiser Guidelines.",
                    category = ServiceCategory.VIDEO_PROMOTION,
                    platform = PlatformType.YOUTUBE,
                    price = 150.0,
                    deliveryDays = 5,
                    deliverables = "• Google Ads Video Campaign Architecture\n• Intent Keyword & Topic Placement Strategy\n• Thumbnail & Hook Copy Optimization\n• Official Google Ads Campaign Analytics CSV & Report\n• Audience Retention Insights",
                    proofRequirement = "Google Ads Manager verified performance report (impressions, watch time, legitimate view rate) with Campaign ID",
                    status = ServiceStatus.APPROVED,
                    rating = 4.95,
                    reviewCount = 22
                ),
                ServiceEntity(
                    id = "srv_tiktok_spark_3",
                    providerId = "usr_provider_2",
                    providerName = "Elena Rostova",
                    providerAgency = "Rostova Creative PR",
                    isProviderVerified = true,
                    title = "TikTok Spark Ads & Compliant Creator Outreach Strategy",
                    description = "Connect with 5 verified TikTok creators in your niche for authentic product showcases or duet integrations. We set up TikTok Spark Ads authorization codes to legitimately boost approved creator posts through TikTok Ads Manager.",
                    category = ServiceCategory.INFLUENCER_MARKETING,
                    platform = PlatformType.TIKTOK,
                    price = 180.0,
                    deliveryDays = 6,
                    deliverables = "• Outreach to 5 Vetted TikTok Creators\n• Product Placement Briefing & Script Alignment\n• Spark Ads Auth Code Generation & Ad Setup\n• Real Engagement Sentiment Monitoring\n• Verified Creator Deliverable Verification Report",
                    proofRequirement = "Live published creator video links with #ad / #sponsored disclosures + TikTok Ads Manager verification screenshot",
                    status = ServiceStatus.APPROVED,
                    rating = 4.92,
                    reviewCount = 18
                ),
                ServiceEntity(
                    id = "srv_telegram_official_4",
                    providerId = "usr_provider_1",
                    providerName = "Apex Growth Agency",
                    providerAgency = "Apex Performance LLC",
                    isProviderVerified = true,
                    title = "Telegram Official Sponsored Posts Setup & Channel PR",
                    description = "Legitimate Telegram advertising using the official Telegram Ad Platform (Fragment). We draft compliant ad copy meeting Telegram's strict length and language policies, target specific public channels with 1000+ members, and track CPM bids.",
                    category = ServiceCategory.CONTENT_PROMOTION,
                    platform = PlatformType.TELEGRAM,
                    price = 95.0,
                    deliveryDays = 3,
                    deliverables = "• Telegram Ad Platform Compliant Copy\n• Target Channel Quality & Bot-Audit Filtering\n• CPM Bidding & Budget Management\n• Official Telegram Ad Dashboard Statistics Export\n• Channel CTR & Subscriber Quality Analysis",
                    proofRequirement = "Telegram Ad Platform official dashboard report showing live impressions and public channel IDs",
                    status = ServiceStatus.APPROVED,
                    rating = 4.88,
                    reviewCount = 14
                ),
                ServiceEntity(
                    id = "srv_design_carousel_5",
                    providerId = "usr_provider_2",
                    providerName = "Elena Rostova",
                    providerAgency = "Rostova Creative PR",
                    isProviderVerified = true,
                    title = "High-Converting Social Media Design Kit & Ad Creatives",
                    description = "Custom design of 5 carousel slide decks and 3 high-impact promotional video thumbnails crafted specifically to stop scrolling organically. Built in accordance with Meta 20% text guidance and YouTube 16:9 thumbnail contrast rules.",
                    category = ServiceCategory.CREATIVE_PRODUCTION,
                    platform = PlatformType.MULTI_PLATFORM,
                    price = 85.0,
                    deliveryDays = 2,
                    deliverables = "• 5 High-Retention Instagram/LinkedIn Carousel Decks\n• 3 High-CTR YouTube Video Thumbnails\n• Editable Figma/Canva Source Files\n• Copywriting & Call-to-Action Variations",
                    proofRequirement = "Direct download link to high-res assets + editable project source file link",
                    status = ServiceStatus.APPROVED,
                    rating = 5.0,
                    reviewCount = 29
                ),
                ServiceEntity(
                    id = "srv_page_mgmt_6",
                    providerId = "usr_provider_2",
                    providerName = "Elena Rostova",
                    providerAgency = "Rostova Creative PR",
                    isProviderVerified = true,
                    title = "Compliant Social Page Management & Real Community Care",
                    description = "14 days of active social media management. We schedule your pre-approved posts, write compelling captions with compliant hashtags, respond to genuine customer inquiries within 2 hours, and actively moderate spam/bot comments without using risky automation tools.",
                    category = ServiceCategory.PAGE_MANAGEMENT,
                    platform = PlatformType.INSTAGRAM,
                    price = 220.0,
                    deliveryDays = 14,
                    deliverables = "• 14-Day Content Publishing Calendar\n• Human Community Moderation (Zero bot scripts)\n• DM Triage & Customer Service Replies\n• Weekly Organic Growth & Sentiment PDF Report",
                    proofRequirement = "Bi-weekly activity log showing live published posts and moderation log summary",
                    status = ServiceStatus.APPROVED,
                    rating = 4.94,
                    reviewCount = 19
                )
            )

            services.forEach { serviceDao.insertService(it) }

            // 3. Initial Active Order (Alex ordered Meta Ads from Apex)
            val order1 = OrderEntity(
                id = "ord_nov_101",
                serviceId = "srv_meta_ads_1",
                serviceTitle = "Meta Ads Manager Compliant Campaign Setup & ROI Scaling",
                category = ServiceCategory.AD_CAMPAIGN_MANAGEMENT,
                platform = PlatformType.INSTAGRAM,
                buyerId = "usr_customer_1",
                buyerName = "Alex Morgan",
                providerId = "usr_provider_1",
                providerName = "Apex Growth Agency",
                amount = 120.0,
                status = OrderStatus.IN_PROGRESS,
                requirementsText = "Promote our ethical sustainable apparel line. Target females 22-40 in US and UK interested in eco-fashion. Website: luminafashion.com",
                targetLink = "https://luminafashion.com/summer-collection",
                complianceChecked = true,
                createdAt = System.currentTimeMillis() - (86400000L * 2),
                updatedAt = System.currentTimeMillis() - (86400000L * 1)
            )

            val order2 = OrderEntity(
                id = "ord_nov_100",
                serviceId = "srv_design_carousel_5",
                serviceTitle = "High-Converting Social Media Design Kit & Ad Creatives",
                category = ServiceCategory.CREATIVE_PRODUCTION,
                platform = PlatformType.MULTI_PLATFORM,
                buyerId = "usr_customer_1",
                buyerName = "Alex Morgan",
                providerId = "usr_provider_2",
                providerName = "Elena Rostova",
                amount = 85.0,
                status = OrderStatus.COMPLETED,
                requirementsText = "Need carousel designs for our biodegradable fabric launch.",
                targetLink = "https://luminafashion.com/about-materials",
                proofDescription = "All 5 carousels designed and exported in 4K resolution. Editable Figma link included in delivery.",
                proofLink = "https://storage.novapromote.com/deliveries/lumina_carousels_final.zip",
                complianceChecked = true,
                createdAt = System.currentTimeMillis() - (86400000L * 8),
                updatedAt = System.currentTimeMillis() - (86400000L * 6)
            )

            orderDao.insertOrder(order1)
            orderDao.insertOrder(order2)

            // 4. Transactions
            transactionDao.insertTransaction(
                TransactionEntity(
                    id = "tx_seed_1",
                    userId = "usr_customer_1",
                    type = TransactionType.DEPOSIT,
                    amount = 500.0,
                    status = TransactionStatus.COMPLETED,
                    paymentGateway = "Stripe Verified Card Payment",
                    referenceCode = "CH_9824_AUTH_SECURE",
                    note = "Funds added via Visa ending in 4242"
                )
            )

            transactionDao.insertTransaction(
                TransactionEntity(
                    id = "tx_seed_2",
                    userId = "usr_customer_1",
                    orderId = "ord_nov_101",
                    type = TransactionType.ESCROW_HOLD,
                    amount = 120.0,
                    status = TransactionStatus.HELD_IN_ESCROW,
                    paymentGateway = "Nova Escrow Vault",
                    referenceCode = "ESCROW_LOCK_101",
                    note = "Locked for Meta Ads Campaign order #ord_nov_101"
                )
            )

            // 5. Initial Review
            reviewDao.insertReview(
                ReviewEntity(
                    id = "rev_1",
                    serviceId = "srv_meta_ads_1",
                    orderId = "ord_past_001",
                    buyerId = "usr_customer_1",
                    buyerName = "Alex Morgan",
                    rating = 5,
                    comment = "Super professional! They walked me through their Meta Business Manager screen share and set up real conversion tracking. Zero fake metrics, genuine targeted clicks from day 2."
                )
            )

            // 6. Audit Log
            auditLogDao.insertLog(
                AuditLogEntity(
                    id = "audit_001",
                    eventType = "SYSTEM_INITIALIZATION",
                    details = "Nova Promote Anti-Fraud Engine activated. Keyword blacklist loaded with 142 prohibited engagement phrases.",
                    initiatedBy = "SYSTEM",
                    severity = AuditSeverity.INFO
                )
            )
        }
    }
}
