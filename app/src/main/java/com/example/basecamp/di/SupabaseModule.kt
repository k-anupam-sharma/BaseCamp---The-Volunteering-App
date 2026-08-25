package com.example.basecamp.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.serializer.KotlinXSerializer
import kotlinx.serialization.json.Json
import javax.inject.Singleton
import kotlin.time.Duration.Companion.seconds

@Module
@InstallIn(SingletonComponent::class)
object SupabaseModule {

    @Provides
    @Singleton
    fun provideSupabaseClient(): SupabaseClient {
        val supabaseUrl = com.example.basecamp.BuildConfig.SUPABASE_URL
        val supabaseKey = com.example.basecamp.BuildConfig.SUPABASE_KEY
        
        return createSupabaseClient(
            supabaseUrl = supabaseUrl,
            supabaseKey = supabaseKey
        ) {
            requestTimeout = 30.seconds
            defaultSerializer = KotlinXSerializer(
                Json {
                    ignoreUnknownKeys = true
                    coerceInputValues = true
                    encodeDefaults = false
                    explicitNulls = false
                    isLenient = true
                }
            )
            install(Auth) {
                scheme = "basecamp"
                host = "login-callback"
            }
            install(io.github.jan.supabase.compose.auth.ComposeAuth)
            install(Postgrest)
            install(Storage)
        }
    }
}



