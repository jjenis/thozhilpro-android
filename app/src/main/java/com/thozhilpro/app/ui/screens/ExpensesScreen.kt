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
import com.thozhilpro.app.ui.viewmodel.ExpensesViewModel
import java.math.BigDecimal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpensesScreen(navController: NavController, viewModel: ExpensesViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var editingExpense by remember { mutableStateOf<com.thozhilpro.app.data.model.Expense?>(null) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Expenses", fontWeight = FontWeight.Bold) }, navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, "Back") } }) },
        floatingActionButton = { FloatingActionButton(onClick = { showAddDialog = true }) { Icon(Icons.Default.Add, "Add") } },
        bottomBar = { BottomNavBar(navController, Routes.EXPENSES) }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            OutlinedTextField(value = uiState.searchQuery, onValueChange = { viewModel.setSearchQuery(it) }, placeholder = { Text("Search expenses...") }, leadingIcon = { Icon(Icons.Default.Search, null) }, modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), singleLine = true)
            if (uiState.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            } else {
                LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(uiState.filteredExpenses) { e ->
                        Card(Modifier.fillMaxWidth()) {
                            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Column(Modifier.weight(1f)) {
                                    AssistChip(onClick = {}, label = { Text(e.type, fontSize = 11.sp) }, colors = AssistChipDefaults.assistChipColors(containerColor = Blue.copy(0.1f), labelColor = Blue))
                                    Text(e.expenseDate, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(0.5f))
                                    e.description?.let { Text(it, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(0.6f)) }
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(formatCurrency(e.amount.toDouble()), fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Red)
                                    Row {
                                        IconButton(onClick = { editingExpense = e }) { Icon(Icons.Default.Edit, "Edit", tint = Indigo, modifier = Modifier.size(18.dp)) }
                                        IconButton(onClick = { viewModel.deleteExpense(e.id) }) { Icon(Icons.Default.Delete, "Delete", tint = Red, modifier = Modifier.size(18.dp)) }
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
        AddExpenseDialog(onDismiss = { showAddDialog = false }, onAdd = { type, amt, date, desc ->
            viewModel.createExpense(type, amt, date, desc)
            showAddDialog = false
        })
    }

    editingExpense?.let { expense ->
        EditExpenseDialog(expense = expense, onDismiss = { editingExpense = null }, onSave = { type, amt, date, desc ->
            viewModel.updateExpense(expense.id, type, amt, date, desc)
            editingExpense = null
        })
    }
}

@Composable
fun AddExpenseDialog(onDismiss: () -> Unit, onAdd: (String, BigDecimal, String, String?) -> Unit) {
    val types = listOf("LABOUR", "SALARY", "TRANSPORT", "MAINTENANCE", "UTILITIES", "RENT", "SUPPLIES", "DISCOUNT", "OTHER")
    var type by remember { mutableStateOf(types[0]) }
    var amount by remember { mutableStateOf("") }
    var date by remember { mutableStateOf(java.time.LocalDate.now().toString()) }
    var desc by remember { mutableStateOf("") }

    AlertDialog(onDismissRequest = onDismiss, title = { Text("Add Expense") },
        text = { Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(value = type, onValueChange = { type = it }, label = { Text("Type *") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(value = amount, onValueChange = { amount = it }, label = { Text("Amount *") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text("Date (YYYY-MM-DD)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        }},
        confirmButton = { Button(onClick = { amount.toBigDecimalOrNull()?.let { onAdd(type, it, date, desc.ifBlank { null }) } }, enabled = amount.toBigDecimalOrNull() != null && type.isNotBlank()) { Text("Add") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun EditExpenseDialog(expense: com.thozhilpro.app.data.model.Expense, onDismiss: () -> Unit, onSave: (String, BigDecimal, String, String?) -> Unit) {
    var type by remember { mutableStateOf(expense.type) }
    var amount by remember { mutableStateOf(expense.amount.toPlainString()) }
    var date by remember { mutableStateOf(expense.expenseDate) }
    var desc by remember { mutableStateOf(expense.description ?: "") }

    AlertDialog(onDismissRequest = onDismiss, title = { Text("Edit Expense") },
        text = { Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(value = type, onValueChange = { type = it }, label = { Text("Type *") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(value = amount, onValueChange = { amount = it }, label = { Text("Amount *") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text("Date") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        }},
        confirmButton = { Button(onClick = { amount.toBigDecimalOrNull()?.let { onSave(type, it, date, desc.ifBlank { null }) } }) { Text("Save") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
