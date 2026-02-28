package com.thozhilpro.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.thozhilpro.app.ui.navigation.Routes
import com.thozhilpro.app.ui.theme.*

data class MoreMenuItemData(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val iconColor: Color,
    val route: String
)

@Composable
fun MoreScreen(navController: NavController) {
    val menuItems = listOf(
        MoreMenuItemData("Customers", "Manage customers", Icons.Default.People, Blue, Routes.CUSTOMERS),
        MoreMenuItemData("Suppliers", "Manage suppliers", Icons.Default.LocalShipping, Purple, Routes.SUPPLIERS),
        MoreMenuItemData("Purchases", "Track purchases", Icons.Default.ShoppingCart, Orange, Routes.PURCHASES),
        MoreMenuItemData("Retail Sales", "Retail transactions", Icons.Default.ShoppingBag, Green, Routes.RETAIL_SALES),
        MoreMenuItemData("Expenses", "Track spending", Icons.Default.Money, Red, Routes.EXPENSES),
        MoreMenuItemData("Analytics", "Charts & trends", Icons.Default.PieChart, Indigo, Routes.ANALYTICS),
        MoreMenuItemData("Notifications", "Alerts & updates", Icons.Default.Notifications, Orange, Routes.NOTIFICATIONS),
        MoreMenuItemData("Settings", "App settings", Icons.Default.Settings, Color(0xFF64748B), Routes.SETTINGS),
        MoreMenuItemData("Profile", "Account settings", Icons.Default.Person, IndigoDark, Routes.PROFILE)
    )

    Scaffold(
        bottomBar = { BottomNavBar(navController, Routes.MORE) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
        ) {
            // Header
            Surface(color = MaterialTheme.colorScheme.surface) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "More",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        letterSpacing = (-0.3).sp
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                for (rowIndex in menuItems.indices step 2) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        MoreGridCard(
                            item = menuItems[rowIndex],
                            onClick = { navController.navigate(menuItems[rowIndex].route) },
                            modifier = Modifier.weight(1f)
                        )
                        if (rowIndex + 1 < menuItems.size) {
                            MoreGridCard(
                                item = menuItems[rowIndex + 1],
                                onClick = { navController.navigate(menuItems[rowIndex + 1].route) },
                                modifier = Modifier.weight(1f)
                            )
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MoreGridCard(item: MoreMenuItemData, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp, horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(item.iconColor.copy(alpha = 0.12f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.title,
                    tint = item.iconColor,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = item.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = item.subtitle,
                fontSize = 12.sp,
                color = Color.Gray,
                maxLines = 1
            )
        }
    }
}
