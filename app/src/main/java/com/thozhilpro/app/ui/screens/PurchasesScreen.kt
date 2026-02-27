package com.thozhilpro.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.thozhilpro.app.ui.viewmodel.PurchasesViewModel
import java.math.BigDecimal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PurchasesScreen(navController: NavController, viewModel: PurchasesViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Purchases", fontWeight = FontWeight.Bold) }, navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, "Back") } }) },
        bottomBar = { BottomNavBar(navController, Routes.PURCHASES) }
    ) { padding ->
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(uiState.purchases) { p ->
                    Card(Modifier.fillMaxWidth()) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(p.supplier?.name ?: "Unknown", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                                Text(p.purchaseDate, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(0.5f))
                                p.invoiceNumber?.let { Text("Invoice: $it", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(0.5f)) }
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(formatCurrency((p.totalAmount ?: BigDecimal.ZERO).toDouble()), fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Indigo)
                                val statusColor = if (p.status == "SETTLED") Green else Orange
                                AssistChip(onClick = {}, label = { Text(p.status ?: "PENDING", fontSize = 11.sp) }, colors = AssistChipDefaults.assistChipColors(containerColor = statusColor.copy(0.1f), labelColor = statusColor))
                            }
                        }
                    }
                }
            }
        }
    }
}
