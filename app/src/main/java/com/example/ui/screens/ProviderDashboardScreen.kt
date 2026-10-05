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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProviderDashboardScreen(
    viewModel: NovaViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val allServices by viewModel.allServicesForAdmin.collectAsState()
    val providerServices = allServices.filter { it.providerId == currentUser?.id }

    var showCreateServiceDialog by remember { mutableStateOf(false) }
    val form by viewModel.createForm.collectAsState()

    // Real-time compliance check on form
    val prohibitedDetected = remember(form.title, form.description, form.deliverables) {
        val combined = "${form.title} ${form.description} ${form.deliverables}".lowercase()
        val bannedList = listOf("fake", "bot", "auto-follow", "autolike", "scrape", "guaranteed followers", "synthetic", "click farm")
        bannedList.firstOrNull { combined.contains(it) }
    }

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
                        text = "Provider Studio",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Monetize legitimate marketing skills & manage campaigns",
                        color = Slate400,
                        fontSize = 12.sp
                    )
                }

                Button(
                    onClick = { showCreateServiceDialog = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyanNeon,
                        contentColor = Slate950
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("create_service_button")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("New Service", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        // Metrics Grid
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
                        Text("Available Earnings", color = Slate400, fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$${String.format("%.2f", currentUser?.walletBalance ?: 0.0)}",
                            color = EmeraldLight,
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
                        Text("Active In Escrow", color = Slate400, fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$${String.format("%.2f", currentUser?.escrowBalance ?: 0.0)}",
                            color = CyanGlow,
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
                        Text("Client Rating", color = Slate400, fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = AmberWarning, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "${currentUser?.rating ?: 5.0}",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Provider Credentials Card
        item {
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
                        Text(
                            text = currentUser?.name ?: "Provider",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        if (currentUser?.isVerified == true) {
                            VerifiedBadge(label = "KYC Verified Partner")
                        }
                    }
                    if (!currentUser?.agencyName.isNullOrBlank()) {
                        Text(
                            text = currentUser?.agencyName ?: "",
                            color = IndigoAccent,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = currentUser?.providerBio.takeIf { !it.isNullOrBlank() }
                            ?: "Compliant social advertising media buyer and content marketing strategist.",
                        color = Slate300,
                        fontSize = 12.sp
                    )
                }
            }
        }

        item {
            Text(
                text = "My Published Services (${providerServices.size})",
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }

        if (providerServices.isEmpty()) {
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
                        Icon(Icons.Default.Campaign, contentDescription = null, tint = Slate600, modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("You haven't created any services yet", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Click '+ New Service' to publish an advertising or creator campaign offering.", color = Slate400, fontSize = 12.sp)
                    }
                }
            }
        } else {
            items(providerServices, key = { it.id }) { service ->
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

                            val (statusColor, statusText) = when (service.status) {
                                ServiceStatus.APPROVED -> EmeraldLight to "Approved & Live"
                                ServiceStatus.PENDING_APPROVAL -> AmberWarning to "In Compliance Review"
                                ServiceStatus.FLAGGED -> RedGlow to "Policy Flagged"
                                ServiceStatus.REJECTED -> RedGlow to "Rejected"
                            }
                            Surface(
                                color = statusColor.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = statusText,
                                    color = statusColor,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = service.title,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Turnaround: ${service.deliveryDays} days",
                                color = Slate400,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "$${String.format("%.2f", service.price)}",
                                color = CyanGlow,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            }
        }
    }

    // Create Service Dialog
    if (showCreateServiceDialog) {
        AlertDialog(
            onDismissRequest = { showCreateServiceDialog = false },
            title = {
                Text(
                    text = "Create Compliant Service",
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Text(
                        text = "All services must follow Meta, Google, TikTok, and Telegram advertising rules. Fake engagement, bots, and metric inflation are strictly prohibited.",
                        color = Slate300,
                        fontSize = 11.sp
                    )

                    if (prohibitedDetected != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            color = RedProhibited.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = RedGlow, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Prohibited keyword detected: '$prohibitedDetected'. Services promising synthetic metrics will be blocked.",
                                    color = RedGlow,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = form.title,
                        onValueChange = { viewModel.createForm.value = form.copy(title = it) },
                        label = { Text("Service Title") },
                        placeholder = { Text("e.g. Official Meta Ads Campaign Setup & Audience Audit") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("new_service_title_input")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = form.priceString,
                        onValueChange = { viewModel.createForm.value = form.copy(priceString = it) },
                        label = { Text("Price ($ USD)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("new_service_price_input")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = form.deliveryDaysString,
                        onValueChange = { viewModel.createForm.value = form.copy(deliveryDaysString = it) },
                        label = { Text("Delivery Time (Days)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = form.description,
                        onValueChange = { viewModel.createForm.value = form.copy(description = it) },
                        label = { Text("Description & Methodology") },
                        placeholder = { Text("Describe the advertising strategy, ad channels, or creator outreach...") },
                        minLines = 2,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = form.proofRequirement,
                        onValueChange = { viewModel.createForm.value = form.copy(proofRequirement = it) },
                        label = { Text("Proof of Completed Work to Provide") },
                        placeholder = { Text("e.g. Meta Ads Manager campaign summary export PDF") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.submitNewService(
                            onSuccess = { showCreateServiceDialog = false }
                        )
                    },
                    enabled = prohibitedDetected == null && form.title.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = Slate950),
                    modifier = Modifier.testTag("submit_new_service_button")
                ) {
                    Text("Submit for Verification", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateServiceDialog = false }) {
                    Text("Cancel", color = Slate400)
                }
            },
            containerColor = Slate900
        )
    }
}
