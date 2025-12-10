package com.example.uas_mobile_app.ui.theme

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.uas_mobile_app.model.Event
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDialog(
    event: Event? = null,
    onDismiss: () -> Unit,
    onSave: (Event) -> Unit
) {
    val context = LocalContext.current

    var title by remember { mutableStateOf(event?.title ?: "") }
    var date by remember { mutableStateOf(event?.date ?: "") }
    var time by remember { mutableStateOf(event?.time ?: "") }
    var location by remember { mutableStateOf(event?.location ?: "") }
    var description by remember { mutableStateOf(event?.description ?: "") }
    var capacity by remember { mutableStateOf(event?.capacity?.toString() ?: "") }
    var status by remember { mutableStateOf(event?.status ?: "upcoming") }
    var expanded by remember { mutableStateOf(false) }

    val calendar = Calendar.getInstance()

    LaunchedEffect(event) {
        event?.let {
            val parts = it.date.split("-")
            if (parts.size == 3) {
                calendar.set(parts[0].toInt(), parts[1].toInt() - 1, parts[2].toInt())
            }
            val timeParts = it.time.split(":")
            if (timeParts.size >= 2) {
                calendar.set(Calendar.HOUR_OF_DAY, timeParts[0].toInt())
                calendar.set(Calendar.MINUTE, timeParts[1].toInt())
            }
        }
    }

    val datePicker = DatePickerDialog(
        context,
        { _, year, month, day ->
            date = String.format("%04d-%02d-%02d", year, month + 1, day)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    val timePicker = TimePickerDialog(
        context,
        { _, hour, minute ->
            time = String.format("%02d:%02d:00", hour, minute)
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (event == null) "Buat Event Baru" else "Edit Event") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Judul *") }
                )

                OutlinedTextField(
                    value = date,
                    onValueChange = {},
                    label = { Text("Tanggal (YYYY-MM-DD) *") },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { datePicker.show() }) {
                            Icon(Icons.Default.CalendarToday, "Pilih Tanggal")
                        }
                    }
                )

                OutlinedTextField(
                    value = time,
                    onValueChange = {},
                    label = { Text("Waktu (HH:MM:SS) *") },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { timePicker.show() }) {
                            Icon(Icons.Default.AccessTime, "Pilih Waktu")
                        }
                    }
                )

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Lokasi *") }
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Deskripsi") }
                )

                OutlinedTextField(
                    value = capacity,
                    onValueChange = { newValue ->
                        capacity = newValue.filter { it.isDigit() }
                    },
                    label = { Text("Kapasitas") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )

                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                    OutlinedTextField(
                        value = status,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Status *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        listOf("upcoming", "ongoing", "completed", "cancelled").forEach { s ->
                            DropdownMenuItem(text = { Text(s) }, onClick = {
                                status = s
                                expanded = false
                            })
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (title.isBlank() || date.isBlank() || time.isBlank() || location.isBlank()) return@Button
                val newEvent = Event(
                    id = event?.id,
                    title = title,
                    date = date,
                    time = time,
                    location = location,
                    description = description.takeIf { it.isNotBlank() },
                    capacity = capacity.toIntOrNull(),
                    status = status
                )
                onSave(newEvent)
            }) {
                Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}