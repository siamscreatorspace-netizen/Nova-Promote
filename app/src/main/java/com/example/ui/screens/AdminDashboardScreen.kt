package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.example.data.model.*
import com.example.ui.components.PlatformChip
import com.example.ui.components.VerifiedBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.NovaViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AdminDashboardScreen(
    viewModel: NovaViewModel,
    modifier: Modifier = Modifier
) {
    val allOrders by viewModel.allOrdersForAdmin.collectAsState()
    val allServices by viewModel.allServicesForAdmin.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()
    val auditLogs by viewModel.auditLogs.collectAsState()

    var selectedAdminTab by remember { mutableIntStateOf(0) }

    // Dispute resolution dialog state
    var resolvingOrder by remember { mutableStateOf<OrderEntity?>(null) }
    var resolutionNotes by remember { mutableStateOf("") }

    val disputedOrders = allOrders.filter { it.status == OrderStatus.DISPUTED }
    val pendingServices = allServices.filter { it.status == ServiceStatus.PENDING_APPROVAL || it.status == ServiceStatus.FLAGGED }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Nova Compliance & Admin Ops",
                        color = Color.White,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Legal oversight, anti-fraud enforcement & dispute resolution",
                        color = Slate400,
                        fontSize = 12.sp
                    )
                }
                Surface(
                    color = RedProhibited.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "ADMIN ACTIVE",
                        color = RedGlow,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }
        }

        // Summary Metric Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Slate900)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Open Disputes", color = Slate400, fontSize = 11.sp)
                        Text(
                            text = "${disputedOrders.size}",
                            color = if (disputedOrders.isEmpty()) EmeraldLight else RedGlow,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Slate900)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Pending Review", color = Slate400, fontSize = 11.sp)
                        Text(
                            text = "${pendingServices.size}",
                            color = if (pendingServices.isEmpty()) EmeraldLight else AmberWarning,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Slate900)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Audit Incidents", color = Slate400, fontSize = 11.sp)
                        Text(
                            text = "${auditLogs.size}",
                            color = CyanGlow,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // App Owner bKash Payout & Free Service Hub
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Slate850),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CyanGlow.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Stars, contentDescription = null, tint = CyanGlow, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("App Owner Revenue & Free Access", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Surface(
                            color = EmeraldVerify.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text("OWNER ACCESS", color = EmeraldLight, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "সব কাস্টমারদের অর্ডার থেকে প্ল্যাটফর্ম ফি সরাসরি মালিকের রেভিনিউ ফান্ডে জমা হয়। আপনি ওনার হিসেবে নিজের বিকাশে টাকা তুলতে পারবেন এবং যেকোনো সার্ভিস ফ্রিতে নেওয়ার জন্য ফ্রি ক্রেডিট ব্যবহার করতে পারবেন।",
                        color = Slate300,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.depositToWallet(1000.0, "App Owner Unlimited Free Pass")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = Slate950),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Free $1,000 Credit", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }

                        OutlinedButton(
                            onClick = {
                                viewModel.depositToWallet(-250.0, "Admin Commission Payout to bKash: 017XXXXXXXX")
                            },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = EmeraldLight),
                            border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldLight),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Send to bKash", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Sub Tabs
        item {
            TabRow(
                selectedTabIndex = selectedAdminTab,
                containerColor = Slate900,
                contentColor = CyanGlow,
                divider = {}
            ) {
                Tab(
                    selected = selectedAdminTab == 0,
                    onClick = { selectedAdminTab = 0 },
                    text = { Text("Disputes (${disputedOrders.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedAdminTab == 1,
                    onClick = { selectedAdminTab = 1 },
                    text = { Text("Services (${allServices.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedAdminTab == 2,
                    onClick = { selectedAdminTab = 2 },
                    text = { Text("Users & KYC", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedAdminTab == 3,
                    onClick = { selectedAdminTab = 3 },
                    text = { Text("Audit Trail", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
            }
        }

        when (selectedAdminTab) {
            0 -> {
                // Disputes Tab
                if (disputedOrders.isEmpty()) {
                    item {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Slate900),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = EmeraldLight, modifier = Modifier.size(36.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("No active disputes", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("All platform orders are running smoothly.", color = Slate400, fontSize = 12.sp)
                            }
                        }
                    }
                } else {
                    items(disputedOrders, key = { it.id }) { order ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Slate900),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, RedProhibited.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Disputed Order #${order.id}",
                                        color = RedGlow,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "$${String.format("%.2f", order.amount)} in Escrow",
                                        color = CyanGlow,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Text("Buyer: ${order.buyerName} | Provider: ${order.providerName}", color = Slate300, fontSize = 12.sp)
                                Spacer(modifier = Modifier.height(6.dp))
                                Surface(
                                    color = Slate800,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text("Buyer Claim:", color = AmberWarning, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        Text(order.disputeReason, color = Slate100, fontSize = 12.sp)
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    Button(
                                        onClick = {
                                            resolvingOrder = order
                                            resolutionNotes = ""
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = Slate950),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.testTag("arbitrate_dispute_button")
                                    ) {
                                        Text("Arbitrate Dispute", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            1 -> {
                // Services Moderation Tab
                items(allServices, key = { it.id }) { service ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Slate900),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Slate800, RoundedCornerShape(12.dp))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                PlatformChip(platform = service.platform)
                                Surface(
                                    color = when (service.status) {
                                        ServiceStatus.APPROVED -> EmeraldVerify.copy(alpha = 0.2f)
                                        ServiceStatus.PENDING_APPROVAL -> AmberWarning.copy(alpha = 0.2f)
                                        ServiceStatus.FLAGGED, ServiceStatus.REJECTED -> RedProhibited.copy(alpha = 0.2f)
                                    },
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = service.status.name,
                                        color = when (service.status) {
                                            ServiceStatus.APPROVED -> EmeraldLight
                                            ServiceStatus.PENDING_APPROVAL -> AmberWarning
                                            ServiceStatus.FLAGGED, ServiceStatus.REJECTED -> RedGlow
                                        },
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(service.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("By: ${service.providerName} (${service.providerAgency})", color = Slate400, fontSize = 12.sp)

                            if (service.flagReason.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("Flag Reason: ${service.flagReason}", color = RedGlow, fontSize = 11.sp)
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                if (service.status != ServiceStatus.APPROVED) {
                                    Button(
                                        onClick = { viewModel.adminModerateService(service.id, ServiceStatus.APPROVED) },
                                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldVerify, contentColor = Slate950),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Approve", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                }
                                if (service.status != ServiceStatus.REJECTED) {
                                    OutlinedButton(
                                        onClick = { viewModel.adminModerateService(service.id, ServiceStatus.REJECTED, "Platform policy violation") },
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = RedGlow),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Reject", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            2 -> {
                // Users & KYC
                items(allUsers, key = { it.id }) { user ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Slate900),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Slate800, RoundedCornerShape(12.dp))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(user.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    if (user.isVerified) {
                                        VerifiedBadge(label = "KYC Verified")
                                    }
                                }
                                Text("${user.role.name} • ${user.email}", color = Slate400, fontSize = 11.sp)
                                Text("Wallet: $${String.format("%.2f", user.walletBalance)} | Escrow: $${String.format("%.2f", user.escrowBalance)}", color = Slate300, fontSize = 11.sp)
                            }

                            Switch(
                                checked = user.isVerified,
                                onCheckedChange = { checked ->
                                    viewModel.adminToggleVerification(user.id, checked)
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = EmeraldLight,
                                    checkedTrackColor = EmeraldVerify.copy(alpha = 0.4f)
                                )
                            )
                        }
                    }
                }
            }

            3 -> {
                // Audit Trail
                val dateFormatter = SimpleDateFormat("MMM dd HH:mm:ss", Locale.getDefault())
                items(auditLogs, key = { it.id }) { log ->
                    Card(
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Slate900),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = when (log.severity) {
                                    AuditSeverity.SECURITY_ALERT -> Icons.Default.Warning
                                    AuditSeverity.WARNING -> Icons.Default.Info
                                    AuditSeverity.INFO -> Icons.Default.CheckCircle
                                },
                                contentDescription = null,
                                tint = when (log.severity) {
                                    AuditSeverity.SECURITY_ALERT -> RedGlow
                                    AuditSeverity.WARNING -> AmberWarning
                                    AuditSeverity.INFO -> CyanGlow
                                },
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(log.eventType, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text(log.details, color = Slate300, fontSize = 11.sp)
                                Text(dateFormatter.format(Date(log.timestamp)), color = Slate500, fontSize = 9.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    // Arbitration Dialog
    if (resolvingOrder != null) {
        AlertDialog(
            onDismissRequest = { resolvingOrder = null },
            title = {
                Text("Arbitrate Order #${resolvingOrder?.id}", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    Text(
                        text = "Amount: $${resolvingOrder?.amount} held in Escrow.\nDispute: ${resolvingOrder?.disputeReason}",
                        color = Slate300,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = resolutionNotes,
                        onValueChange = { resolutionNotes = it },
                        label = { Text("Official Arbitration Notes") },
                        placeholder = { Text("e.g. Campaign delivered 100% compliant proof. Releasing to provider.") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Row {
                    OutlinedButton(
                        onClick = {
                            resolvingOrder?.let { o ->
                                viewModel.adminResolveDispute(o.id, refundBuyer = true, notes = resolutionNotes.ifEmpty { "Buyer refunded in full." })
                            }
                            resolvingOrder = null
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = RedGlow)
                    ) {
                        Text("Refund Buyer")
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Button(
                        onClick = {
                            resolvingOrder?.let { o ->
                                viewModel.adminResolveDispute(o.id, refundBuyer = false, notes = resolutionNotes.ifEmpty { "Proof validated. Released to provider." })
                            }
                            resolvingOrder = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldVerify, contentColor = Slate950)
                    ) {
                        Text("Release Payout")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { resolvingOrder = null }) {
                    Text("Cancel", color = Slate400)
                }
            },
            containerColor = Slate900
        )
    }
}
