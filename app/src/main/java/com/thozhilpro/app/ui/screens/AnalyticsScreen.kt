package com.thozhilpro.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.thozhilpro.app.ui.navigation.Routes
import com.thozhilpro.app.ui.theme.*
import com.thozhilpro.app.ui.viewmodel.AnalyticsViewModel
import java.math.BigDecimal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(navController: NavController, viewModel: AnalyticsViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Analytics", fontWeight = FontWeight.Bold) }, navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, "Back") } }) },
        bottomBar = { BottomNavBar(navController, Routes.ANALYTICS) }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Date filter
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(value = uiState.startDate, onValueChange = { viewModel.setStartDate(it) }, label = { Text("From") }, modifier = Modifier.weight(1f), singleLine = true)
                    OutlinedTextField(value = uiState.endDate, onValueChange = { viewModel.setEndDate(it) }, label = { Text("To") }, modifier = Modifier.weight(1f), singleLine = true)
                    Button(onClick = { viewModel.loadAnalytics() }) { Text("Go") }
                }
            }

            if (uiState.isLoading) {
                item { Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator() } }
            } else {
                val data = uiState.data
                // Summary cards
                item {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        StatCard("Wholesale", formatCurrency((data?.totalWholesaleSales ?: BigDecimal.ZERO).toDouble()), Indigo, Modifier.weight(1f))
                        StatCard("Retail", formatCurrency((data?.totalRetailSales ?: BigDecimal.ZERO).toDouble()), Blue, Modifier.weight(1f))
                    }
                }
                item {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        StatCard("Purchases", formatCurrency((data?.totalPurchases ?: BigDecimal.ZERO).toDouble()), Orange, Modifier.weight(1f))
                        StatCard("Expenses", formatCurrency((data?.totalExpenses ?: BigDecimal.ZERO).toDouble()), Red, Modifier.weight(1f))
                    }
                }
                item {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        val pl = data?.profitLoss ?: BigDecimal.ZERO
                        StatCard("Profit/Loss", formatCurrency(pl.toDouble()), if (pl >= BigDecimal.ZERO) Green else Red, Modifier.weight(1f))
                        StatCard("Received", formatCurrency((data?.totalReceived ?: BigDecimal.ZERO).toDouble()), Green, Modifier.weight(1f))
                    }
                }

                // Fast moving items
                item { Text("Fast Moving Items", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, modifier = Modifier.padding(top = 8.dp)) }
                val fast = data?.fastMovingItems ?: emptyList()
                if (fast.isEmpty()) {
                    item { Card(Modifier.fillMaxWidth()) { Text("No data", Modifier.padding(16.dp), color = MaterialTheme.colorScheme.onSurface.copy(0.5f)) } }
                } else {
                    itemsIndexed(fast) { idx, item ->
                        Card(Modifier.fillMaxWidth()) {
                            Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text("${idx + 1}. ${item.name ?: ""}", Modifier.weight(1f), fontSize = 14.sp)
                                AssistChip(onClick = {}, label = { Text("${item.quantitySold ?: 0} sold", fontSize = 11.sp) }, colors = AssistChipDefaults.assistChipColors(containerColor = Green.copy(0.1f), labelColor = Green))
                            }
                        }
                    }
                }

                // Not moving items
                item { Text("Not Moving Items", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, modifier = Modifier.padding(top = 8.dp)) }
                val notMoving = data?.notMovingItems ?: emptyList()
                if (notMoving.isEmpty()) {
                    item { Card(Modifier.fillMaxWidth()) { Text("No data", Modifier.padding(16.dp), color = MaterialTheme.colorScheme.onSurface.copy(0.5f)) } }
                } else {
                    itemsIndexed(notMoving.take(10)) { idx, item ->
                        Card(Modifier.fillMaxWidth()) {
                            Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text("${idx + 1}. ${item.name ?: ""} (qty: ${item.quantity ?: 0})", Modifier.weight(1f), fontSize = 14.sp)
                                AssistChip(onClick = {}, label = { Text("${item.daysInStock ?: 0}d", fontSize = 11.sp) }, colors = AssistChipDefaults.assistChipColors(containerColor = Orange.copy(0.1f), labelColor = Orange))
                            }
                        }
                    }
                }

                // Expense breakdown
                item { Text("Expense Breakdown", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, modifier = Modifier.padding(top = 8.dp)) }
                val breakdown = data?.expenseBreakdown ?: emptyMap()
                if (breakdown.isEmpty()) {
                    item { Card(Modifier.fillMaxWidth()) { Text("No expenses", Modifier.padding(16.dp), color = MaterialTheme.colorScheme.onSurface.copy(0.5f)) } }
                } else {
                    breakdown.entries.forEach { (type, amount) ->
                        item {
                            Card(Modifier.fillMaxWidth()) {
                                Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Text(type, Modifier.weight(1f), fontSize = 14.sp)
                                    Text(formatCurrency(amount.toDouble()), fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Red)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
