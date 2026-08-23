package com.example.basecamp.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommentLike(
    @SerialName("comment_id") val commentId: String,
    @SerialName("user_id") val userId: String
)
