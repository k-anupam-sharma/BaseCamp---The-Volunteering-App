package com.example.basecamp.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Ticket(
    val id: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("event_id") val eventId: String,
    @SerialName("volunteer_id") val volunteerId: String,
    val status: String = "Pending"
)
