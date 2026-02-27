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
import com.thozhilpro.app.ui.theme.Green
import com.thozhilpro.app.ui.theme.Red
import com.thozhilpro.app.ui.viewmodel.CustomersViewModel
import java.math.BigDecimal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomersScreen(navController: NavController, viewModel: CustomersViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Customers", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, "Back") } }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) { Icon(Icons.Default.Add, "Add") }
        },
        bottomBar = { BottomNavBar(navController, Routes.CUSTOMERS) }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            OutlinedTextField(
                value = uiState.searchQuery, onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = { Text("Search customers...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                singleLine = true
            )
            if (uiState.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            } else {
                LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(uiState.filteredCustomers) { customer ->
                        Card(Modifier.fillMaxWidth()) {
                            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Column(Modifier.weight(1f)) {
                                    Text(customer.fullName, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                                    Text(customer.phone, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(0.6f))
                                    customer.email?.let { Text(it, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(0.5f)) }
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(formatCurrency(customer.balance.toDouble()), fontWeight = FontWeight.Bold, fontSize = 14.sp, color = if (customer.balance > BigDecimal.ZERO) Red else Green)
                                    IconButton(onClick = { viewModel.deleteCustomer(customer.id) }) { Icon(Icons.Default.Delete, "Delete", tint = Red, modifier = Modifier.size(20.dp)) }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddCustomerDialog(onDismiss = { showAddDialog = false }, onAdd = { fn, ln, ph, em, addr, bal ->
            viewModel.createCustomer(fn, ln, ph, em, addr, bal)
            showAddDialog = false
        })
    }
}

@Composable
fun AddCustomerDialog(onDismiss: () -> Unit, onAdd: (String, String, String, String?, String?, BigDecimal?) -> Unit) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var balance by remember { mutableStateOf("0") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Customer") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = firstName, onValueChange = { firstName = it }, label = { Text("First Name *") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(value = lastName, onValueChange = { lastName = it }, label = { Text("Last Name *") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone *") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Address") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(value = balance, onValueChange = { balance = it }, label = { Text("Previous Balance") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            }
        },
        confirmButton = {
            Button(onClick = { onAdd(firstName, lastName, phone, email.ifBlank { null }, address.ifBlank { null }, balance.toBigDecimalOrNull()) }, enabled = firstName.isNotBlank() && lastName.isNotBlank() && phone.isNotBlank()) { Text("Add") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
