package com.thozhilpro.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.thozhilpro.app.ui.theme.*
import com.thozhilpro.app.ui.viewmodel.SubscriptionExpiredViewModel

@Composable
fun SubscriptionExpiredScreen(
    onSubscriptionRenewed: () -> Unit,
    viewModel: SubscriptionExpiredViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isRenewed) {
        if (uiState.isRenewed) onSubscriptionRenewed()
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(48.dp))
        Icon(Icons.Default.Warning, "Expired", tint = Orange, modifier = Modifier.size(72.dp))
        Spacer(Modifier.height(16.dp))
        Text("Subscription Expired", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        Spacer(Modifier.height(8.dp))
        Text("Your subscription has expired. Please renew to continue using ThozhilPro.", fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground.copy(0.6f), textAlign = TextAlign.Center)

        Spacer(Modifier.height(24.dp))

        // Billing cycle toggle
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Monthly", fontWeight = if (!uiState.isYearly) FontWeight.Bold else FontWeight.Normal, color = if (!uiState.isYearly) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(0.5f))
            Switch(checked = uiState.isYearly, onCheckedChange = { viewModel.toggleBillingCycle() })
            Text("Yearly (Save 20%)", fontWeight = if (uiState.isYearly) FontWeight.Bold else FontWeight.Normal, color = if (uiState.isYearly) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(0.5f))
        }

        Spacer(Modifier.height(20.dp))

        // Plan cards
        uiState.plans.forEach { plan ->
            val planName = plan["name"]?.toString() ?: ""
            val monthlyPrice = (plan["monthlyPrice"] as? Number)?.toInt() ?: 0
            val yearlyPrice = (plan["yearlyPrice"] as? Number)?.toInt() ?: 0
            val price = if (uiState.isYearly) yearlyPrice else monthlyPrice
            val maxUsers = (plan["maxUsers"] as? Number)?.toInt() ?: 1
            val maxItems = (plan["maxItems"] as? Number)?.toInt() ?: 50
            val isSelected = uiState.selectedPlan == planName

            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                colors = CardDefaults.cardColors(containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface),
                onClick = { viewModel.selectPlan(planName) }
            ) {
                Column(Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(planName, fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.weight(1f))
                        Text("₹$price/${if (uiState.isYearly) "yr" else "mo"}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
                    }
                    Spacer(Modifier.height(8.dp))
                    Text("$maxUsers users • $maxItems items", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(0.6f))
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = { viewModel.startPayment() },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            enabled = uiState.selectedPlan.isNotBlank() && !uiState.isProcessing
        ) {
            if (uiState.isProcessing) {
                CircularProgressIndicator(Modifier.size(22.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text("Subscribe Now", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            }
        }

        if (uiState.error != null) {
            Text(uiState.error!!, color = MaterialTheme.colorScheme.error, fontSize = 13.sp, modifier = Modifier.padding(top = 8.dp))
        }

        Spacer(Modifier.height(32.dp))
    }
}
