package com.thozhilpro.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.thozhilpro.app.ui.navigation.Routes
import com.thozhilpro.app.ui.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController, viewModel: SettingsViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Settings", fontWeight = FontWeight.Bold) }, navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, "Back") } }) },
        bottomBar = { BottomNavBar(navController, Routes.SETTINGS) }
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Business Settings", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)

            OutlinedTextField(value = uiState.companyName, onValueChange = { viewModel.updateField("companyName", it) }, label = { Text("Company Name") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(value = uiState.phone, onValueChange = { viewModel.updateField("phone", it) }, label = { Text("Phone") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(value = uiState.currency, onValueChange = { viewModel.updateField("currency", it) }, label = { Text("Currency") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(value = uiState.gstNumber, onValueChange = { viewModel.updateField("gstNumber", it) }, label = { Text("GST Number") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(value = uiState.gstPercentage, onValueChange = { viewModel.updateField("gstPercentage", it) }, label = { Text("GST %") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(value = uiState.address, onValueChange = { viewModel.updateField("address", it) }, label = { Text("Address") }, modifier = Modifier.fillMaxWidth(), maxLines = 2)
            OutlinedTextField(value = uiState.paymentDelayDays, onValueChange = { viewModel.updateField("paymentDelayDays", it) }, label = { Text("Payment Delay Alert (days)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(value = uiState.stockAgeDays, onValueChange = { viewModel.updateField("stockAgeDays", it) }, label = { Text("Stock Age Alert (days)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)

            Button(
                onClick = { viewModel.saveSettings() },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                enabled = !uiState.isSaving
            ) {
                if (uiState.isSaving) CircularProgressIndicator(Modifier.size(22.dp), strokeWidth = 2.dp)
                else Text("Save Settings", fontWeight = FontWeight.SemiBold)
            }

            if (uiState.saveSuccess) {
                Text("Settings saved!", color = MaterialTheme.colorScheme.primary, fontSize = 13.sp)
            }
            if (uiState.error != null) {
                Text(uiState.error!!, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
            }

            Spacer(Modifier.height(16.dp))
            Text("Change Password", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)

            var currentPwd by remember { mutableStateOf("") }
            var newPwd by remember { mutableStateOf("") }
            var pwdMsg by remember { mutableStateOf<String?>(null) }

            OutlinedTextField(value = currentPwd, onValueChange = { currentPwd = it }, label = { Text("Current Password") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(value = newPwd, onValueChange = { newPwd = it }, label = { Text("New Password") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            Button(
                onClick = { viewModel.changePassword(currentPwd, newPwd) { msg -> pwdMsg = msg; currentPwd = ""; newPwd = "" } },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                enabled = currentPwd.isNotBlank() && newPwd.isNotBlank()
            ) { Text("Change Password") }
            pwdMsg?.let { Text(it, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary) }

            Spacer(Modifier.height(32.dp))
        }
    }
}
