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
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import java.io.ByteArrayOutputStream
import io.github.jan.supabase.storage.storage

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
        eventId: String? = null,
        title: String, description: String, date: String, cause: String, location: String, orgName: String, maxVolunteersStr: String,
        typeOfWork: String, payment: String, dressCode: String, contactDetails: String, locationLink: String, isMultiDay: Boolean, endDate: String,
        context: android.content.Context, bannerUri: android.net.Uri?
    ) {
        viewModelScope.launch {
            _createState.value = CreateEventState.Loading
            try {
                // Check for empty required fields
                if (title.isBlank() || description.isBlank() || date.isBlank() || location.isBlank() ||
                    cause.isBlank() || maxVolunteersStr.isBlank() || typeOfWork.isBlank() ||
                    payment.isBlank() || dressCode.isBlank() || contactDetails.isBlank() || locationLink.isBlank() ||
                    (isMultiDay && endDate.isBlank())) {
                    _createState.value = CreateEventState.Error("Please fill all required fields")
                    return@launch
                }

                // Regex Validations
                if (!title.matches(Regex("^[A-Za-z\\s]+$"))) {
                    _createState.value = CreateEventState.Error("Title must contain only alphabets")
                    return@launch
                }
                if (!description.matches(Regex("^[A-Za-z0-9\\s.,!?'-]+$"))) {
                    _createState.value = CreateEventState.Error("Description contains invalid characters")
                    return@launch
                }
                if (!date.matches(Regex("^[0-9\\-]+$"))) {
                    _createState.value = CreateEventState.Error("Date must contain only numbers and -")
                    return@launch
                }
                if (isMultiDay && !endDate.matches(Regex("^[0-9\\-]+$"))) {
                    _createState.value = CreateEventState.Error("End Date must contain only numbers and -")
                    return@launch
                }
                if (!location.matches(Regex("^[A-Za-z0-9\\s.,!?'-]+$"))) {
                    _createState.value = CreateEventState.Error("Location contains invalid characters")
                    return@launch
                }
                if (!android.util.Patterns.WEB_URL.matcher(locationLink).matches()) {
                    _createState.value = CreateEventState.Error("Please enter a valid Location Link")
                    return@launch
                }
                if (!maxVolunteersStr.matches(Regex("^[0-9]+$"))) {
                    _createState.value = CreateEventState.Error("Max Volunteers must be a valid number")
                    return@launch
                }
                if (!typeOfWork.matches(Regex("^[A-Za-z\\s]+$"))) {
                    _createState.value = CreateEventState.Error("Type of work must contain only alphabets")
                    return@launch
                }
                if (!payment.matches(Regex("^[A-Za-z0-9\\s]+$"))) {
                    _createState.value = CreateEventState.Error("Payments/Perks must contain only alphabets and numbers")
                    return@launch
                }
                if (!dressCode.matches(Regex("^[A-Za-z\\s]+$"))) {
                    _createState.value = CreateEventState.Error("Dress code must contain only alphabets")
                    return@launch
                }
                if (!contactDetails.matches(Regex("^[0-9\\s+\\-]+$"))) {
                    _createState.value = CreateEventState.Error("Contact details must contain only numbers")
                    return@launch
                }

                val maxVolunteers = maxVolunteersStr.toIntOrNull() ?: 0

                var finalBannerUrl: String? = null
                if (bannerUri != null) {
                    try {
                        val inputStream = context.contentResolver.openInputStream(bannerUri)
                        val originalBitmap = BitmapFactory.decodeStream(inputStream)
                        inputStream?.close()

                        if (originalBitmap != null) {
                            var rotatedBitmap = originalBitmap
                            val exifInputStream = context.contentResolver.openInputStream(bannerUri)
                            if (exifInputStream != null) {
                                val exif = ExifInterface(exifInputStream)
                                val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
                                
                                val matrix = Matrix()
                                when (orientation) {
                                    ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
                                    ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                                    ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
                                }
                                
                                if (!matrix.isIdentity) {
                                    rotatedBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.width, originalBitmap.height, matrix, true)
                                    originalBitmap.recycle()
                                }
                                exifInputStream.close()
                            }

                            val outputStream = ByteArrayOutputStream()
                            rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
                            val compressedBytes = outputStream.toByteArray()

                            val imageEventId = eventId ?: UUID.randomUUID().toString()
                            val path = "${supabaseClient.auth.currentUserOrNull()?.id ?: "unknown"}/banner_${imageEventId}.jpg"
                            val bucket = supabaseClient.storage["event_banners"]
                            
                            bucket.upload(path, compressedBytes, upsert = true)
                            finalBannerUrl = bucket.publicUrl(path)
                        }
                    } catch (e: Exception) {
                        // If image upload fails, you can either stop creation or continue without it.
                        // We will stop creation.
                        _createState.value = CreateEventState.Error("Failed to upload banner photo: ${e.message}")
                        return@launch
                    }
                }

                val finalEventId = eventId ?: UUID.randomUUID().toString()
                
                val newEvent = Event(
                    id = finalEventId,
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
                    contactDetails = contactDetails,
                    locationLink = locationLink,
                    isMultiDay = isMultiDay,
                    endDate = if (isMultiDay) endDate else "",
                    bannerUrl = finalBannerUrl
                )
                
                supabaseClient.postgrest["events"].upsert(newEvent)
                _createState.value = CreateEventState.Success
                fetchDashboardData()
            } catch (e: Exception) {
                _createState.value = CreateEventState.Error(e.userFriendlyMessage("Failed to create event"))
            }
        }
    }
    
    suspend fun getEventById(eventId: String): Event? {
        return try {
            supabaseClient.postgrest["events"].select {
                filter { eq("id", eventId) }
            }.decodeSingleOrNull<Event>()
        } catch (e: Exception) {
            null
        }
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








