package com.example.vetclinic.data.localSource.database

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.vetclinic.data.localSource.database.models.AppointmentWithDetailsDbModel
import com.example.vetclinic.data.localSource.database.models.PetDbModel
import com.example.vetclinic.data.localSource.database.models.UserDbModel

@Database(
    entities = [UserDbModel::class, PetDbModel::class, AppointmentWithDetailsDbModel::class],
    version = 2,
    exportSchema = false
)
abstract class VetClinicDatabase : RoomDatabase() {
    companion object {
        @Volatile
        private var INSTANCE: VetClinicDatabase? = null
        private val DB_NAME = "VetClinicDb_1"

        fun getInstance(application: Application): VetClinicDatabase {

            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    application.applicationContext,
                    VetClinicDatabase::class.java, DB_NAME
                ).addMigrations(VetClinicMigrations.Migration_1_2)
                    .build().also { INSTANCE = it }

            }
        }
    }

    abstract fun vetClinicDao(): VetClinicDao

}