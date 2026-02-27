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
import com.thozhilpro.app.ui.viewmodel.ItemsViewModel
import java.math.BigDecimal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemsScreen(navController: NavController, viewModel: ItemsViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Inventory", fontWeight = FontWeight.Bold) }, navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, "Back") } }) },
        floatingActionButton = { FloatingActionButton(onClick = { showAddDialog = true }) { Icon(Icons.Default.Add, "Add") } },
        bottomBar = { BottomNavBar(navController, Routes.ITEMS) }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            OutlinedTextField(value = uiState.searchQuery, onValueChange = { viewModel.setSearchQuery(it) }, placeholder = { Text("Search items...") }, leadingIcon = { Icon(Icons.Default.Search, null) }, modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), singleLine = true)
            if (uiState.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            } else {
                LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(uiState.filteredItems) { item ->
                        Card(Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Column(Modifier.weight(1f)) {
                                        Text(item.name, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                                        item.category?.let { Text(it, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(0.5f)) }
                                    }
                                    val statusColor = when (item.status) { "ACTIVE" -> Green; "OUT_OF_STOCK" -> Red; else -> Orange }
                                    AssistChip(onClick = {}, label = { Text(item.status ?: "ACTIVE", fontSize = 11.sp) }, colors = AssistChipDefaults.assistChipColors(containerColor = statusColor.copy(0.1f), labelColor = statusColor))
                                }
                                Spacer(Modifier.height(8.dp))
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Column { Text("Qty: ${item.quantity}", fontSize = 13.sp); Text(item.unit ?: "", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(0.5f)) }
                                    Column(horizontalAlignment = Alignment.End) { Text("Buy: ${formatCurrency((item.purchaseRate ?: BigDecimal.ZERO).toDouble())}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(0.6f)); Text("Sell: ${formatCurrency((item.wholesalePrice ?: BigDecimal.ZERO).toDouble())}", fontSize = 12.sp, color = Green) }
                                    IconButton(onClick = { viewModel.deleteItem(item.id) }) { Icon(Icons.Default.Delete, "Delete", tint = Red, modifier = Modifier.size(20.dp)) }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        var name by remember { mutableStateOf("") }; var category by remember { mutableStateOf("") }; var unit by remember { mutableStateOf("") }
        var qty by remember { mutableStateOf("0") }; var purchaseRate by remember { mutableStateOf("0") }; var wholesale by remember { mutableStateOf("0") }; var retail by remember { mutableStateOf("0") }; var location by remember { mutableStateOf("") }
        AlertDialog(onDismissRequest = { showAddDialog = false }, title = { Text("Add Item") },
            text = { Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name *") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category") }, modifier = Modifier.weight(1f), singleLine = true); OutlinedTextField(value = unit, onValueChange = { unit = it }, label = { Text("Unit") }, modifier = Modifier.weight(1f), singleLine = true) }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { OutlinedTextField(value = qty, onValueChange = { qty = it }, label = { Text("Qty") }, modifier = Modifier.weight(1f), singleLine = true); OutlinedTextField(value = purchaseRate, onValueChange = { purchaseRate = it }, label = { Text("Purchase Rate") }, modifier = Modifier.weight(1f), singleLine = true) }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { OutlinedTextField(value = wholesale, onValueChange = { wholesale = it }, label = { Text("Wholesale") }, modifier = Modifier.weight(1f), singleLine = true); OutlinedTextField(value = retail, onValueChange = { retail = it }, label = { Text("Retail") }, modifier = Modifier.weight(1f), singleLine = true) }
                OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text("Location") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            }},
            confirmButton = { Button(onClick = { viewModel.createItem(name, category.ifBlank { null }, unit.ifBlank { null }, qty.toIntOrNull() ?: 0, purchaseRate.toBigDecimalOrNull(), wholesale.toBigDecimalOrNull(), retail.toBigDecimalOrNull(), location.ifBlank { null }); showAddDialog = false }, enabled = name.isNotBlank()) { Text("Add") } },
            dismissButton = { TextButton(onClick = { showAddDialog = false }) { Text("Cancel") } }
        )
    }
}
