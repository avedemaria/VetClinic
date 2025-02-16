package com.example.vetclinic.data.database.model

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [UserDbModel::class], version = 1, exportSchema = false)
abstract class VetClinicDatabase : RoomDatabase() {

    companion object {
        private var INSTANCE: VetClinicDatabase? = null
        private val DB_NAME = "VetClinicDb"
        private val LOCK = Any()

        fun getInstance(application: Application): VetClinicDatabase {


            INSTANCE?.let {
                return it
            }


            synchronized(LOCK) {
                INSTANCE?.let {
                    return it
                }
            }

            val database =
                Room.databaseBuilder(application, VetClinicDatabase::class.java, DB_NAME).build()
            INSTANCE = database
            return database

        }
    }

    abstract fun vetClinicDao(): VetClinicDao


}