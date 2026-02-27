package com.thozhilpro.app.ui.screens

import androidx.compose.foundation.clickable
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
import com.thozhilpro.app.ui.theme.Blue
import com.thozhilpro.app.ui.viewmodel.NotificationsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(navController: NavController, viewModel: NotificationsViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notifications", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, "Back") } },
                actions = { TextButton(onClick = { viewModel.markAllRead() }) { Text("Mark All Read") } }
            )
        },
        bottomBar = { BottomNavBar(navController, Routes.NOTIFICATIONS) }
    ) { padding ->
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else if (uiState.notifications.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.NotificationsNone, "No notifications", modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.onSurface.copy(0.3f))
                    Spacer(Modifier.height(8.dp))
                    Text("No notifications", color = MaterialTheme.colorScheme.onSurface.copy(0.5f))
                }
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(uiState.notifications) { n ->
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable { viewModel.markRead(n.id) },
                        colors = CardDefaults.cardColors(containerColor = if (n.isRead) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.primaryContainer.copy(0.3f))
                    ) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                            Column(Modifier.weight(1f)) {
                                Text(n.title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                                n.subtitle?.let { Text(it, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(0.6f)) }
                                n.message?.let { Text(it, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(0.5f), modifier = Modifier.padding(top = 4.dp)) }
                                n.createdAt?.let { Text(it, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(0.4f), modifier = Modifier.padding(top = 4.dp)) }
                            }
                            if (!n.isRead) {
                                AssistChip(onClick = {}, label = { Text("New", fontSize = 10.sp) }, colors = AssistChipDefaults.assistChipColors(containerColor = Blue.copy(0.1f), labelColor = Blue))
                            }
                        }
                    }
                }
            }
        }
    }
}
