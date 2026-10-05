package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PlatformType
import com.example.ui.theme.*

@Composable
fun PlatformChip(
    platform: PlatformType,
    modifier: Modifier = Modifier
) {
    val (bg, label) = when (platform) {
        PlatformType.INSTAGRAM -> ColorInstagram to "Instagram"
        PlatformType.FACEBOOK -> ColorFacebook to "Facebook"
        PlatformType.YOUTUBE -> ColorYouTube to "YouTube"
        PlatformType.TIKTOK -> ColorTikTok to "TikTok"
        PlatformType.TELEGRAM -> ColorTelegram to "Telegram"
        PlatformType.MULTI_PLATFORM -> VioletAccent to "Multi-Platform"
    }

    Surface(
        color = bg.copy(alpha = 0.18f),
        contentColor = if (platform == PlatformType.TIKTOK) CyanGlow else bg,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(if (platform == PlatformType.TIKTOK) CyanGlow else bg)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun VerifiedBadge(
    label: String = "Verified Provider",
    modifier: Modifier = Modifier
) {
    Surface(
        color = EmeraldVerify.copy(alpha = 0.15f),
        contentColor = EmeraldLight,
        shape = RoundedCornerShape(6.dp),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Verified,
                contentDescription = "Verified",
                modifier = Modifier.size(13.dp),
                tint = EmeraldLight
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun EscrowProtectionPill(
    modifier: Modifier = Modifier
) {
    Surface(
        color = CyanNeon.copy(alpha = 0.14f),
        contentColor = CyanGlow,
        shape = RoundedCornerShape(6.dp),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Escrow Protected",
                modifier = Modifier.size(12.dp),
                tint = CyanGlow
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Escrow Protected",
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun AntiFraudBanner(
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Slate900
        ),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, Slate700, RoundedCornerShape(12.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = "Legal Guarantee",
                tint = EmeraldVerify,
                modifier = Modifier
                    .size(24.dp)
                    .padding(top = 2.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "Strict Anti-Fraud & Policy Guarantee",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Nova Promote prohibits fake followers, bot engagement, and artificial metrics. All services utilize official ad networks, creator PR, and real human campaigns in full compliance with platform rules.",
                    color = Slate300,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
            }
        }
    }
}
