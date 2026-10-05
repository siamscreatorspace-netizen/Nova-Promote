package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.example.data.db.NovaDatabase
import com.example.data.model.ServiceEntity
import com.example.data.model.UserRole
import com.example.data.repository.NovaRepository
import com.example.ui.dialogs.UserSwitcherDialog
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.NovaViewModel

enum class MainNavScreen(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    MARKETPLACE("Market", Icons.Default.Storefront),
    ORDERS("Orders", Icons.Default.ReceiptLong),
    PROVIDER_STUDIO("Studio", Icons.Default.BusinessCenter),
    WALLET("Wallet", Icons.Default.AccountBalanceWallet),
    POLICIES("Safety", Icons.Default.GppGood),
    ADMIN("Admin", Icons.Default.AdminPanelSettings)
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = NovaDatabase.getDatabase(applicationContext, lifecycleScope)
        val repository = NovaRepository(db)
        val viewModel = NovaViewModel(repository)

        setContent {
            NovaPromoteTheme {
                NovaApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NovaApp(viewModel: NovaViewModel) {
    var currentScreen by remember { mutableStateOf(MainNavScreen.MARKETPLACE) }
    var selectedServiceForDetail by remember { mutableStateOf<ServiceEntity?>(null) }
    var showUserSwitcher by remember { mutableStateOf(false) }

    val currentUser by viewModel.currentUser.collectAsState()
    val userMessage by viewModel.userMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(userMessage) {
        userMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearUserMessage()
        }
    }

    // Handle back button smoothly
    BackHandler(enabled = selectedServiceForDetail != null || currentScreen != MainNavScreen.MARKETPLACE) {
        if (selectedServiceForDetail != null) {
            selectedServiceForDetail = null
        } else {
            currentScreen = MainNavScreen.MARKETPLACE
        }
    }

    Scaffold(
        topBar = {
            if (selectedServiceForDetail == null && currentScreen != MainNavScreen.POLICIES) {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.img_app_icon),
                                contentDescription = "Nova Promote Logo",
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Nova Promote",
                                        color = Color.White,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Surface(
                                        color = EmeraldVerify.copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = "LEGAL",
                                            color = EmeraldLight,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "Compliant Social Marketing",
                                    color = Slate400,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    },
                    actions = {
                        // User Persona switcher chip
                        Surface(
                            color = Slate800,
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier
                                .clickable { showUserSwitcher = true }
                                .border(1.dp, Slate700, RoundedCornerShape(20.dp))
                                .testTag("user_persona_chip")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when (currentUser?.role) {
                                                UserRole.CUSTOMER -> CyanGlow
                                                UserRole.PROVIDER -> IndigoAccent
                                                UserRole.ADMIN -> RedGlow
                                                null -> Slate400
                                            }
                                        )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = currentUser?.name?.take(10) ?: "Profile",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Switch user",
                                    tint = Slate400,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Quick Wallet pill
                        Surface(
                            color = Slate900,
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier
                                .clickable {
                                    selectedServiceForDetail = null
                                    currentScreen = MainNavScreen.WALLET
                                }
                                .border(1.dp, CyanGlow.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                                .padding(end = 8.dp)
                                .testTag("top_bar_wallet_chip")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = CyanGlow,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "$${String.format("%.0f", currentUser?.walletBalance ?: 0.0)}",
                                    color = CyanGlow,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Slate950
                    )
                )
            }
        },
        bottomBar = {
            if (selectedServiceForDetail == null) {
                NavigationBar(
                    containerColor = Slate900,
                    contentColor = Color.White,
                    tonalElevation = 8.dp,
                    windowInsets = WindowInsets.navigationBars,
                    modifier = Modifier.border(1.dp, Slate800)
                ) {
                    MainNavScreen.values().forEach { screen ->
                        val isSelected = currentScreen == screen
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                selectedServiceForDetail = null
                                currentScreen = screen
                            },
                            icon = {
                                Icon(
                                    imageVector = screen.icon,
                                    contentDescription = screen.label,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = screen.label,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Slate950,
                                selectedTextColor = CyanGlow,
                                indicatorColor = CyanNeon,
                                unselectedIconColor = Slate400,
                                unselectedTextColor = Slate400
                            ),
                            modifier = Modifier.testTag("nav_item_${screen.name.lowercase()}")
                        )
                    }
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Slate950,
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (selectedServiceForDetail != null) {
                ServiceDetailScreen(
                    service = selectedServiceForDetail!!,
                    viewModel = viewModel,
                    onBack = { selectedServiceForDetail = null },
                    onOrderPlaced = {
                        selectedServiceForDetail = null
                        currentScreen = MainNavScreen.ORDERS
                    }
                )
            } else {
                when (currentScreen) {
                    MainNavScreen.MARKETPLACE -> {
                        HomeScreen(
                            viewModel = viewModel,
                            onServiceSelected = { service ->
                                selectedServiceForDetail = service
                            },
                            onNavigateToPolicies = {
                                currentScreen = MainNavScreen.POLICIES
                            }
                        )
                    }
                    MainNavScreen.ORDERS -> {
                        OrdersScreen(viewModel = viewModel)
                    }
                    MainNavScreen.PROVIDER_STUDIO -> {
                        ProviderDashboardScreen(viewModel = viewModel)
                    }
                    MainNavScreen.WALLET -> {
                        WalletScreen(viewModel = viewModel)
                    }
                    MainNavScreen.POLICIES -> {
                        SafetyPoliciesScreen(
                            onBack = { currentScreen = MainNavScreen.MARKETPLACE }
                        )
                    }
                    MainNavScreen.ADMIN -> {
                        AdminDashboardScreen(viewModel = viewModel)
                    }
                }
            }
        }
    }

    if (showUserSwitcher) {
        UserSwitcherDialog(
            viewModel = viewModel,
            onDismiss = { showUserSwitcher = false }
        )
    }
}
