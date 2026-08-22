package com.example.baseccamp.domain.repository

interface UserRepository {
    suspend fun saveDeviceToken(userId: String, token: String): Result<Unit>
}

