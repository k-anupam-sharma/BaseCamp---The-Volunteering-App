package com.example.basecamp.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Notification(
    val id: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("user_id") val userId: String,
    val title: String,
    val message: String,
    @SerialName("is_read") val isRead: Boolean = false
)