package com.example.vetclinic.data.database.model

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.vetclinic.CodeReview

@Database(entities = [UserDbModel::class], version = 1, exportSchema = false)
abstract class VetClinicDatabase : RoomDatabase() {

    companion object {
        @CodeReview("Чтобы переменная не кешировалась в потоках и всегда читалась из памяти" +
                "Если не сделать, то один поток обновит значение, а второй все еще видит старое из кеша")
        @Volatile
        private var INSTANCE: VetClinicDatabase? = null
        private val DB_NAME = "VetClinicDb"

        @CodeReview("Паттерн double-checked locking")
        fun getInstance(application: Application): VetClinicDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    // потенциально может быть передан сервис, активити и т.п.
                    // Приведет к утечке памяти, если Room будет держать ссылку на активити
                    application.applicationContext,
                    VetClinicDatabase::class.java,
                    DB_NAME
                ).build().also { INSTANCE = it }
            }
        }
    }

//    INSTANCE?.let {
//        return it
//    }
//
//
//    synchronized(LOCK) {
//        INSTANCE?.let {
//            return it
//        }
//    }
//  Два потока могут одновременно зайти сюда и создать инстансы базы
//    val database =
//        Room.databaseBuilder(application, VetClinicDatabase::class.java, DB_NAME).build()
//    INSTANCE = database
//    return database

    abstract fun vetClinicDao(): VetClinicDao
}