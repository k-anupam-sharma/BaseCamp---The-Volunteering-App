package com.example.basecamp.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    @SerialName("created_at") val createdAt: String? = null,
    val name: String,
    val role: String, // 'Volunteer' or 'Organization'
    val email: String
)
