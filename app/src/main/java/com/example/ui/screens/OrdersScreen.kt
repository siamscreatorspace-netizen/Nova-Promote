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
import com.example.data.model.OrderEntity
import com.example.data.model.OrderStatus
import com.example.data.model.UserRole
import com.example.ui.components.PlatformChip
import com.example.ui.theme.*
import com.example.ui.viewmodel.NovaViewModel

@Composable
fun OrdersScreen(
    viewModel: NovaViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val customerOrders by viewModel.customerOrders.collectAsState()
    val providerOrders by viewModel.providerOrders.collectAsState()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val isProvider = currentUser?.role == UserRole.PROVIDER || currentUser?.role == UserRole.ADMIN

    // Dialog states
    var proofDialogOrder by remember { mutableStateOf<OrderEntity?>(null) }
    var proofText by remember { mutableStateOf("") }
    var proofLink by remember { mutableStateOf("") }

    var revisionDialogOrder by remember { mutableStateOf<OrderEntity?>(null) }
    var revisionNotes by remember { mutableStateOf("") }

    var disputeDialogOrder by remember { mutableStateOf<OrderEntity?>(null) }
    var disputeReason by remember { mutableStateOf("") }

    var reviewDialogOrder by remember { mutableStateOf<OrderEntity?>(null) }
    var reviewRating by remember { mutableIntStateOf(5) }
    var reviewComment by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Escrow Orders & Campaigns",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Track deliverables, verify campaign analytics, and release escrow payouts safely.",
            color = Slate400,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Tabs: My Purchases vs Provider Deliveries
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Slate900,
            contentColor = CyanGlow,
            divider = {}
        ) {
            Tab(
                selected = selectedTabIndex == 0,
                onClick = { selectedTabIndex = 0 },
                text = { Text("My Purchases (${customerOrders.size})", fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedTabIndex == 1,
                onClick = { selectedTabIndex = 1 },
                text = { Text("Client Deliveries (${providerOrders.size})", fontWeight = FontWeight.Bold) }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        val displayedOrders = if (selectedTabIndex == 0) customerOrders else providerOrders

        if (displayedOrders.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 80.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.ReceiptLong,
                        contentDescription = null,
                        tint = Slate600,
                        modifier = Modifier.size(54.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = if (selectedTabIndex == 0) "No purchase orders yet" else "No client orders yet",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (selectedTabIndex == 0) "Browse the marketplace to book a compliant campaign." else "Orders from clients will appear here.",
                        color = Slate400,
                        fontSize = 12.sp
                    )
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(displayedOrders, key = { it.id }) { order ->
                    OrderItemCard(
                        order = order,
                        isBuyer = selectedTabIndex == 0,
                        onAccept = { viewModel.acceptOrder(order.id) },
                        onSubmitProof = {
                            proofDialogOrder = order
                            proofText = ""
                            proofLink = ""
                        },
                        onReleaseEscrow = { viewModel.approveProofAndReleaseEscrow(order.id) },
                        onRequestRevision = {
                            revisionDialogOrder = order
                            revisionNotes = ""
                        },
                        onRaiseDispute = {
                            disputeDialogOrder = order
                            disputeReason = ""
                        },
                        onOpenReview = {
                            reviewDialogOrder = order
                            reviewRating = 5
                            reviewComment = ""
                        }
                    )
                }
            }
        }
    }

    // Submit Proof Dialog
    if (proofDialogOrder != null) {
        AlertDialog(
            onDismissRequest = { proofDialogOrder = null },
            title = { Text("Submit Legitimate Delivery Proof", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        text = "Provide transparent proof of compliant work (e.g. Meta Ads Manager campaign export summary link, Facebook Ad Library preview link, or live creator post URLs).",
                        color = Slate300,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = proofText,
                        onValueChange = { proofText = it },
                        label = { Text("Proof Summary & Campaign Results") },
                        placeholder = { Text("e.g. Meta Ads live, reached 14,200 targeted humans, 3.4% CTR...") },
                        minLines = 3,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("proof_description_input")
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = proofLink,
                        onValueChange = { proofLink = it },
                        label = { Text("Proof Link / Report URL") },
                        placeholder = { Text("https://storage.novapromote.com/report.pdf") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("proof_link_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        proofDialogOrder?.let { o ->
                            viewModel.submitProof(o.id, proofText, proofLink)
                        }
                        proofDialogOrder = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = Slate950)
                ) {
                    Text("Submit Proof", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { proofDialogOrder = null }) {
                    Text("Cancel", color = Slate400)
                }
            },
            containerColor = Slate900
        )
    }

    // Request Revision Dialog
    if (revisionDialogOrder != null) {
        AlertDialog(
            onDismissRequest = { revisionDialogOrder = null },
            title = { Text("Request Deliverable Revision", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        text = "Specify which contracted deliverables require adjustment according to the original agreement.",
                        color = Slate300,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = revisionNotes,
                        onValueChange = { revisionNotes = it },
                        label = { Text("Revision Notes") },
                        minLines = 3,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("revision_notes_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        revisionDialogOrder?.let { o ->
                            viewModel.requestRevision(o.id, revisionNotes)
                        }
                        revisionDialogOrder = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AmberWarning, contentColor = Slate950)
                ) {
                    Text("Send Request", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { revisionDialogOrder = null }) {
                    Text("Cancel", color = Slate400)
                }
            },
            containerColor = Slate900
        )
    }

    // Dispute Dialog
    if (disputeDialogOrder != null) {
        AlertDialog(
            onDismissRequest = { disputeDialogOrder = null },
            title = { Text("Escalate to Nova Dispute Center", color = RedGlow, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        text = "Funds remain safely locked in Escrow. Nova Compliance Officers review campaign metrics, targeting logs, and policy compliance to arbitrate a fair refund or release.",
                        color = Slate300,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = disputeReason,
                        onValueChange = { disputeReason = it },
                        label = { Text("Reason for Dispute") },
                        placeholder = { Text("e.g. Inadequate proof provided, platform policy violation...") },
                        minLines = 3,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("dispute_reason_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        disputeDialogOrder?.let { o ->
                            viewModel.raiseDispute(o.id, disputeReason)
                        }
                        disputeDialogOrder = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RedProhibited, contentColor = Color.White)
                ) {
                    Text("Submit Dispute", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { disputeDialogOrder = null }) {
                    Text("Cancel", color = Slate400)
                }
            },
            containerColor = Slate900
        )
    }

    // Review Dialog
    if (reviewDialogOrder != null) {
        AlertDialog(
            onDismissRequest = { reviewDialogOrder = null },
            title = { Text("Leave Verified Client Review", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Rate your experience:", color = Slate300, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row {
                        (1..5).forEach { star ->
                            IconButton(
                                onClick = { reviewRating = star },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = if (star <= reviewRating) Icons.Default.Star else Icons.Default.StarBorder,
                                    contentDescription = "$star stars",
                                    tint = if (star <= reviewRating) AmberWarning else Slate600
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = reviewComment,
                        onValueChange = { reviewComment = it },
                        label = { Text("Review Comment") },
                        placeholder = { Text("Share honest feedback regarding campaign performance...") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        reviewDialogOrder?.let { o ->
                            viewModel.submitReview(o.serviceId, o.id, reviewRating, reviewComment)
                        }
                        reviewDialogOrder = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = Slate950)
                ) {
                    Text("Submit Review", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { reviewDialogOrder = null }) {
                    Text("Cancel", color = Slate400)
                }
            },
            containerColor = Slate900
        )
    }
}

@Composable
fun OrderItemCard(
    order: OrderEntity,
    isBuyer: Boolean,
    onAccept: () -> Unit,
    onSubmitProof: () -> Unit,
    onReleaseEscrow: () -> Unit,
    onRequestRevision: () -> Unit,
    onRaiseDispute: () -> Unit,
    onOpenReview: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (statusBg, statusTextColor) = when (order.status) {
        OrderStatus.PLACED -> Slate800 to Slate300
        OrderStatus.IN_PROGRESS -> IndigoAccent.copy(alpha = 0.2f) to IndigoAccent
        OrderStatus.PROOF_SUBMITTED -> CyanNeon.copy(alpha = 0.2f) to CyanGlow
        OrderStatus.REVISION_REQUESTED -> AmberWarning.copy(alpha = 0.2f) to AmberWarning
        OrderStatus.COMPLETED -> EmeraldVerify.copy(alpha = 0.2f) to EmeraldLight
        OrderStatus.DISPUTED -> RedProhibited.copy(alpha = 0.2f) to RedGlow
        OrderStatus.REFUNDED -> Slate700 to Slate300
        OrderStatus.CANCELLED -> Slate700 to Slate400
    }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Slate900),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, Slate800, RoundedCornerShape(14.dp))
            .testTag("order_card_${order.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: ID + Status badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "#${order.id.takeLast(6).uppercase()}",
                    color = Slate400,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Surface(
                    color = statusBg,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = order.status.label,
                        color = statusTextColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Service Title & Platform
            Row(verticalAlignment = Alignment.CenterVertically) {
                PlatformChip(platform = order.platform)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isBuyer) "Provider: ${order.providerName}" else "Client: ${order.buyerName}",
                    color = Slate300,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = order.serviceTitle,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Amount in Escrow
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Target URL: ${order.targetLink}",
                    color = Slate400,
                    fontSize = 11.sp,
                    maxLines = 1
                )
                Text(
                    text = "$${String.format("%.2f", order.amount)} in Escrow",
                    color = CyanGlow,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            // Proof Section if submitted
            if (order.proofDescription.isNotEmpty() || order.proofLink.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    color = Slate850,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "Delivered Campaign Proof:",
                            color = CyanGlow,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (order.proofDescription.isNotEmpty()) {
                            Text(
                                text = order.proofDescription,
                                color = Slate100,
                                fontSize = 11.sp
                            )
                        }
                        if (order.proofLink.isNotEmpty()) {
                            Text(
                                text = "Link: ${order.proofLink}",
                                color = Slate300,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            // Dispute / Revisions if any
            if (order.revisionNotes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Revision Requested: ${order.revisionNotes}",
                    color = AmberWarning,
                    fontSize = 11.sp
                )
            }
            if (order.disputeReason.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Dispute Reason: ${order.disputeReason}",
                    color = RedGlow,
                    fontSize = 11.sp
                )
            }
            if (order.disputeResolution.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Resolution: ${order.disputeResolution}",
                    color = EmeraldLight,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Slate800, thickness = 1.dp)
            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons based on status & role
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!isBuyer) {
                    // Provider actions
                    when (order.status) {
                        OrderStatus.PLACED -> {
                            Button(
                                onClick = onAccept,
                                colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = Slate950),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Accept Order", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                        OrderStatus.IN_PROGRESS, OrderStatus.REVISION_REQUESTED -> {
                            Button(
                                onClick = onSubmitProof,
                                colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = Slate950),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("submit_proof_button")
                            ) {
                                Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Submit Proof", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                        else -> {
                            Text(text = "Awaiting Client Review", color = Slate400, fontSize = 11.sp)
                        }
                    }
                } else {
                    // Customer actions
                    when (order.status) {
                        OrderStatus.PROOF_SUBMITTED -> {
                            OutlinedButton(
                                onClick = onRaiseDispute,
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = RedGlow),
                                border = androidx.compose.foundation.BorderStroke(1.dp, RedProhibited.copy(alpha = 0.5f)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Dispute", fontSize = 12.sp)
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            OutlinedButton(
                                onClick = onRequestRevision,
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = AmberWarning),
                                border = androidx.compose.foundation.BorderStroke(1.dp, AmberWarning.copy(alpha = 0.5f)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Revision", fontSize = 12.sp)
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Button(
                                onClick = onReleaseEscrow,
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldVerify, contentColor = Slate950),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("release_escrow_button")
                            ) {
                                Text("Approve & Release", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                        OrderStatus.COMPLETED -> {
                            Button(
                                onClick = onOpenReview,
                                colors = ButtonDefaults.buttonColors(containerColor = Slate800, contentColor = Color.White),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = AmberWarning, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Leave Review", fontSize = 12.sp)
                            }
                        }
                        OrderStatus.PLACED, OrderStatus.IN_PROGRESS -> {
                            Text("Work In Progress (Escrow Locked)", color = Slate400, fontSize = 11.sp)
                        }
                        else -> {
                            Text(text = order.status.label, color = Slate400, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}
