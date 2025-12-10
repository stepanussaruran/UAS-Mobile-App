// ui/EventScreen.kt
package com.example.uas_mobile_app.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.uas_mobile_app.model.Event
import com.example.uas_mobile_app.viewmodel.EventViewModel
import kotlinx.coroutines.delay
import androidx.compose.material.pullrefresh.*
import com.example.uas_mobile_app.ui.theme.StatCard
import com.example.uas_mobile_app.ui.theme.EventCard
import com.example.uas_mobile_app.ui.theme.EventDialog

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun EventScreen(viewModel: EventViewModel) {
    val events by viewModel.events
    val stats by viewModel.stats
    val error by viewModel.error
    val success by viewModel.success
    val isLoading by viewModel.isLoading

    // State untuk dialog
    var showCreateDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf<Event?>(null) }
    var showDeleteDialog by remember { mutableStateOf<Event?>(null) }

    // Pull-to-refresh state
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isLoading,
        onRefresh = { viewModel.loadData() }
    )

    // Load data saat pertama kali
    LaunchedEffect(Unit) {
        viewModel.loadData()
    }

    // Auto-clear success/error setelah 3 detik
    LaunchedEffect(success, error) {
        if (success != null || error != null) {
            delay(3000)
            viewModel.clearMessages()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Event Management") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Event")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
        ) {

            stats?.let {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                ) {
                    StatCard(label = "Total", value = it.total.toString(), color = MaterialTheme.colorScheme.primary)
                    StatCard(label = "Upcoming", value = it.upcoming.toString(), color = MaterialTheme.colorScheme.tertiary)
                    StatCard(label = "Ongoing", value = it.ongoing.toString(), color = MaterialTheme.colorScheme.secondary)
                    StatCard(label = "Sel                esai", value = it.completed.toString(), color = MaterialTheme.colorScheme.error)
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            success?.let { Text(it, color = MaterialTheme.colorScheme.primary) }
            error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            Spacer(modifier = Modifier.height(8.dp))

            // === PULL-TO-REFRESH + LIST ===
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pullRefresh(pullRefreshState)
            ) {
                if (isLoading && events.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    LazyColumn {
                        items(events.size) { index ->
                            val event = events[index]
                            EventCard(
                                event = event,
                                onEdit = { showEditDialog = event },
                                onDelete = { showDeleteDialog = event }
                            )
                        }
                    }
                }

                PullRefreshIndicator(
                    refreshing = isLoading,
                    state = pullRefreshState,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        }
    }

    if (showCreateDialog) {
        EventDialog(
            onDismiss = { showCreateDialog = false },
            onSave = { newEvent ->
                viewModel.createEvent(newEvent) { showCreateDialog = false }
            }
        )
    }

    showEditDialog?.let { event ->
        EventDialog(
            event = event,
            onDismiss = { showEditDialog = null },
            onSave = { updatedEvent ->
                viewModel.updateEvent(event.id!!, updatedEvent) { showEditDialog = null }
            }
        )
    }

    showDeleteDialog?.let { event ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Hapus Event?") },
            text = { Text("Yakin ingin menghapus \"${event.title}\"?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteEvent(event.id!!) { showDeleteDialog = null }
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) { Text("Hapus") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) { Text("Batal") }
            }
        )
    }
}