package com.basecamp.app.presentation.screens.organization

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.basecamp.app.domain.model.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.util.UUID

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

    fun createEvent(title: String, description: String, date: String, cause: String, location: String, orgName: String) {
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
                    orgName = orgName
                )
                
                supabaseClient.postgrest["Events"].insert(newEvent)
                _createState.value = CreateEventState.Success
            } catch (e: Exception) {
                _createState.value = CreateEventState.Error(e.message ?: "Failed to create event")
            }
        }
    }
    
    fun resetState() {
        _createState.value = CreateEventState.Idle
    }
}
