package com.example.basecamp.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Event(
    val id: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("org_id") val orgId: String,
    val title: String,
    val description: String = "",
    val cause: String,
    val location: String,
    val date: String,
    @SerialName("org_name") val orgName: String,
    @SerialName("max_volunteers") val maxVolunteers: Int = 0,
    @SerialName("type_of_work") val typeOfWork: String = "",
    val payment: String = "",
    @SerialName("dress_code") val dressCode: String = "",
    @SerialName("contact_details") val contactDetails: String = ""
)
