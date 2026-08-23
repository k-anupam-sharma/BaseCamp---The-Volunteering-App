package com.example.basecamp.presentation.screens.shared

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.basecamp.domain.model.Comment
import com.example.basecamp.domain.model.CommentLike
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CommentWithLikes(
    val comment: Comment,
    val likes: Int,
    val isLikedByMe: Boolean,
    val replies: List<CommentWithLikes> = emptyList()
)

sealed class ChatState {
    object Loading : ChatState()
    data class Success(val comments: List<CommentWithLikes>) : ChatState()
    data class Error(val message: String) : ChatState()
}

@HiltViewModel
class EventChatViewModel @Inject constructor(
    private val supabaseClient: SupabaseClient
) : ViewModel() {

    private val _chatState = MutableStateFlow<ChatState>(ChatState.Loading)
    val chatState: StateFlow<ChatState> = _chatState.asStateFlow()

    private var currentEventId: String? = null

    val currentUserId: String
        get() = supabaseClient.auth.currentUserOrNull()?.id ?: ""

    val currentUserName: String
        get() = supabaseClient.auth.currentUserOrNull()?.userMetadata?.get("full_name")?.toString()?.replace("\"", "") ?: "Unknown User"

    fun loadComments(eventId: String) {
        currentEventId = eventId
        viewModelScope.launch {
            _chatState.value = ChatState.Loading
            try {
                // Fetch comments
                val comments = supabaseClient.postgrest["comments"]
                    .select {
                        filter {
                            eq("event_id", eventId)
                        }
                        order("created_at", Order.ASCENDING)
                    }.decodeList<Comment>()

                // Fetch likes for these comments
                val commentIds = comments.mapNotNull { it.id }
                val likes = if (commentIds.isNotEmpty()) {
                    supabaseClient.postgrest["comment_likes"]
                        .select {
                            filter {
                                isIn("comment_id", commentIds)
                            }
                        }.decodeList<CommentLike>()
                } else emptyList()

                val organizedComments = organizeComments(comments, likes, currentUserId)
                _chatState.value = ChatState.Success(organizedComments)
            } catch (e: Exception) {
                _chatState.value = ChatState.Error("Failed to load comments: ${e.message}")
            }
        }
    }

    private fun organizeComments(
        allComments: List<Comment>,
        allLikes: List<CommentLike>,
        userId: String
    ): List<CommentWithLikes> {
        val commentsById = allComments.associateBy { it.id }
        val likesByComment = allLikes.groupBy { it.commentId }

        fun buildTree(parentId: String?): List<CommentWithLikes> {
            return allComments
                .filter { it.parentCommentId == parentId }
                .map { comment ->
                    val likes = likesByComment[comment.id] ?: emptyList()
                    CommentWithLikes(
                        comment = comment,
                        likes = likes.size,
                        isLikedByMe = likes.any { it.userId == userId },
                        replies = buildTree(comment.id)
                    )
                }
        }

        return buildTree(null)
    }

    fun addComment(eventId: String, text: String, parentCommentId: String? = null) {
        viewModelScope.launch {
            try {
                // Fetch actual user name from users table
                var realName = "Unknown User"
                try {
                    val userObj = supabaseClient.postgrest["users"]
                        .select { filter { eq("id", currentUserId) } }
                        .decodeSingleOrNull<com.example.basecamp.domain.model.User>()
                    if (userObj != null && userObj.name.isNotBlank()) {
                        realName = userObj.name
                    }
                } catch (e: Exception) {
                    realName = currentUserName
                }

                val newComment = Comment(
                    eventId = eventId,
                    userId = currentUserId,
                    userName = realName,
                    text = text,
                    parentCommentId = parentCommentId
                )
                supabaseClient.postgrest["comments"].insert(newComment)
                loadComments(eventId) // Refresh
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun toggleLike(commentId: String) {
        viewModelScope.launch {
            try {
                val currentState = _chatState.value
                if (currentState is ChatState.Success) {
                    val userId = currentUserId
                    
                    // Optimistic update
                    // Wait, this is a bit complex for a simple app, let's just do the DB call and reload
                    
                    val likeObj = CommentLike(commentId, userId)
                    
                    // Check if already liked
                    val existingLike = supabaseClient.postgrest["comment_likes"]
                        .select {
                            filter {
                                eq("comment_id", commentId)
                                eq("user_id", userId)
                            }
                        }.decodeList<CommentLike>()
                        
                    if (existingLike.isNotEmpty()) {
                        supabaseClient.postgrest["comment_likes"].delete {
                            filter {
                                eq("comment_id", commentId)
                                eq("user_id", userId)
                            }
                        }
                    } else {
                        supabaseClient.postgrest["comment_likes"].insert(likeObj)
                    }
                    
                    currentEventId?.let { loadComments(it) }
                }
            } catch (e: Exception) {
                // Ignore for now
            }
        }
    }
}
