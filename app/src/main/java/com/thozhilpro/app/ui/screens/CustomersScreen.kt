package com.thozhilpro.app.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.thozhilpro.app.data.model.*
import com.thozhilpro.app.ui.navigation.Routes
import com.thozhilpro.app.ui.theme.Green
import com.thozhilpro.app.ui.theme.Orange
import com.thozhilpro.app.ui.theme.Red
import com.thozhilpro.app.ui.viewmodel.CustomersViewModel
import java.math.BigDecimal
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomersScreen(navController: NavController, viewModel: CustomersViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showSettleDialog by remember { mutableStateOf(false) }
    var showPayBalanceDialog by remember { mutableStateOf(false) }
    var editCustomer by remember { mutableStateOf<CustomerDetail?>(null) }
    val context = LocalContext.current

    // Settlement result snackbar
    LaunchedEffect(uiState.settlementResult) {
        if (uiState.settlementResult != null) {
            showSettleDialog = false
            showPayBalanceDialog = false
            viewModel.clearSettlementResult()
        }
    }

    if (uiState.showingDetail && uiState.customerDetail != null) {
        CustomerDetailScreen(
            detail = uiState.customerDetail!!,
            isLoading = uiState.isLoading,
            onBack = { viewModel.goBackToList() },
            onSettle = { showSettleDialog = true },
            onPayBalance = { showPayBalanceDialog = true },
            onEdit = { editCustomer = uiState.customerDetail; showEditDialog = true },
            onShareStatement = { shareCustomerStatement(context, uiState.customerDetail!!) },
            onShareBill = { billId -> shareBillWhatsApp(context, uiState.customerDetail!!, billId) },
            onDownloadPdf = { billId -> downloadBillPdf(context, billId) }
        )
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(if (uiState.showingDelayedOnly) "Delayed Payments" else "Customers", fontWeight = FontWeight.Bold) },
                    navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, "Back") } },
                    actions = {
                        IconButton(onClick = { viewModel.loadDelayedPaymentCustomers() }) {
                            Icon(Icons.Default.Warning, "Delayed", tint = Red)
                        }
                        if (uiState.showingDelayedOnly) {
                            TextButton(onClick = { viewModel.loadCustomers() }) { Text("All", color = MaterialTheme.colorScheme.primary) }
                        }
                    }
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
                            CustomerListCard(customer = customer, isDelayed = uiState.showingDelayedOnly,
                                onView = { viewModel.loadCustomerDetail(customer.id) },
                                onDelete = { viewModel.deleteCustomer(customer.id) })
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddCustomerDialog(onDismiss = { showAddDialog = false }, onAdd = { fn, ln, ph, em, addr, bal ->
            viewModel.createCustomer(fn, ln, ph, em, addr, bal); showAddDialog = false
        })
    }

    if (showEditDialog && editCustomer != null) {
        EditCustomerDialog(detail = editCustomer!!, onDismiss = { showEditDialog = false; editCustomer = null },
            onUpdate = { id, fn, ln, ph, em, addr, idt, idn ->
                viewModel.updateCustomer(id, fn, ln, ph, em, addr, idt, idn); showEditDialog = false; editCustomer = null
            })
    }

    if (showSettleDialog && uiState.customerDetail != null) {
        SettlementDialog(detail = uiState.customerDetail!!, onDismiss = { showSettleDialog = false },
            onSettle = { req -> viewModel.settleBills(req); showSettleDialog = false })
    }

    if (showPayBalanceDialog && uiState.customerDetail != null) {
        PayAgainstBalanceDialog(detail = uiState.customerDetail!!, onDismiss = { showPayBalanceDialog = false },
            onPay = { req -> viewModel.settleBills(req); showPayBalanceDialog = false })
    }
}

@Composable
fun CustomerListCard(customer: Customer, isDelayed: Boolean, onView: () -> Unit, onDelete: () -> Unit) {
    Card(Modifier.fillMaxWidth().clickable(onClick = onView),
        colors = if (isDelayed) CardDefaults.cardColors(containerColor = Red.copy(alpha = 0.05f)) else CardDefaults.cardColors()
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isDelayed) Text("⚠️ ", fontSize = 14.sp)
                    Text(customer.fullName, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                }
                Text(customer.phone, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(0.6f))
                customer.email?.let { Text(it, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(0.5f)) }
            }
            Column(horizontalAlignment = Alignment.End) {
                val pending = customer.pendingAmount ?: BigDecimal.ZERO
                val prevBal = customer.previousBalance ?: BigDecimal.ZERO
                Text(formatCurrency(customer.balance.toDouble()), fontWeight = FontWeight.Bold, fontSize = 14.sp,
                    color = if (customer.balance > BigDecimal.ZERO) Red else Green)
                if (prevBal > BigDecimal.ZERO) {
                    Text("Prev: ${formatCurrency(prevBal.toDouble())}", fontSize = 11.sp, color = Orange)
                }
                Row {
                    IconButton(onClick = onView, modifier = Modifier.size(32.dp)) { Icon(Icons.Default.Visibility, "View", modifier = Modifier.size(18.dp)) }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) { Icon(Icons.Default.Delete, "Delete", tint = Red, modifier = Modifier.size(18.dp)) }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerDetailScreen(
    detail: CustomerDetail, isLoading: Boolean,
    onBack: () -> Unit, onSettle: () -> Unit, onPayBalance: () -> Unit,
    onEdit: () -> Unit, onShareStatement: () -> Unit,
    onShareBill: (Long) -> Unit, onDownloadPdf: (Long) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(detail.fullName, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        if (detail.hasDelayedPayments) {
                            Spacer(Modifier.width(8.dp))
                            Badge(containerColor = Red) { Text("DELAYED", fontSize = 10.sp, color = Color.White) }
                        }
                    }
                },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") } },
                actions = { IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, "Edit") } }
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else {
            Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())) {
                // Info Card
                Card(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Column(Modifier.padding(16.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            InfoItem("Phone", detail.phone ?: "-")
                            InfoItem("Email", detail.email ?: "-")
                        }
                        Spacer(Modifier.height(8.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            InfoItem("Address", detail.address ?: "-")
                            InfoItem("ID", if (detail.idType != null) "${detail.idType}: ${detail.idNumber}" else "-")
                        }
                    }
                }

                // Summary Cards
                Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SummaryCard(Modifier.weight(1f), "Total Sales", detail.totalSales, MaterialTheme.colorScheme.primary)
                    SummaryCard(Modifier.weight(1f), "Pending", detail.totalPending, Red)
                }
                Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SummaryCard(Modifier.weight(1f), "Prev Balance", detail.previousBalance, Orange)
                    SummaryCard(Modifier.weight(1f), "Total Due", detail.totalDue, if (detail.totalDue > BigDecimal.ZERO) Red else Green)
                }

                // Action Buttons
                Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = onSettle, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)) {
                        Text("💰 Settle", fontSize = 13.sp)
                    }
                    OutlinedButton(onClick = onPayBalance, modifier = Modifier.weight(1f)) {
                        Text("💳 Pay Balance", fontSize = 13.sp)
                    }
                }
                Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onShareStatement, modifier = Modifier.weight(1f)) {
                        Text("📤 Statement", fontSize = 13.sp)
                    }
                    OutlinedButton(onClick = onEdit, modifier = Modifier.weight(1f)) {
                        Text("✏️ Edit", fontSize = 13.sp)
                    }
                }

                // Bills Section
                Text("Bills / Invoices", fontWeight = FontWeight.Bold, fontSize = 16.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp))

                if (detail.bills.isEmpty()) {
                    Text("No bills found", color = MaterialTheme.colorScheme.onSurface.copy(0.5f),
                        modifier = Modifier.fillMaxWidth().padding(32.dp), textAlign = TextAlign.Center)
                } else {
                    detail.bills.forEach { bill ->
                        BillCard(bill = bill, onShare = { onShareBill(bill.id) }, onDownload = { onDownloadPdf(bill.id) })
                    }
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun InfoItem(label: String, value: String) {
    Column {
        Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(0.5f))
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun SummaryCard(modifier: Modifier, label: String, amount: BigDecimal, color: Color) {
    Card(modifier) {
        Column(Modifier.padding(12.dp)) {
            Text(formatCurrency(amount.toDouble()), fontWeight = FontWeight.Bold, fontSize = 18.sp, color = color)
            Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(0.6f))
        }
    }
}

@Composable
fun BillCard(bill: SaleBill, onShare: () -> Unit, onDownload: () -> Unit) {
    val bgColor = if (bill.paymentDelayed) Red.copy(alpha = 0.05f) else Color.Transparent
    Card(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = if (bill.paymentDelayed) Red.copy(0.04f) else MaterialTheme.colorScheme.surface)) {
        Column(Modifier.padding(12.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text(bill.invoiceNumber ?: "SALE-${bill.id}", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Text(bill.saleDate ?: "", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(0.5f))
                }
                StatusBadge(bill.status ?: "PENDING", bill.paymentDelayed, bill.daysOverdue)
            }
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Amount: ${formatCurrency(bill.totalAmount.toDouble())}", fontSize = 12.sp)
                    Text("Paid: ${formatCurrency(bill.paymentReceived.toDouble())}", fontSize = 12.sp, color = Green)
                    if (bill.discountAmount > BigDecimal.ZERO) Text("Discount: ${formatCurrency(bill.discountAmount.toDouble())}", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Balance Due", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(0.5f))
                    Text(formatCurrency(bill.balanceDue.toDouble()), fontWeight = FontWeight.Bold, fontSize = 16.sp,
                        color = if (bill.balanceDue > BigDecimal.ZERO) Red else Green)
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onDownload, modifier = Modifier.height(32.dp), contentPadding = PaddingValues(horizontal = 12.dp)) {
                    Text("📄 PDF", fontSize = 11.sp)
                }
                OutlinedButton(onClick = onShare, modifier = Modifier.height(32.dp), contentPadding = PaddingValues(horizontal = 12.dp)) {
                    Text("📱 Share", fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
fun StatusBadge(status: String, delayed: Boolean, daysOverdue: Long) {
    val color = when (status) { "SETTLED" -> Green; "PARTIAL" -> Orange; else -> Red }
    val text = if (delayed) "$status ⚠️ ${daysOverdue}d" else status
    Surface(shape = MaterialTheme.shapes.small, color = color.copy(alpha = 0.15f)) {
        Text(text, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = color)
    }
}

// ===== DIALOGS =====

@Composable
fun AddCustomerDialog(onDismiss: () -> Unit, onAdd: (String, String, String, String?, String?, BigDecimal?) -> Unit) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var balance by remember { mutableStateOf("0") }

    AlertDialog(onDismissRequest = onDismiss, title = { Text("Add Customer") },
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
            Button(onClick = { onAdd(firstName, lastName, phone, email.ifBlank { null }, address.ifBlank { null }, balance.toBigDecimalOrNull()) },
                enabled = firstName.isNotBlank() && lastName.isNotBlank() && phone.isNotBlank()) { Text("Add") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun EditCustomerDialog(detail: CustomerDetail, onDismiss: () -> Unit,
                       onUpdate: (Long, String, String, String, String?, String?, String?, String?) -> Unit) {
    var firstName by remember { mutableStateOf(detail.firstName) }
    var lastName by remember { mutableStateOf(detail.lastName) }
    var phone by remember { mutableStateOf(detail.phone ?: "") }
    var email by remember { mutableStateOf(detail.email ?: "") }
    var address by remember { mutableStateOf(detail.address ?: "") }
    var idType by remember { mutableStateOf(detail.idType ?: "") }
    var idNumber by remember { mutableStateOf(detail.idNumber ?: "") }

    AlertDialog(onDismissRequest = onDismiss, title = { Text("Edit Customer") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.verticalScroll(rememberScrollState())) {
                OutlinedTextField(value = firstName, onValueChange = { firstName = it }, label = { Text("First Name *") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(value = lastName, onValueChange = { lastName = it }, label = { Text("Last Name *") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone *") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Address") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(value = idType, onValueChange = { idType = it }, label = { Text("ID Type") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(value = idNumber, onValueChange = { idNumber = it }, label = { Text("ID Number") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            }
        },
        confirmButton = {
            Button(onClick = { onUpdate(detail.id, firstName, lastName, phone, email.ifBlank { null }, address.ifBlank { null }, idType.ifBlank { null }, idNumber.ifBlank { null }) },
                enabled = firstName.isNotBlank() && lastName.isNotBlank() && phone.isNotBlank()) { Text("Update") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun SettlementDialog(detail: CustomerDetail, onDismiss: () -> Unit, onSettle: (SettlementRequest) -> Unit) {
    val unsettled = detail.bills.filter { it.status != "SETTLED" }
    val selectedBills = remember { mutableStateMapOf<Long, Boolean>() }
    var paymentAmount by remember { mutableStateOf("") }
    var discountAmount by remember { mutableStateOf("0") }
    var paymentMode by remember { mutableStateOf("CASH") }
    var description by remember { mutableStateOf("") }

    val selectedTotal = unsettled.filter { selectedBills[it.id] == true }.sumOf { it.balanceDue }

    AlertDialog(onDismissRequest = onDismiss, title = { Text("Settle Bills") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.verticalScroll(rememberScrollState())) {
                Text("Select Bills:", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                unsettled.forEach { bill ->
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Checkbox(checked = selectedBills[bill.id] == true,
                            onCheckedChange = { selectedBills[bill.id] = it })
                        Column(Modifier.weight(1f)) {
                            Text(bill.invoiceNumber ?: "SALE-${bill.id}", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            Text(bill.saleDate ?: "", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(0.5f))
                        }
                        Text(formatCurrency(bill.balanceDue.toDouble()), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Red)
                    }
                }
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = unsettled.all { selectedBills[it.id] == true },
                        onCheckedChange = { checked -> unsettled.forEach { selectedBills[it.id] = checked } })
                    Text("Select All", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                HorizontalDivider()
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Bills Total:", fontSize = 13.sp); Text(formatCurrency(selectedTotal.toDouble()), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Prev Balance:", fontSize = 13.sp, color = Orange); Text(formatCurrency(detail.previousBalance.toDouble()), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Orange)
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Grand Total:", fontWeight = FontWeight.Bold); Text(formatCurrency(selectedTotal.add(detail.previousBalance).toDouble()), fontWeight = FontWeight.Bold, color = Red)
                }
                HorizontalDivider()
                OutlinedTextField(value = paymentAmount, onValueChange = { paymentAmount = it }, label = { Text("Payment Amount *") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(value = discountAmount, onValueChange = { discountAmount = it }, label = { Text("Discount Amount") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            }
        },
        confirmButton = {
            Button(onClick = {
                val saleIds = unsettled.filter { selectedBills[it.id] == true }.map { it.id }
                onSettle(SettlementRequest(
                    customerId = detail.id, saleIds = saleIds,
                    paymentAmount = paymentAmount.toBigDecimalOrNull() ?: BigDecimal.ZERO,
                    discountAmount = discountAmount.toBigDecimalOrNull() ?: BigDecimal.ZERO,
                    paymentMode = paymentMode, description = description.ifBlank { null },
                    paymentDate = LocalDate.now().toString(), againstPreviousBalance = saleIds.isEmpty()
                ))
            }, enabled = (paymentAmount.toBigDecimalOrNull() ?: BigDecimal.ZERO) > BigDecimal.ZERO || (discountAmount.toBigDecimalOrNull() ?: BigDecimal.ZERO) > BigDecimal.ZERO) { Text("Settle") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun PayAgainstBalanceDialog(detail: CustomerDetail, onDismiss: () -> Unit, onPay: (SettlementRequest) -> Unit) {
    var paymentAmount by remember { mutableStateOf(detail.previousBalance.max(BigDecimal.ZERO).toPlainString()) }
    var discountAmount by remember { mutableStateOf("0") }
    var paymentMode by remember { mutableStateOf("CASH") }
    var description by remember { mutableStateOf("Payment against previous balance") }

    AlertDialog(onDismissRequest = onDismiss, title = { Text("Pay Against Balance") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Card(colors = CardDefaults.cardColors(containerColor = Orange.copy(0.1f))) {
                    Row(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Current Balance:", fontWeight = FontWeight.Medium)
                        Text(formatCurrency(detail.previousBalance.toDouble()), fontWeight = FontWeight.Bold, color = Orange)
                    }
                }
                OutlinedTextField(value = paymentAmount, onValueChange = { paymentAmount = it }, label = { Text("Payment Amount *") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(value = discountAmount, onValueChange = { discountAmount = it }, label = { Text("Discount (if any)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            }
        },
        confirmButton = {
            Button(onClick = {
                onPay(SettlementRequest(
                    customerId = detail.id, saleIds = emptyList(),
                    paymentAmount = paymentAmount.toBigDecimalOrNull() ?: BigDecimal.ZERO,
                    discountAmount = discountAmount.toBigDecimalOrNull() ?: BigDecimal.ZERO,
                    paymentMode = paymentMode, description = description.ifBlank { null },
                    paymentDate = LocalDate.now().toString(), againstPreviousBalance = true
                ))
            }, enabled = (paymentAmount.toBigDecimalOrNull() ?: BigDecimal.ZERO) > BigDecimal.ZERO) { Text("Pay") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

// ===== WhatsApp & PDF Helpers =====

private fun shareBillWhatsApp(context: Context, detail: CustomerDetail, billId: Long) {
    val bill = detail.bills.find { it.id == billId } ?: return
    val phone = (detail.phone ?: "").replace(Regex("[^0-9]"), "")
    val msg = "Hi ${detail.firstName},\n\nInvoice: ${bill.invoiceNumber ?: "SALE-$billId"}\nAmount: ${bill.totalAmount}\nBalance Due: ${bill.balanceDue}\n\nThank you!"
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/$phone?text=${Uri.encode(msg)}"))
    context.startActivity(intent)
}

private fun shareCustomerStatement(context: Context, detail: CustomerDetail) {
    val phone = (detail.phone ?: "").replace(Regex("[^0-9]"), "")
    val msg = "Hi ${detail.firstName},\n\nAccount Statement:\nTotal Due: ${detail.totalDue}\n\nThank you!"
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/$phone?text=${Uri.encode(msg)}"))
    context.startActivity(intent)
}

private fun downloadBillPdf(context: Context, billId: Long) {
    val token = context.getSharedPreferences("auth", Context.MODE_PRIVATE).getString("token", "") ?: ""
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://10.0.2.2:8080/api/invoices/sale/$billId?token=$token"))
    context.startActivity(intent)
}
