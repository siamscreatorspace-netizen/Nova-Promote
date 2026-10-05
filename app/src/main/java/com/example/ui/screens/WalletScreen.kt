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
import com.example.data.model.TransactionEntity
import com.example.data.model.TransactionType
import com.example.ui.theme.*
import com.example.ui.viewmodel.NovaViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun WalletScreen(
    viewModel: NovaViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val transactions by viewModel.userTransactions.collectAsState()

    var showDepositDialog by remember { mutableStateOf(false) }
    var depositAmountText by remember { mutableStateOf("100") }
    var selectedPaymentGateway by remember { mutableStateOf("Stripe Card Gateway (Visa/Mastercard)") }

    var showWithdrawDialog by remember { mutableStateOf(false) }
    var withdrawAmountText by remember { mutableStateOf("50") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "Secure Escrow Wallet",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Regulatory-compliant payment management & escrow vault",
                color = Slate400,
                fontSize = 12.sp
            )
        }

        // Wallet Balance Fintech Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Slate900),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Slate800, RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "AVAILABLE BALANCE",
                            color = Slate400,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Surface(
                            color = EmeraldVerify.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = EmeraldLight, modifier = Modifier.size(11.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("FDIC-Insured Custody", color = EmeraldLight, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "$${String.format("%.2f", currentUser?.walletBalance ?: 0.0)}",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = Slate800, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Locked in Order Escrow", color = Slate400, fontSize = 11.sp)
                            Text(
                                text = "$${String.format("%.2f", currentUser?.escrowBalance ?: 0.0)}",
                                color = CyanGlow,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Lifetime Transacted", color = Slate400, fontSize = 11.sp)
                            Text(
                                text = "$${String.format("%.2f", (currentUser?.walletBalance ?: 0.0) + (currentUser?.escrowBalance ?: 0.0))}",
                                color = Slate200,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { showDepositDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = Slate950),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("deposit_funds_button")
                        ) {
                            Icon(Icons.Default.AddCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Deposit Funds", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }

                        OutlinedButton(
                            onClick = { showWithdrawDialog = true },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Slate700),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                        ) {
                            Icon(Icons.Default.AccountBalance, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("bKash/Bank Payout", fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Owner VIP Free Credit Card
                    Surface(
                        color = IndigoAccent.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, IndigoAccent.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("👑 App Owner Privilege", color = CyanGlow, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                                Text("Take services free or test escrow flow", color = Slate300, fontSize = 11.sp)
                            }
                            Button(
                                onClick = {
                                    viewModel.depositToWallet(500.0, "App Owner Free VIP Credit")
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = IndigoAccent, contentColor = Color.White),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.testTag("claim_owner_credit_button")
                            ) {
                                Text("+ $500 Free Credit", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Trust & Escrow Guarantee Card
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Slate900),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Slate800, RoundedCornerShape(12.dp))
            ) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = Icons.Default.VerifiedUser,
                        contentDescription = null,
                        tint = CyanGlow,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "How Nova Escrow Safeguards Your Money",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "1. Customer places order: Funds are locked in Nova Escrow.\n2. Provider delivers legitimate campaign proof.\n3. Customer verifies compliance and releases funds.\n4. Disputes are reviewed by human compliance officers.",
                            color = Slate300,
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        item {
            Text(
                text = "Transaction History (${transactions.size})",
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }

        if (transactions.isEmpty()) {
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
                        Text("No recorded transactions yet", color = Slate400, fontSize = 13.sp)
                    }
                }
            }
        } else {
            items(transactions, key = { it.id }) { tx ->
                TransactionRow(tx = tx)
            }
        }
    }

    // Deposit Dialog
    if (showDepositDialog) {
        val gateways = listOf(
            "bKash (বিকাশ) Instant Merchant Checkout",
            "Nagad (নগদ) Mobile Banking Gateway",
            "Stripe Card Gateway (Visa/Mastercard)",
            "Direct ACH / Wire Bank Transfer",
            "Google Pay / Digital Wallet"
        )
        var bkashNumber by remember { mutableStateOf("") }
        var transactionTrxId by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showDepositDialog = false },
            title = {
                Text("Deposit Funds (bKash / Card / Bank)", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    Text(
                        text = "Nova Promote supports direct bKash (বিকাশ), Nagad, and international cards. Funds are deposited into your secure escrow wallet.",
                        color = Slate300,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = depositAmountText,
                        onValueChange = { depositAmountText = it },
                        label = { Text("Deposit Amount ($ USD or BDT equiv)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("deposit_amount_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Select Payment Gateway:", color = Slate400, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(4.dp))

                    gateways.forEach { gw ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp)
                        ) {
                            RadioButton(
                                selected = selectedPaymentGateway == gw,
                                onClick = { selectedPaymentGateway = gw },
                                colors = RadioButtonDefaults.colors(selectedColor = CyanGlow)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = gw, color = Slate200, fontSize = 12.sp)
                        }
                    }

                    if (selectedPaymentGateway.contains("bKash") || selectedPaymentGateway.contains("Nagad")) {
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = bkashNumber,
                            onValueChange = { bkashNumber = it },
                            label = { Text("Sender Mobile Number (017XX...)") },
                            placeholder = { Text("e.g. 01712345678") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = transactionTrxId,
                            onValueChange = { transactionTrxId = it },
                            label = { Text("TrxID / Reference") },
                            placeholder = { Text("e.g. 9J4K2L1") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = depositAmountText.toDoubleOrNull() ?: 50.0
                        viewModel.depositToWallet(amt, selectedPaymentGateway)
                        showDepositDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = Slate950),
                    modifier = Modifier.testTag("confirm_deposit_button")
                ) {
                    Text("Complete Payment", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDepositDialog = false }) {
                    Text("Cancel", color = Slate400)
                }
            },
            containerColor = Slate900
        )
    }

    // Withdraw Dialog
    if (showWithdrawDialog) {
        var payoutMethod by remember { mutableStateOf("bKash (বিকাশ)") }
        var accountInfo by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showWithdrawDialog = false },
            title = {
                Text("Withdraw Revenue (bKash / Bank)", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    Text(
                        text = "Withdraw your earnings or platform commissions directly to your personal or merchant bKash/Nagad/Bank account.",
                        color = Slate300,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = withdrawAmountText,
                        onValueChange = { withdrawAmountText = it },
                        label = { Text("Withdraw Amount ($ USD)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Select Payout Channel:", color = Slate400, fontSize = 11.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = payoutMethod == "bKash (বিকাশ)",
                            onClick = { payoutMethod = "bKash (বিকাশ)" },
                            label = { Text("bKash") }
                        )
                        FilterChip(
                            selected = payoutMethod == "Nagad (নগদ)",
                            onClick = { payoutMethod = "Nagad (নগদ)" },
                            label = { Text("Nagad") }
                        )
                        FilterChip(
                            selected = payoutMethod == "Bank Transfer",
                            onClick = { payoutMethod = "Bank Transfer" },
                            label = { Text("Bank") }
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = accountInfo,
                        onValueChange = { accountInfo = it },
                        label = { Text("bKash Number / Bank Account Info") },
                        placeholder = { Text("e.g. 017XXXXXXXX (Personal/Agent/Merchant)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = withdrawAmountText.toDoubleOrNull() ?: 50.0
                        viewModel.depositToWallet(-amt, "Payout to $payoutMethod: $accountInfo")
                        showWithdrawDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = Slate950)
                ) {
                    Text("Withdraw to bKash", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showWithdrawDialog = false }) {
                    Text("Cancel", color = Slate400)
                }
            },
            containerColor = Slate900
        )
    }
}

@Composable
fun TransactionRow(
    tx: TransactionEntity,
    modifier: Modifier = Modifier
) {
    val (icon, iconColor, sign) = when (tx.type) {
        TransactionType.DEPOSIT -> Triple(Icons.Default.ArrowDownward, EmeraldLight, "+")
        TransactionType.ESCROW_RELEASE -> Triple(Icons.Default.ArrowDownward, EmeraldLight, "+")
        TransactionType.REFUND -> Triple(Icons.Default.Replay, EmeraldLight, "+")
        TransactionType.ESCROW_HOLD -> Triple(Icons.Default.Lock, CyanGlow, "-")
        TransactionType.WITHDRAWAL -> Triple(Icons.Default.ArrowUpward, AmberWarning, "-")
    }

    val dateFormatter = remember { SimpleDateFormat("MMM dd, yyyy • HH:mm", Locale.getDefault()) }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Slate900),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, Slate800, RoundedCornerShape(12.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(iconColor.copy(alpha = 0.15f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tx.type.label,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Text(
                    text = tx.note,
                    color = Slate400,
                    fontSize = 11.sp,
                    maxLines = 1
                )
                Text(
                    text = dateFormatter.format(Date(tx.timestamp)),
                    color = Slate600,
                    fontSize = 10.sp
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "$sign$${String.format("%.2f", tx.amount)}",
                    color = if (sign == "+") EmeraldLight else Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    text = tx.referenceCode.takeLast(10),
                    color = Slate500,
                    fontSize = 9.sp
                )
            }
        }
    }
}
