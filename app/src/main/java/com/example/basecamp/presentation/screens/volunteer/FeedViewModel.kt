package com.example.basecamp.presentation.screens.volunteer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.basecamp.domain.model.Event
import com.example.basecamp.domain.model.Ticket
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import io.github.jan.supabase.gotrue.auth
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class FeedState {
    object Loading : FeedState()
    data class Success(val availableEvents: List<Event>, val rsvpedEvents: List<Event>) : FeedState()
    data class Error(val message: String) : FeedState()
}

sealed class RsvpState {
    object Idle : RsvpState()
    object Loading : RsvpState()
    data class Success(val eventId: String) : RsvpState()
    data class Error(val message: String) : RsvpState()
}

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val supabaseClient: SupabaseClient
) : ViewModel() {

    private val _feedState = MutableStateFlow<FeedState>(FeedState.Loading)
    val feedState: StateFlow<FeedState> = _feedState.asStateFlow()
    
    private val _rsvpState = MutableStateFlow<RsvpState>(RsvpState.Idle)
    val rsvpState: StateFlow<RsvpState> = _rsvpState.asStateFlow()
    
    private val _rsvpEventIds = MutableStateFlow<Set<String>>(emptySet())
    val rsvpEventIds: StateFlow<Set<String>> = _rsvpEventIds.asStateFlow()

    val currentUserId: String
        get() = supabaseClient.auth.currentUserOrNull()?.id ?: "unknown"

    init {
        fetchEvents()
    }

    fun fetchEvents(showLoading: Boolean = true) {
        viewModelScope.launch {
            if (showLoading) {
                _feedState.value = FeedState.Loading
            }
            try {
                val userId = currentUserId
                // Fetch events from the Supabase "Events" table
                val events = supabaseClient.postgrest["events"]
                    .select()
                    .decodeList<Event>()
                
                // Fetch all tickets to calculate capacity
                val allTickets = supabaseClient.postgrest["tickets"].select().decodeList<Ticket>()
                
                // Get my RSVPs
                val myRsvpIds = allTickets.filter { it.volunteerId == userId }.map { it.eventId }.toSet()
                _rsvpEventIds.value = myRsvpIds
                
                val ticketCounts = allTickets.groupingBy { it.eventId }.eachCount()
                
                val rsvpedEvents = events.filter { it.id in myRsvpIds }
                val availableEvents = events.filter { event ->
                    val count = ticketCounts[event.id] ?: 0
                    val isFull = event.maxVolunteers > 0 && count >= event.maxVolunteers
                    event.id !in myRsvpIds && !isFull
                }
                
                _feedState.value = FeedState.Success(availableEvents, rsvpedEvents)
            } catch (e: Exception) {
                _feedState.value = FeedState.Error(e.userFriendlyMessage("Failed to fetch events"))
            }
        }
    }

    fun rsvpForEvent(eventId: String) {
        viewModelScope.launch {
            _rsvpState.value = RsvpState.Loading
            try {
                val userId = supabaseClient.auth.currentUserOrNull()?.id ?: throw Exception("Not logged in")
                
                val newTicket = Ticket(
                    eventId = eventId,
                    volunteerId = userId
                )
                
                supabaseClient.postgrest["tickets"].insert(newTicket)
                _rsvpEventIds.value = _rsvpEventIds.value + eventId
                _rsvpState.value = RsvpState.Success(eventId)
                fetchEvents(showLoading = false)
            } catch (e: Exception) {
                _rsvpState.value = RsvpState.Error(e.userFriendlyMessage("Failed to RSVP. You may have already RSVP'd."))
            }
        }
    }
    
    fun resetRsvpState() {
        _rsvpState.value = RsvpState.Idle
    }

    private fun Exception.userFriendlyMessage(default: String): String {
        val msg = this.message ?: return default
        return msg.substringBefore("URL:").substringBefore("HTTP request to").trim()
    }
}



