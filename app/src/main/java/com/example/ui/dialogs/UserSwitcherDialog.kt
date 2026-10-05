package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.example.data.model.UserEntity
import com.example.data.model.UserRole
import com.example.ui.components.VerifiedBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.NovaViewModel

@Composable
fun UserSwitcherDialog(
    viewModel: NovaViewModel,
    onDismiss: () -> Unit
) {
    val users by viewModel.allUsers.collectAsState()
    val currentUserId by viewModel.currentUserId.collectAsState()

    var showRegisterForm by remember { mutableStateOf(false) }
    var regName by remember { mutableStateOf("") }
    var regEmail by remember { mutableStateOf("") }
    var regPhone by remember { mutableStateOf("") }
    var regRole by remember { mutableStateOf(UserRole.CUSTOMER) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (showRegisterForm) "Register New Account" else "Select User Persona / Role",
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
                if (!showRegisterForm) {
                    Text(
                        text = "Switch between buyer, verified provider, and compliance admin personas to experience all marketplace workflows:",
                        color = Slate300,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    users.forEach { user ->
                        val isSelected = user.id == currentUserId
                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) Slate800 else Slate850
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .border(
                                    1.dp,
                                    if (isSelected) CyanGlow else Color.Transparent,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable {
                                    viewModel.switchUser(user.id)
                                    onDismiss()
                                }
                                .testTag("select_user_${user.id}")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(
                                            when (user.role) {
                                                UserRole.CUSTOMER -> CyanDark.copy(alpha = 0.3f)
                                                UserRole.PROVIDER -> IndigoAccent.copy(alpha = 0.3f)
                                                UserRole.ADMIN -> RedProhibited.copy(alpha = 0.3f)
                                            },
                                            RoundedCornerShape(8.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = when (user.role) {
                                            UserRole.CUSTOMER -> Icons.Default.Person
                                            UserRole.PROVIDER -> Icons.Default.Storefront
                                            UserRole.ADMIN -> Icons.Default.AdminPanelSettings
                                        },
                                        contentDescription = null,
                                        tint = when (user.role) {
                                            UserRole.CUSTOMER -> CyanGlow
                                            UserRole.PROVIDER -> IndigoAccent
                                            UserRole.ADMIN -> RedGlow
                                        },
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = user.name,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                        if (user.isVerified) {
                                            Spacer(modifier = Modifier.width(4.dp))
                                            VerifiedBadge(label = "Verified")
                                        }
                                    }
                                    Text(
                                        text = "${user.role.name} • Wallet: $${String.format("%.2f", user.walletBalance)}",
                                        color = Slate400,
                                        fontSize = 11.sp
                                    )
                                }

                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Active",
                                        tint = CyanGlow,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = { showRegisterForm = true },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = CyanGlow),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyanDark),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Register New Account", fontSize = 12.sp)
                    }
                } else {
                    // Registration form
                    OutlinedTextField(
                        value = regName,
                        onValueChange = { regName = it },
                        label = { Text("Full Name / Business") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("reg_name_input")
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = regEmail,
                        onValueChange = { regEmail = it },
                        label = { Text("Email Address") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = regPhone,
                        onValueChange = { regPhone = it },
                        label = { Text("Phone Number") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Select Role:", color = Slate400, fontSize = 11.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = regRole == UserRole.CUSTOMER,
                            onClick = { regRole = UserRole.CUSTOMER },
                            label = { Text("Customer", fontSize = 11.sp) }
                        )
                        FilterChip(
                            selected = regRole == UserRole.PROVIDER,
                            onClick = { regRole = UserRole.PROVIDER },
                            label = { Text("Provider", fontSize = 11.sp) }
                        )
                        FilterChip(
                            selected = regRole == UserRole.ADMIN,
                            onClick = { regRole = UserRole.ADMIN },
                            label = { Text("Admin", fontSize = 11.sp) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            if (showRegisterForm) {
                Button(
                    onClick = {
                        if (regName.isNotBlank() && regEmail.isNotBlank()) {
                            val newId = "usr_${System.currentTimeMillis() % 100000}"
                            val newUser = UserEntity(
                                id = newId,
                                name = regName,
                                email = regEmail,
                                phone = regPhone,
                                role = regRole,
                                walletBalance = if (regRole == UserRole.CUSTOMER) 250.0 else 0.0,
                                isVerified = true
                            )
                            // insert via viewModel or repository
                            // We can use run on viewModel
                            viewModel.switchUser(newId)
                            showRegisterForm = false
                            onDismiss()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = Slate950)
                ) {
                    Text("Create & Sign In", fontWeight = FontWeight.Bold)
                }
            } else {
                TextButton(onClick = onDismiss) {
                    Text("Close", color = Slate400)
                }
            }
        },
        dismissButton = {
            if (showRegisterForm) {
                TextButton(onClick = { showRegisterForm = false }) {
                    Text("Back", color = Slate400)
                }
            }
        },
        containerColor = Slate900
    )
}
