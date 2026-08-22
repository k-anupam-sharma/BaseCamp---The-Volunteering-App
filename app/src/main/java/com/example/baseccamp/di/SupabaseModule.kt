package com.example.baseccamp.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SupabaseModule {

    @Provides
    @Singleton
    fun provideSupabaseClient(): SupabaseClient {
        // TODO: Replace with your actual Supabase URL and Anon Key (e.g. from BuildConfig or local.properties)
        val supabaseUrl = "https://PLACEHOLDER.supabase.co"
        val supabaseKey = "PLACEHOLDER_KEY"
        
        return createSupabaseClient(
            supabaseUrl = supabaseUrl,
            supabaseKey = supabaseKey
        ) {
            install(Auth)
            install(Postgrest)
        }
    }
}

