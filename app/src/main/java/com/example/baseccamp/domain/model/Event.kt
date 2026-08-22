package com.example.baseccamp.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Event(
    val id: String,
    val title: String,
    val description: String = "",
    val cause: String,
    val location: String,
    val date: String,
    val orgName: String
)

