package com.example.basecamp.presentation.screens.organization

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.basecamp.domain.model.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.util.UUID

import com.example.basecamp.domain.model.Ticket

sealed class DashboardState {
    object Loading : DashboardState()
    data class Success(val eventsWithCounts: List<Pair<Event, Int>>) : DashboardState()
    data class Error(val message: String) : DashboardState()
}

sealed class CreateEventState {
    object Idle : CreateEventState()
    object Loading : CreateEventState()
    object Success : CreateEventState()
    data class Error(val message: String) : CreateEventState()
}

@HiltViewModel
class OrgViewModel @Inject constructor(
    private val supabaseClient: SupabaseClient
) : ViewModel() {

    private val _createState = MutableStateFlow<CreateEventState>(CreateEventState.Idle)
    val createState: StateFlow<CreateEventState> = _createState.asStateFlow()

    private val _dashboardState = MutableStateFlow<DashboardState>(DashboardState.Loading)
    val dashboardState: StateFlow<DashboardState> = _dashboardState.asStateFlow()

    private val _eventVolunteersState = MutableStateFlow<List<Pair<com.example.basecamp.domain.model.User, Ticket>>>(emptyList())
    val eventVolunteersState: StateFlow<List<Pair<com.example.basecamp.domain.model.User, Ticket>>> = _eventVolunteersState.asStateFlow()

    val currentUserId: String
        get() = supabaseClient.auth.currentUserOrNull()?.id ?: "unknown"

    init {
        fetchDashboardData()
    }

    fun fetchEventVolunteers(eventId: String) {
        viewModelScope.launch {
            try {
                // 1. Fetch tickets for this event
                val tickets = supabaseClient.postgrest["tickets"]
                    .select { filter { eq("event_id", eventId) } }
                    .decodeList<Ticket>()
                
                // 2. Fetch users for those tickets
                val volunteerIds = tickets.map { it.volunteerId }
                if (volunteerIds.isEmpty()) {
                    _eventVolunteersState.value = emptyList()
                    return@launch
                }
                
                val users = supabaseClient.postgrest["users"]
                    .select { filter { isIn("id", volunteerIds) } }
                    .decodeList<com.example.basecamp.domain.model.User>()
                
                val userMap = users.associateBy { it.id }
                
                val result = tickets.mapNotNull { ticket ->
                    userMap[ticket.volunteerId]?.let { user ->
                        Pair(user, ticket)
                    }
                }
                _eventVolunteersState.value = result
            } catch (e: Exception) {
                android.util.Log.e("OrgViewModel", "Error fetching volunteers: ${e.message}", e)
            }
        }
    }

    fun fetchDashboardData(showLoading: Boolean = true) {
        viewModelScope.launch {
            if (showLoading) _dashboardState.value = DashboardState.Loading
            try {
                val userId = currentUserId
                // 1. Fetch events created by this org
                val events = supabaseClient.postgrest["events"]
                    .select { filter { eq("org_id", userId) } }
                    .decodeList<Event>()
                
                // 2. Fetch all tickets
                val allTickets = supabaseClient.postgrest["tickets"].select().decodeList<Ticket>()
                
                // 3. Count RSVPs for each event
                val eventsWithCounts = events.map { event ->
                    val rsvpCount = allTickets.count { it.eventId == event.id }
                    Pair(event, rsvpCount)
                }
                
                _dashboardState.value = DashboardState.Success(eventsWithCounts)
            } catch (e: Exception) {
                _dashboardState.value = DashboardState.Error(e.userFriendlyMessage("Failed to load dashboard data"))
            }
        }
    }

    fun createEvent(
        title: String, description: String, date: String, cause: String, location: String, orgName: String, maxVolunteers: Int,
        typeOfWork: String, payment: String, dressCode: String, contactDetails: String
    ) {
        viewModelScope.launch {
            _createState.value = CreateEventState.Loading
            try {
                val newEvent = Event(
                    id = UUID.randomUUID().toString(),
                    title = title,
                    description = description,
                    cause = cause,
                    location = location,
                    date = date,
                    orgName = orgName,
                    orgId = supabaseClient.auth.currentUserOrNull()?.id ?: "",
                    maxVolunteers = maxVolunteers,
                    typeOfWork = typeOfWork,
                    payment = payment,
                    dressCode = dressCode,
                    contactDetails = contactDetails
                )
                
                supabaseClient.postgrest["events"].insert(newEvent)
                _createState.value = CreateEventState.Success
                fetchDashboardData()
            } catch (e: Exception) {
                _createState.value = CreateEventState.Error(e.userFriendlyMessage("Failed to create event"))
            }
        }
    }
    
        fun getEvent(eventId: String): Event? {
        val state = _dashboardState.value
        if (state is DashboardState.Success) {
            return state.eventsWithCounts.find { it.first.id == eventId }?.first
        }
        return null
    }

    fun updateEvent(
        eventId: String,
        title: String,
        description: String,
        cause: String,
        location: String,
        date: String
    ) {
        viewModelScope.launch {
            _createState.value = CreateEventState.Loading
            try {
                // Update the event
                val updateData = mapOf(
                    "title" to title,
                    "description" to description,
                    "cause" to cause,
                    "location" to location,
                    "date" to date
                )
                supabaseClient.postgrest["events"]
                    .update(updateData) {
                        filter { eq("id", eventId) }
                    }
                
                _createState.value = CreateEventState.Success
                fetchDashboardData() // Refresh list
            } catch (e: Exception) {
                _createState.value = CreateEventState.Error(e.message ?: "Failed to update event")
            }
        }
    }

    fun deleteEvent(eventId: String) {
        viewModelScope.launch {
            try {
                supabaseClient.postgrest["events"].delete {
                    filter { eq("id", eventId) }
                }
                fetchDashboardData()
            } catch (e: Exception) {
                _dashboardState.value = DashboardState.Error(e.userFriendlyMessage("Failed to delete event"))
            }
        }
    }

    fun addVolunteerSpots(eventId: String, currentMax: Int, amount: Int) {
        viewModelScope.launch {
            try {
                supabaseClient.postgrest["events"].update(
                    { set("max_volunteers", currentMax + amount) }
                ) {
                    filter { eq("id", eventId) }
                }
                fetchDashboardData()
            } catch (e: Exception) {
                _dashboardState.value = DashboardState.Error(e.userFriendlyMessage("Failed to update spots"))
            }
        }
    }
    
    fun resetState() {
        _createState.value = CreateEventState.Idle
    }

    private fun Exception.userFriendlyMessage(default: String): String {
        val msg = this.message ?: return default
        return msg.substringBefore("URL:").substringBefore("HTTP request to").trim()
    }
}








