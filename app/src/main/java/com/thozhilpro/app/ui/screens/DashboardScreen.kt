package com.thozhilpro.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.thozhilpro.app.ui.navigation.Routes
import com.thozhilpro.app.ui.theme.*
import com.thozhilpro.app.ui.viewmodel.DashboardViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { navController.navigate(Routes.NOTIFICATIONS) }) { Icon(Icons.Default.Notifications, "Notifications") }
                    IconButton(onClick = { navController.navigate(Routes.PROFILE) }) { Icon(Icons.Default.AccountCircle, "Profile") }
                }
            )
        },
        bottomBar = { BottomNavBar(navController, Routes.DASHBOARD) }
    ) { padding ->
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                val stats = uiState.stats
                // Stat cards
                item {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        StatCard("Items", "${stats?.totalItems ?: 0}", Indigo, Modifier.weight(1f))
                        StatCard("In Stock", "${stats?.inStockItems ?: 0}", Green, Modifier.weight(1f))
                    }
                }
                item {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        StatCard("Customers", "${stats?.totalCustomers ?: 0}", Blue, Modifier.weight(1f))
                        StatCard("Suppliers", "${stats?.totalSuppliers ?: 0}", Purple, Modifier.weight(1f))
                    }
                }
                item {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        StatCard("Today Sales", "${stats?.todaySales ?: 0}", Orange, Modifier.weight(1f))
                        StatCard("Purchases", "${stats?.totalPurchases ?: 0}", Red, Modifier.weight(1f))
                    }
                }
                // Revenue card
                item {
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp)) {
                            Text("This Month Revenue", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(0.6f))
                            Text(formatCurrency(stats?.monthlyRevenue?.toDouble() ?: 0.0), fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Green)
                            Text("Last month: ${formatCurrency(stats?.lastMonthRevenue?.toDouble() ?: 0.0)}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(0.5f))
                        }
                    }
                }
                // Pending card
                item {
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp)) {
                            Text("Pending from Customers", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(0.6f))
                            Text(formatCurrency(stats?.totalPendingFromCustomers?.toDouble() ?: 0.0), fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Red)
                            Text("${stats?.customersWithDues ?: 0} customers with dues", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(0.5f))
                        }
                    }
                }
                // Investment card
                item {
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp)) {
                            Text("Total Investment", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(0.6f))
                            Text(formatCurrency(stats?.totalInvestment?.toDouble() ?: 0.0), fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Blue)
                        }
                    }
                }
                // Quick navigation
                item {
                    Text("Quick Access", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, modifier = Modifier.padding(top = 8.dp))
                }
                item {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        QuickNavCard("Inventory", Icons.Default.Inventory2, Indigo, Modifier.weight(1f)) { navController.navigate(Routes.ITEMS) }
                        QuickNavCard("Purchases", Icons.Default.ShoppingCart, Orange, Modifier.weight(1f)) { navController.navigate(Routes.PURCHASES) }
                    }
                }
                item {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        QuickNavCard("Sales", Icons.Default.Storefront, Green, Modifier.weight(1f)) { navController.navigate(Routes.SALES) }
                        QuickNavCard("Payments", Icons.Default.Payment, Blue, Modifier.weight(1f)) { navController.navigate(Routes.PAYMENTS) }
                    }
                }
                item {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        QuickNavCard("Expenses", Icons.Default.Receipt, Red, Modifier.weight(1f)) { navController.navigate(Routes.EXPENSES) }
                        QuickNavCard("Analytics", Icons.Default.Analytics, Purple, Modifier.weight(1f)) { navController.navigate(Routes.ANALYTICS) }
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(Modifier.padding(16.dp)) {
            Text(value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = color)
            Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(0.6f))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickNavCard(label: String, icon: ImageVector, color: Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(modifier = modifier, onClick = onClick) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Icon(icon, label, tint = color, modifier = Modifier.size(28.dp))
            Text(label, fontWeight = FontWeight.Medium, fontSize = 14.sp)
        }
    }
}

@Composable
fun BottomNavBar(navController: NavController, currentRoute: String) {
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Dashboard, "Dashboard") },
            label = { Text("Home", fontSize = 11.sp) },
            selected = currentRoute == Routes.DASHBOARD,
            onClick = { if (currentRoute != Routes.DASHBOARD) navController.navigate(Routes.DASHBOARD) { popUpTo(Routes.DASHBOARD) { inclusive = true } } }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Inventory2, "Items") },
            label = { Text("Items", fontSize = 11.sp) },
            selected = currentRoute == Routes.ITEMS,
            onClick = { navController.navigate(Routes.ITEMS) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Storefront, "Sales") },
            label = { Text("Sales", fontSize = 11.sp) },
            selected = currentRoute == Routes.SALES || currentRoute == Routes.RETAIL_SALES,
            onClick = { navController.navigate(Routes.SALES) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Payment, "Payments") },
            label = { Text("Payments", fontSize = 11.sp) },
            selected = currentRoute == Routes.PAYMENTS,
            onClick = { navController.navigate(Routes.PAYMENTS) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Menu, "More") },
            label = { Text("More", fontSize = 11.sp) },
            selected = currentRoute == Routes.MORE,
            onClick = { navController.navigate(Routes.MORE) }
        )
    }
}

fun formatCurrency(value: Double): String {
    val fmt = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    return fmt.format(value)
}
