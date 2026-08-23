package com.example.basecamp.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Comment(
    val id: String? = null,
    @SerialName("event_id") val eventId: String,
    @SerialName("user_id") val userId: String,
    @SerialName("user_name") val userName: String,
    val text: String,
    @SerialName("parent_comment_id") val parentCommentId: String? = null,
    @SerialName("created_at") val createdAt: String? = null
)
