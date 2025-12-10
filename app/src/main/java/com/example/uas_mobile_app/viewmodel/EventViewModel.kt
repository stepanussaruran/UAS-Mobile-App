// viewmodel/EventViewModel.kt
package com.example.uas_mobile_app.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uas_mobile_app.api.RetrofitClient
import com.example.uas_mobile_app.model.Event
import com.example.uas_mobile_app.model.EventStats
import kotlinx.coroutines.launch

class EventViewModel : ViewModel() {
    private val _events = mutableStateOf<List<Event>>(emptyList())
    val events: State<List<Event>> = _events

    private val _stats = mutableStateOf<EventStats?>(null)
    val stats: State<EventStats?> = _stats

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    // TAMBAHKAN INI!
    private val _success = mutableStateOf<String?>(null)
    val success: State<String?> = _success

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _success.value = null
            try {
                val eventResponse = RetrofitClient.apiService.getAllEvents()
                if (eventResponse.isSuccessful && eventResponse.body()?.status == 200) {
                    _events.value = eventResponse.body()?.data ?: emptyList()
                } else {
                    _error.value = eventResponse.body()?.message
                }

                val statsResponse = RetrofitClient.apiService.getStatistics()
                if (statsResponse.isSuccessful && statsResponse.body()?.status == 200) {
                    _stats.value = statsResponse.body()?.data
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Network error"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createEvent(event: Event, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitClient.apiService.createEvent(event)
                if (response.isSuccessful && response.body()?.status == 201) {
                    _success.value = "Event berhasil dibuat!"
                    onSuccess()
                    loadData()
                } else {
                    _error.value = response.body()?.message ?: "Gagal membuat event"
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateEvent(id: Int, event: Event, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitClient.apiService.updateEvent(id, event)
                if (response.isSuccessful && response.body()?.status == 200) {
                    _success.value = "Event berhasil diupdate!"
                    onSuccess()
                    loadData()
                } else {
                    _error.value = response.body()?.message ?: "Gagal update"
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteEvent(id: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitClient.apiService.deleteEvent(id)
                if (response.isSuccessful && response.body()?.status == 200) {
                    _success.value = "Event berhasil dihapus!"
                    onSuccess()
                    loadData()
                } else {
                    _error.value = response.body()?.message ?: "Gagal hapus"
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    // TAMBAHKAN INI!
    fun clearMessages() {
        _error.value = null
        _success.value = null
    }
}