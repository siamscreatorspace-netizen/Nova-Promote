package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SafetyPoliciesScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedPolicyTab by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Safety, Compliance & Legal",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("policy_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Slate950
                )
            )
        },
        containerColor = Slate950,
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            ScrollableTabRow(
                selectedTabIndex = selectedPolicyTab,
                containerColor = Slate900,
                contentColor = CyanGlow,
                edgePadding = 0.dp,
                divider = {}
            ) {
                Tab(
                    selected = selectedPolicyTab == 0,
                    onClick = { selectedPolicyTab = 0 },
                    text = { Text("Prohibited Services", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedPolicyTab == 1,
                    onClick = { selectedPolicyTab = 1 },
                    text = { Text("Platform Rules", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedPolicyTab == 2,
                    onClick = { selectedPolicyTab = 2 },
                    text = { Text("Terms of Service", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedPolicyTab == 3,
                    onClick = { selectedPolicyTab = 3 },
                    text = { Text("Privacy & Security", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedPolicyTab == 4,
                    onClick = { selectedPolicyTab = 4 },
                    text = { Text("Refund Policy", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            LazyColumn(
                contentPadding = PaddingValues(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                when (selectedPolicyTab) {
                    0 -> {
                        // Prohibited Services
                        item {
                            PolicyCard(
                                title = "1. Strict Zero-Tolerance on Artificial Traffic",
                                icon = Icons.Default.Block,
                                iconTint = RedGlow,
                                content = "Nova Promote strictly prohibits the sale, purchase, brokering, or facilitation of:\n\n" +
                                        "• Fake followers, ghost accounts, or subscriber bots.\n" +
                                        "• Automated engagement (auto-likes, auto-comments, auto-views, mass follow-unfollow).\n" +
                                        "• Artificial click farms, proxy rotation networks, or metric spoofing scripts.\n" +
                                        "• Any mechanism designed to bypass, evade, or trick social media platform algorithms.\n\n" +
                                        "Violators are banned immediately with all pending balances permanently frozen."
                            )
                        }
                        item {
                            PolicyCard(
                                title = "2. Legitimate Marketing Channels Only",
                                icon = Icons.Default.CheckCircle,
                                iconTint = EmeraldLight,
                                content = "All marketplace services must execute promotional activities strictly via approved channels:\n\n" +
                                        "• Official ad platform bidding (Meta Ads Manager, Google/YouTube Ads, TikTok Ads Manager, Telegram Fragment).\n" +
                                        "• Legitimate creator sponsorships with proper disclosure hashtags (#ad, #sponsored).\n" +
                                        "• Content creation, thumbnail graphics, copywriting, and organic creative optimization.\n" +
                                        "• Human community management and customer care."
                            )
                        }
                    }

                    1 -> {
                        // Platform Rules
                        item {
                            PolicyCard(
                                title = "Meta (Facebook & Instagram) Compliance",
                                icon = Icons.Default.Campaign,
                                iconTint = ColorFacebook,
                                content = "Providers operating Meta campaigns must comply with Meta Advertising Standards:\n\n" +
                                        "• Use Meta Business Manager and Authorized Partner tools.\n" +
                                        "• Adhere to restricted category guidelines and transparent disclaimers.\n" +
                                        "• Never use scraping tools or automation extensions."
                            )
                        }
                        item {
                            PolicyCard(
                                title = "YouTube & Google Advertising Policy",
                                icon = Icons.Default.PlayCircle,
                                iconTint = ColorYouTube,
                                content = "Video promotions on YouTube must follow Google Video Ad Guidelines:\n\n" +
                                        "• Promoted via Google Ads TrueView or in-feed discovery format.\n" +
                                        "• No third-party viewer injection or embed spam.\n" +
                                        "• Transparent analytics reporting from official Google Ads dashboards."
                            )
                        }
                        item {
                            PolicyCard(
                                title = "TikTok Commercial Content Policy",
                                icon = Icons.Default.MusicNote,
                                iconTint = ColorTikTok,
                                content = "TikTok influencer and paid campaigns must use:\n\n" +
                                        "• TikTok Spark Ads authorized by creator access codes.\n" +
                                        "• TikTok Commercial Content Disclosure toggled ON.\n" +
                                        "• Organic duets and creator partnerships."
                            )
                        }
                        item {
                            PolicyCard(
                                title = "Telegram Official Advertising Platform",
                                icon = Icons.Default.Send,
                                iconTint = ColorTelegram,
                                content = "Telegram promotions must follow the official Telegram Ad Platform standards:\n\n" +
                                        "• Ad copy must meet the 160-character maximum and tone guidelines.\n" +
                                        "• Placed in public channels with over 1,000 members.\n" +
                                        "• No spam messaging in private direct chats."
                            )
                        }
                    }

                    2 -> {
                        // Terms of Service
                        item {
                            PolicyCard(
                                title = "Advertising Performance Disclaimer",
                                icon = Icons.Default.Info,
                                iconTint = AmberWarning,
                                content = "IMPORTANT NOTICE REGARDING ADVERTISING OUTCOMES:\n\n" +
                                        "Nova Promote and its providers CANNOT guarantee specific metric numbers (e.g. 'gain 5,000 followers' or '10,000 guaranteed views').\n\n" +
                                        "Advertising results, conversion rates, and viewer retention depend entirely on creative resonance, market fit, audience demand, and platform auction dynamics.\n\n" +
                                        "Any service provider claiming guaranteed followers or vanity metrics is in direct violation of Nova Promote rules and will be removed."
                            )
                        }
                        item {
                            PolicyCard(
                                title = "Escrow Custody & Contract Terms",
                                icon = Icons.Default.Handshake,
                                iconTint = CyanGlow,
                                content = "By placing an order on Nova Promote:\n\n" +
                                        "• You deposit funds into the Nova Escrow Vault.\n" +
                                        "• The provider is legally obligated to deliver the exact deliverables listed in the contract.\n" +
                                        "• Funds are released only upon customer confirmation of verified proof or after administrative arbitration."
                            )
                        }
                    }

                    3 -> {
                        // Privacy & Security
                        item {
                            PolicyCard(
                                title = "Zero Password Policy",
                                icon = Icons.Default.VpnKeyOff,
                                iconTint = EmeraldLight,
                                content = "CRITICAL SECURITY MANDATE:\n\n" +
                                        "• Nova Promote and its providers NEVER ask for or store your social media login passwords or 2FA codes.\n" +
                                        "• All agency collaborations are conducted via official Business Manager invites or public asset sharing.\n" +
                                        "• Any provider attempting to solicit client login credentials will be immediately reported to law enforcement and permanently banned."
                            )
                        }
                        item {
                            PolicyCard(
                                title = "Data Encryption & GDPR/CCPA Compliance",
                                icon = Icons.Default.Security,
                                iconTint = CyanGlow,
                                content = "• User information is encrypted in transit and at rest.\n" +
                                        "• Transaction logs and audit trails are preserved for anti-fraud reconciliation.\n" +
                                        "• You may request complete erasure of your marketplace profile at any time."
                            )
                        }
                    }

                    4 -> {
                        // Refund Policy
                        item {
                            PolicyCard(
                                title = "Nova Escrow Refund & Dispute Rules",
                                icon = Icons.Default.Replay,
                                iconTint = EmeraldLight,
                                content = "Our Escrow protection ensures you never pay for non-compliant or unperformed work:\n\n" +
                                        "1. Cancellation Before Work: If a provider fails to accept or start within 48 hours, orders can be cancelled for an immediate 100% wallet refund.\n\n" +
                                        "2. Inadequate Proof: If a provider cannot supply verified campaign dashboard reports or proof links, you may request revisions or open a dispute.\n\n" +
                                        "3. Dispute Arbitration: Nova Compliance Officers review campaign metrics within 24 hours. If deliverables were not met, 100% of escrow funds are refunded to your wallet."
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PolicyCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    content: String,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Slate900),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, Slate800, RoundedCornerShape(12.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = content,
                color = Slate300,
                fontSize = 12.sp,
                lineHeight = 18.sp
            )
        }
    }
}
