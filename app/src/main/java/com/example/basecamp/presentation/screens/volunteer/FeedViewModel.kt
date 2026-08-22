package com.example.basecamp.presentation.screens.volunteer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.basecamp.domain.model.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class FeedState {
    object Loading : FeedState()
    data class Success(val events: List<Event>) : FeedState()
    data class Error(val message: String) : FeedState()
}

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val supabaseClient: SupabaseClient
) : ViewModel() {

    private val _feedState = MutableStateFlow<FeedState>(FeedState.Loading)
    val feedState: StateFlow<FeedState> = _feedState.asStateFlow()

    init {
        fetchEvents()
    }

    fun fetchEvents() {
        viewModelScope.launch {
            _feedState.value = FeedState.Loading
            try {
                // Fetch events from the Supabase "Events" table
                val events = supabaseClient.postgrest["events"]
                    .select()
                    .decodeList<Event>()
                
                _feedState.value = FeedState.Success(events)
            } catch (e: Exception) {
                _feedState.value = FeedState.Error(e.message ?: "Failed to fetch events")
            }
        }
    }
}



