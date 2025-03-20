package com.example.vetclinic.di

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.example.vetclinic.BuildConfig
import com.example.vetclinic.data.UserDataStoreImpl
import com.example.vetclinic.data.database.model.VetClinicDao
import com.example.vetclinic.data.database.model.VetClinicDatabase
import com.example.vetclinic.data.network.SupabaseApiFactory
import com.example.vetclinic.data.network.SupabaseApiService
import com.example.vetclinic.domain.UserDataStore
import dagger.Module
import dagger.Provides
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import jakarta.inject.Singleton


@Module
class DataModule {


    @Provides
    @Singleton
    fun provideSupabaseClient(): SupabaseClient {
        return createSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_KEY
        ) {
            install(Auth)
//            install(Postgrest)
        }
    }


    @Provides
    @Singleton
    fun provideSupabaseDb(): SupabaseApiService {
        return SupabaseApiFactory.apiService
    }

    @Provides
    @Singleton
    fun provideVetClinicDao(application: Application): VetClinicDao {
        return VetClinicDatabase.getInstance(application).vetClinicDao()
    }

    @Provides
    @Singleton
    fun provideUserDataStore(application: Application): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            produceFile = { application.preferencesDataStoreFile("user_prefs") }
        )
    }

}