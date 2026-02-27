package com.thozhilpro.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.thozhilpro.app.ui.viewmodel.ProfileCompletionViewModel

@Composable
fun ProfileCompletionDialog(
    onDismiss: () -> Unit,
    onComplete: () -> Unit,
    viewModel: ProfileCompletionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) { viewModel.refreshData() }
    LaunchedEffect(uiState.isSuccess) { if (uiState.isSuccess) onComplete() }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Card(modifier = Modifier.fillMaxWidth().padding(8.dp), shape = MaterialTheme.shapes.large) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Complete Your Profile", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Text("Please fill in your details to continue", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), modifier = Modifier.padding(bottom = 20.dp))

                var firstName by remember { mutableStateOf(uiState.firstName) }
                var lastName by remember { mutableStateOf(uiState.lastName) }
                var companyName by remember { mutableStateOf(uiState.companyName) }
                var phone by remember { mutableStateOf(uiState.phone) }

                OutlinedTextField(value = firstName, onValueChange = { firstName = it }, label = { Text("First Name *") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(value = lastName, onValueChange = { lastName = it }, label = { Text("Last Name *") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(value = companyName, onValueChange = { companyName = it }, label = { Text("Company Name *") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone *") }, modifier = Modifier.fillMaxWidth(), singleLine = true)

                if (uiState.error != null) {
                    Text(uiState.error!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp, modifier = Modifier.padding(top = 6.dp))
                }

                Spacer(Modifier.height(20.dp))
                Button(
                    onClick = { viewModel.completeProfile(firstName, lastName, companyName, phone) },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    enabled = !uiState.isLoading && firstName.isNotBlank() && lastName.isNotBlank() && companyName.isNotBlank() && phone.isNotBlank()
                ) {
                    if (uiState.isLoading) CircularProgressIndicator(Modifier.size(22.dp), strokeWidth = 2.dp)
                    else Text("Save & Continue", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
