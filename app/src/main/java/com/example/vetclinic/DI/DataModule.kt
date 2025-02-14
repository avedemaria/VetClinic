package com.example.vetclinic.DI

import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import io.github.jan.supabase.BuildConfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.SupabaseClientBuilder
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.createSupabaseClient
import jakarta.inject.Singleton


@Module
class DataModule {

    @Provides
    @Singleton
    fun provideSupabaseClient(): SupabaseClient {
        return createSupabaseClient(
            supabaseUrl = com.example.vetclinic.BuildConfig.SUPABASE_URL,
            supabaseKey = com.example.vetclinic.BuildConfig.SUPABASE_KEY
        ) {install(Auth)}
    }



    @Provides
    @Singleton
    fun provideFirebaseDb(): FirebaseDatabase {
        return FirebaseDatabase.getInstance()
    }

}