package com.example.vetclinic.data.database.model

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [UserDbModel::class], version = 1, exportSchema = false)
abstract class VetClinicDatabase : RoomDatabase() {

    companion object {
        @Volatile   //"Чтобы переменная не кешировалась в потоках и всегда читалась из памяти" +
        //"Если не сделать, то один поток обновит значение, а второй все еще видит старое из кеша")

        private var INSTANCE: VetClinicDatabase? = null
        private val DB_NAME = "VetClinicDb"

        fun getInstance(application: Application): VetClinicDatabase {

            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    application.applicationContext,
                    VetClinicDatabase::class.java, DB_NAME
                ).build().also { INSTANCE = it }

            }
        }
    }

    abstract fun vetClinicDao(): VetClinicDao


}