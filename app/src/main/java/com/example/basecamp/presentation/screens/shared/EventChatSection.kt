package com.example.basecamp.presentation.screens.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.basecamp.presentation.components.GlassPanel
import com.example.basecamp.presentation.components.GlacierTextField

@Composable
fun EventChatSection(
    chatState: ChatState,
    eventOwnerId: String,
    onAddComment: (text: String, parentId: String?) -> Unit,
    onToggleLike: (commentId: String) -> Unit,
    onRefresh: () -> Unit,
    headerContent: @Composable () -> Unit = {}
) {
    var newCommentText by remember { mutableStateOf("") }
    var replyingToId by remember { mutableStateOf<String?>(null) }
    var replyingToName by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        
        headerContent()
        
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Comments",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Refresh",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF7DD3FC), // Hot Pink
                modifier = Modifier.clickable { onRefresh() }
            )
        }
        
        HorizontalDivider(color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground, thickness = 2.dp)
        Spacer(modifier = Modifier.height(16.dp))

        // Comments List
        Box(modifier = Modifier.weight(1f)) {
            when (chatState) {
                is ChatState.Loading -> {
                    CircularProgressIndicator(
                        color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is ChatState.Error -> {
                    Text(
                        text = chatState.message,
                        color = Color(0xFF7DD3FC),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is ChatState.Success -> {
                    if (chatState.comments.isEmpty()) {
                        Text(
                            text = "No comments yet",
                            color = Color.LightGray,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(chatState.comments) { commentWithLikes ->
                                CommentNode(
                                    commentNode = commentWithLikes,
                                    eventOwnerId = eventOwnerId,
                                    onReplyClick = { id, name ->
                                        replyingToId = id
                                        replyingToName = name
                                    },
                                    onLikeClick = { id ->
                                        onToggleLike(id)
                                    },
                                    level = 0
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Input Area
        GlassPanel {
            Column(modifier = Modifier.padding(16.dp)) {
                if (replyingToId != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Replying to $replyingToName",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF7DD3FC)
                        )
                        Text(
                            text = "Cancel",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.LightGray,
                            modifier = Modifier.clickable {
                                replyingToId = null
                                replyingToName = null
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        GlacierTextField(
                            value = newCommentText,
                            onValueChange = { newCommentText = it },
                            placeholder = "Write a comment..."
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (newCommentText.isNotBlank()) {
                                onAddComment(newCommentText, replyingToId)
                                newCommentText = ""
                                replyingToId = null
                                replyingToName = null
                            }
                        },
                        modifier = Modifier
                            .clip(androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
                            .background(Color(0xFFD3A270)) // Bronze tone
                            .padding(8.dp)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun CommentNode(
    commentNode: CommentWithLikes,
    eventOwnerId: String,
    onReplyClick: (String, String) -> Unit,
    onLikeClick: (String) -> Unit,
    level: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (level == 0) {
                    Modifier.background(androidx.compose.material3.MaterialTheme.colorScheme.onBackground).padding(12.dp)
                } else {
                    Modifier.padding(start = 16.dp, top = 12.dp)
                }
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            if (level > 0) {
                // Visual indicator for replies
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(4.dp)
                        .background(Color(0xFF7DD3FC)) // Hot Pink
                )
                Spacer(modifier = Modifier.width(12.dp))
            }
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = commentNode.comment.userName,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground
                    )
                    
                    if (commentNode.comment.userId == eventOwnerId) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFFAFF00)) // Electric Yellow
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "Organizer",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = commentNode.comment.text,
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (commentNode.isLikedByMe) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (commentNode.isLikedByMe) Color(0xFF7DD3FC) else Color.LightGray,
                        modifier = Modifier
                            .size(16.dp)
                            .clickable { commentNode.comment.id?.let { onLikeClick(it) } }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${commentNode.likes}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.LightGray
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Reply",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.clickable {
                            commentNode.comment.id?.let { onReplyClick(it, commentNode.comment.userName) }
                        }
                    )
                }
            }
        }
        
        if (commentNode.replies.isNotEmpty()) {
            commentNode.replies.forEach { reply ->
                CommentNode(
                    commentNode = reply,
                    eventOwnerId = eventOwnerId,
                    onReplyClick = onReplyClick,
                    onLikeClick = onLikeClick,
                    level = level + 1
                )
            }
        }
    }
}

