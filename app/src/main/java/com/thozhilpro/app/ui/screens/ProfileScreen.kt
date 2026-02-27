package com.thozhilpro.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.thozhilpro.app.ui.navigation.Routes
import com.thozhilpro.app.ui.theme.*
import com.thozhilpro.app.ui.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val user by viewModel.user.collectAsState()
    val tenant by viewModel.tenant.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Profile", fontWeight = FontWeight.Bold) }, navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, "Back") } }) },
        bottomBar = { BottomNavBar(navController, Routes.PROFILE) }
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // User info card
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(
                        modifier = Modifier.size(72.dp),
                        shape = MaterialTheme.shapes.extraLarge,
                        color = MaterialTheme.colorScheme.primary
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                (user?.firstName?.firstOrNull()?.uppercaseChar() ?: 'U').toString(),
                                fontSize = 28.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Text("${user?.firstName ?: ""} ${user?.lastName ?: ""}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(user?.email ?: "", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface.copy(0.6f))
                    user?.role?.let {
                        AssistChip(onClick = {}, label = { Text(it, fontSize = 11.sp) }, colors = AssistChipDefaults.assistChipColors(containerColor = Indigo.copy(0.1f), labelColor = Indigo))
                    }
                }
            }

            // Business card
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(20.dp)) {
                    Text("Business", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                    Spacer(Modifier.height(8.dp))
                    Text(tenant?.companyName ?: "My Business", fontSize = 15.sp, fontWeight = FontWeight.Medium)
                    tenant?.phone?.let { Text("Phone: $it", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(0.6f)) }
                    tenant?.address?.let { Text(it, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(0.5f)) }
                    Spacer(Modifier.height(8.dp))
                    val plan = tenant?.plan ?: "FREE"
                    val status = tenant?.status ?: "ACTIVE"
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        AssistChip(onClick = {}, label = { Text(plan) }, colors = AssistChipDefaults.assistChipColors(containerColor = Indigo.copy(0.1f), labelColor = Indigo))
                        val sColor = if (status == "ACTIVE") Green else Red
                        AssistChip(onClick = {}, label = { Text(status) }, colors = AssistChipDefaults.assistChipColors(containerColor = sColor.copy(0.1f), labelColor = sColor))
                    }
                }
            }

            // Navigation items
            ProfileMenuItem(Icons.Default.People, "Customers") { navController.navigate(Routes.CUSTOMERS) }
            ProfileMenuItem(Icons.Default.Factory, "Suppliers") { navController.navigate(Routes.SUPPLIERS) }
            ProfileMenuItem(Icons.Default.Inventory2, "Inventory") { navController.navigate(Routes.ITEMS) }
            ProfileMenuItem(Icons.Default.ShoppingCart, "Purchases") { navController.navigate(Routes.PURCHASES) }
            ProfileMenuItem(Icons.Default.Storefront, "Wholesale Sales") { navController.navigate(Routes.SALES) }
            ProfileMenuItem(Icons.Default.Store, "Retail Sales") { navController.navigate(Routes.RETAIL_SALES) }
            ProfileMenuItem(Icons.Default.Receipt, "Expenses") { navController.navigate(Routes.EXPENSES) }
            ProfileMenuItem(Icons.Default.Analytics, "Analytics") { navController.navigate(Routes.ANALYTICS) }
            ProfileMenuItem(Icons.Default.Notifications, "Notifications") { navController.navigate(Routes.NOTIFICATIONS) }
            ProfileMenuItem(Icons.Default.Settings, "Settings") { navController.navigate(Routes.SETTINGS) }

            Spacer(Modifier.height(8.dp))
            Button(
                onClick = { viewModel.logout(); onLogout() },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Red)
            ) {
                Icon(Icons.Default.Logout, "Logout", modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text("Logout", fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileMenuItem(icon: ImageVector, label: String, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(horizontal = 16.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, label, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
            Spacer(Modifier.width(16.dp))
            Text(label, fontSize = 15.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
            Icon(Icons.Default.ChevronRight, "Go", tint = MaterialTheme.colorScheme.onSurface.copy(0.4f))
        }
    }
}
