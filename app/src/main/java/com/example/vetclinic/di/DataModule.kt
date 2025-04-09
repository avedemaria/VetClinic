package com.example.vetclinic.di

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import com.example.vetclinic.BuildConfig
import com.example.vetclinic.data.database.model.VetClinicDao
import com.example.vetclinic.data.database.model.VetClinicDatabase
import com.example.vetclinic.data.network.SupabaseApiFactory
import com.example.vetclinic.data.network.SupabaseApiService
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
            install(Auth)
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
    fun provideSupabaseDb(): SupabaseApiService {
        return SupabaseApiFactory.apiService
    }

    @Provides
    @Singleton
    fun provideVetClinicDatabase(context: Context): VetClinicDatabase {
        return Room.databaseBuilder(
            context,
            VetClinicDatabase::class.java,
            "VetClinicDb"
        )
            .fallbackToDestructiveMigration()  // Обрабатываем миграции, если что-то не так
            .build()
    }


    @Provides
    @Singleton
    fun provideContext(application: Application): Context {
        return application.applicationContext
    }



    @Provides
    @Singleton
    fun provideVetClinicDao(db: VetClinicDatabase): VetClinicDao {
        return db.vetClinicDao()
    }

    @Provides
    @Singleton
    fun provideUserDataStore(application: Application): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            produceFile = { application.preferencesDataStoreFile("user_prefs") }
        )
    }


//
//    @Provides
//    @Singleton
//    fun provideAppointmentRemoteMediatorFactory(
//        vetClinicDao: VetClinicDao,
//        supabaseApiService: SupabaseApiService,
//        appointmentMapper: AppointmentMapper,
//        petMapper: PetMapper,
//        userMapper: UserMapper,
//        doctorMapper: DoctorMapper,
//        serviceMapper: ServiceMapper
//
//        ): AppointmentRemoteMediatorFactory {
//        return AppointmentRemoteMediatorFactory(
//            vetClinicDao = vetClinicDao,
//            supabaseApiService = supabaseApiService,
//            appointmentMapper = appointmentMapper,
//            petMapper = petMapper,
//            userMapper = userMapper,
//            doctorMapper = doctorMapper,
//            serviceMapper = serviceMapper
//
//        )
//    }


}