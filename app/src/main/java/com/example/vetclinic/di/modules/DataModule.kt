package com.example.vetclinic.di.modules

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import com.example.vetclinic.BuildConfig
import com.example.vetclinic.data.localSource.database.VetClinicDao
import com.example.vetclinic.data.localSource.database.VetClinicDatabase
import com.example.vetclinic.data.remoteSource.network.HeaderInterceptor
import com.example.vetclinic.data.remoteSource.network.SupabaseApiFactory
import com.example.vetclinic.data.remoteSource.network.SupabaseApiService
import com.example.vetclinic.data.remoteSource.network.model.AuthInterceptor
import com.example.vetclinic.di.qualifiers.DialogPrefs
import com.example.vetclinic.di.qualifiers.UserPrefs
import com.example.vetclinic.domain.repository.UserDataStore
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
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
            install(Auth) {}
            install(Postgrest)
            install(Realtime)
        }
    }

    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    }


    @Provides
    @Singleton
    fun provideSupabaseDb(
        authInterceptor: AuthInterceptor,
    ): SupabaseApiService {
        return SupabaseApiFactory(authInterceptor).apiService
    }


//    @Provides
//    @Singleton
//    fun provideVetClinicDatabase(context: Context): VetClinicDatabase {
//        return Room.databaseBuilder(
//            context,
//            VetClinicDatabase::class.java,
//            "VetClinicDb"
//        )
//            .fallbackToDestructiveMigration()
//            .build()
//    }


    @Provides
    @Singleton
    fun provideContext(application: Application): Context {
        return application.applicationContext
    }


    @Provides
    @Singleton
    fun provideVetClinicDao(application: Application): VetClinicDao {
        return VetClinicDatabase.getInstance(application).vetClinicDao()
    }

    @Provides
    @Singleton
    @UserPrefs
    fun provideUserDataStore(application: Application): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            produceFile = { application.preferencesDataStoreFile("user_prefs") }
        )
    }

    @Provides
    @Singleton
    @DialogPrefs
    fun provideDialogDataStore(application: Application): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            produceFile = { application.preferencesDataStoreFile("dialog_prefs") }
        )
    }

}