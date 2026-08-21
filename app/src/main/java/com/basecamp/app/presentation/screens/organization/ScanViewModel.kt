package com.basecamp.app.presentation.screens.organization

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

sealed class ScanState {
    object Idle : ScanState()
    object Scanning : ScanState()
    object Loading : ScanState()
    data class Success(val volunteerId: String) : ScanState()
    data class Error(val message: String) : ScanState()
}

@HiltViewModel
class ScanViewModel @Inject constructor(
    private val supabaseClient: SupabaseClient
) : ViewModel() {

    private val _scanState = MutableStateFlow<ScanState>(ScanState.Scanning)
    val scanState: StateFlow<ScanState> = _scanState.asStateFlow()

    fun processQrCode(qrData: String) {
        // Prevent multiple calls while already loading or successful
        if (_scanState.value is ScanState.Loading || _scanState.value is ScanState.Success) return

        _scanState.value = ScanState.Loading

        viewModelScope.launch {
            try {
                val json = JSONObject(qrData)
                val eventId = json.getString("eventId")
                val volunteerId = json.getString("volunteerId")

                // Update RSVPs table in Supabase
                supabaseClient.postgrest["RSVPs"].update(
                    {
                        set("status", "Attended")
                    }
                ) {
                    filter {
                        eq("eventId", eventId)
                        eq("volunteerId", volunteerId)
                    }
                }

                _scanState.value = ScanState.Success(volunteerId)
            } catch (e: Exception) {
                _scanState.value = ScanState.Error("Invalid QR or Update Failed")
            }
        }
    }
    
    fun resumeScanning() {
        _scanState.value = ScanState.Scanning
    }
}
