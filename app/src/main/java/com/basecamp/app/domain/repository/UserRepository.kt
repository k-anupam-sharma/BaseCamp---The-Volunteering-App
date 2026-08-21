package com.basecamp.app.domain.repository

interface UserRepository {
    suspend fun saveDeviceToken(userId: String, token: String): Result<Unit>
}
