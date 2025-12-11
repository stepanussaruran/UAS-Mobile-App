package com.example.uas_mobile_app.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material.pullrefresh.PullRefreshIndicator

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun EventScreen(viewModel: EventViewModel) {
    val events by viewModel.events
    val stats by viewModel.stats
    val error by viewModel.error
    val success by viewModel.success
    val isLoading by viewModel.isLoading

    var showCreateDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf<Event?>(null) }
    var showDeleteDialog by remember { mutableStateOf<Event?>(null) }

    // PULL REFRESH MATERIAL 1
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isLoading,
        onRefresh = { viewModel.loadData() }
    )

    LaunchedEffect(Unit) {
        viewModel.loadData()
    }

    LaunchedEffect(success, error) {
        if (success != null || error != null) {
            delay(3000)
            viewModel.clearMessages()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Event Management", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary) }
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
    )
    { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
        ) {

            stats?.let { s ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    elevation = CardDefaults.cardElevation(3.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            SimpleStat("Total", s.total)
                            SimpleStat("Upcoming", s.upcoming)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            SimpleStat("Ongoing", s.ongoing)
                            SimpleStat("Complete", s.completed)
                        }
                    }
                }
            }

            success?.let { Text(it, color = MaterialTheme.colorScheme.primary) }
            error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            Spacer(modifier = Modifier.height(8.dp))


            // LIST + PULL REFRESH MATERIAL 1
            Box(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .pullRefresh(pullRefreshState),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(events.size) { index ->
                        val event = events[index]
                        EventCard(
                            event = event,
                            onEdit = { showEditDialog = event },
                            onDelete = { showDeleteDialog = event }
                        )
                    }

                    // FAB nggak nutupin card terakhir
                    item { Spacer(modifier = Modifier.height(90.dp)) }
                }

                // Loading pertama kali (kalau kosong)
                if (isLoading && events.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                // Indikator pull-to-refresh
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
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Hapus")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("Batal")
                }
            }
        )
    }
}
@Composable
fun SimpleStat(label: String, value: Int) {
    Column(
        modifier = Modifier.width(140.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

