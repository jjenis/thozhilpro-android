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
import com.thozhilpro.app.ui.viewmodel.PaymentsViewModel
import java.math.BigDecimal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentsScreen(navController: NavController, viewModel: PaymentsViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var editingPayment by remember { mutableStateOf<com.thozhilpro.app.data.model.Payment?>(null) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Payments", fontWeight = FontWeight.Bold) }, navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, "Back") } }) },
        floatingActionButton = { FloatingActionButton(onClick = { showAddDialog = true }) { Icon(Icons.Default.Add, "Add") } },
        bottomBar = { BottomNavBar(navController, Routes.PAYMENTS) }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            OutlinedTextField(value = uiState.searchQuery, onValueChange = { viewModel.setSearchQuery(it) }, placeholder = { Text("Search payments...") }, leadingIcon = { Icon(Icons.Default.Search, null) }, modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), singleLine = true)
            if (uiState.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            } else {
                LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(uiState.filteredPayments) { p ->
                        Card(Modifier.fillMaxWidth()) {
                            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Column(Modifier.weight(1f)) {
                                    val party = p.customer?.fullName ?: p.supplier?.name ?: "-"
                                    Text(party, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                                    Text(p.paymentDate, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(0.5f))
                                    p.description?.let { Text(it, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(0.5f)) }
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(formatCurrency(p.amount.toDouble()), fontWeight = FontWeight.Bold, fontSize = 14.sp, color = if (p.paymentType == "RECEIVED") Green else Red)
                                    val typeColor = if (p.paymentType == "RECEIVED") Green else Red
                                    AssistChip(onClick = {}, label = { Text(p.paymentType ?: "RECEIVED", fontSize = 11.sp) }, colors = AssistChipDefaults.assistChipColors(containerColor = typeColor.copy(0.1f), labelColor = typeColor))
                                    Row {
                                        IconButton(onClick = { editingPayment = p }) { Icon(Icons.Default.Edit, "Edit", tint = Indigo, modifier = Modifier.size(18.dp)) }
                                        IconButton(onClick = { viewModel.deletePayment(p.id) }) { Icon(Icons.Default.Delete, "Delete", tint = Red, modifier = Modifier.size(18.dp)) }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddPaymentDialog(onDismiss = { showAddDialog = false }, onAdd = { amt, mode, type, date, desc ->
            viewModel.createPayment(amt, mode, type, date, desc)
            showAddDialog = false
        })
    }

    editingPayment?.let { payment ->
        EditPaymentDialog(payment = payment, onDismiss = { editingPayment = null }, onSave = { amt, mode, type, date, desc ->
            viewModel.updatePayment(payment.id, amt, mode, type, date, desc)
            editingPayment = null
        })
    }
}

@Composable
fun AddPaymentDialog(onDismiss: () -> Unit, onAdd: (BigDecimal, String, String, String, String?) -> Unit) {
    var amount by remember { mutableStateOf("") }
    var mode by remember { mutableStateOf("CASH") }
    var type by remember { mutableStateOf("RECEIVED") }
    var date by remember { mutableStateOf(java.time.LocalDate.now().toString()) }
    var desc by remember { mutableStateOf("") }

    AlertDialog(onDismissRequest = onDismiss, title = { Text("Record Payment") },
        text = { Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(value = amount, onValueChange = { amount = it }, label = { Text("Amount *") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = type == "RECEIVED", onClick = { type = "RECEIVED" }, label = { Text("Received") }, modifier = Modifier.weight(1f))
                FilterChip(selected = type == "PAID", onClick = { type = "PAID" }, label = { Text("Paid") }, modifier = Modifier.weight(1f))
            }
            OutlinedTextField(value = mode, onValueChange = { mode = it }, label = { Text("Mode (CASH/UPI/CARD)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text("Date (YYYY-MM-DD)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        }},
        confirmButton = { Button(onClick = { amount.toBigDecimalOrNull()?.let { onAdd(it, mode, type, date, desc.ifBlank { null }) } }, enabled = amount.toBigDecimalOrNull() != null) { Text("Add") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun EditPaymentDialog(payment: com.thozhilpro.app.data.model.Payment, onDismiss: () -> Unit, onSave: (BigDecimal, String, String, String, String?) -> Unit) {
    var amount by remember { mutableStateOf(payment.amount.toPlainString()) }
    var mode by remember { mutableStateOf(payment.paymentMode ?: "CASH") }
    var type by remember { mutableStateOf(payment.paymentType ?: "RECEIVED") }
    var date by remember { mutableStateOf(payment.paymentDate) }
    var desc by remember { mutableStateOf(payment.description ?: "") }

    AlertDialog(onDismissRequest = onDismiss, title = { Text("Edit Payment") },
        text = { Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(value = amount, onValueChange = { amount = it }, label = { Text("Amount *") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = type == "RECEIVED", onClick = { type = "RECEIVED" }, label = { Text("Received") }, modifier = Modifier.weight(1f))
                FilterChip(selected = type == "PAID", onClick = { type = "PAID" }, label = { Text("Paid") }, modifier = Modifier.weight(1f))
            }
            OutlinedTextField(value = mode, onValueChange = { mode = it }, label = { Text("Mode") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text("Date") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        }},
        confirmButton = { Button(onClick = { amount.toBigDecimalOrNull()?.let { onSave(it, mode, type, date, desc.ifBlank { null }) } }) { Text("Save") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
