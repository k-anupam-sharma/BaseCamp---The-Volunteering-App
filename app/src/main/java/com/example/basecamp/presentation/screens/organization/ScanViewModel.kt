package com.example.basecamp.presentation.screens.organization

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
    data class Success(val message: String) : ScanState()
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

                // 1. Fetch the ticket
                val ticket = supabaseClient.postgrest["tickets"].select {
                    filter {
                        eq("event_id", eventId)
                        eq("volunteer_id", volunteerId)
                    }
                }.decodeSingleOrNull<com.example.basecamp.domain.model.Ticket>()

                if (ticket == null) {
                    _scanState.value = ScanState.Error("No RSVP found for this volunteer")
                    return@launch
                }

                val nowIso = java.time.Instant.now().toString()

                if (ticket.checkInTime == null) {
                    // 2. 1st Scan -> Login (Check-in)
                    supabaseClient.postgrest["tickets"].update(
                        {
                            set("status", "Checked In")
                            set("check_in_time", nowIso)
                        }
                    ) {
                        filter {
                            eq("event_id", eventId)
                            eq("volunteer_id", volunteerId)
                        }
                    }
                    _scanState.value = ScanState.Success("Logged In!")
                } else if (ticket.checkOutTime == null) {
                    // 3. 2nd Scan -> Logout (Check-out)
                    supabaseClient.postgrest["tickets"].update(
                        {
                            set("status", "Attended")
                            set("check_out_time", nowIso)
                        }
                    ) {
                        filter {
                            eq("event_id", eventId)
                            eq("volunteer_id", volunteerId)
                        }
                    }
                    _scanState.value = ScanState.Success("Logged Out!")
                } else {
                    // 4. Already completed both scans
                    _scanState.value = ScanState.Error("Volunteer has already logged out")
                }
            } catch (e: Exception) {
                _scanState.value = ScanState.Error("Invalid QR or Update Failed")
            }
        }
    }
    
    fun resumeScanning() {
        _scanState.value = ScanState.Scanning
    }
}


