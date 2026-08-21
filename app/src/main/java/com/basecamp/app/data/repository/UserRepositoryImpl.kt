package com.basecamp.app.data.repository

import com.basecamp.app.domain.repository.UserRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val supabaseClient: SupabaseClient
) : UserRepository {

    override suspend fun saveDeviceToken(userId: String, token: String): Result<Unit> {
        return try {
            supabaseClient.postgrest["Users"].update(
                {
                    set("fcm_token", token)
                }
            ) {
                filter {
                    eq("id", userId)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
