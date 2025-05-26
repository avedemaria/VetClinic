package com.example.vetclinic.data.localSource.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.vetclinic.data.localSource.database.models.AppointmentWithDetailsDbModel
import com.example.vetclinic.data.localSource.database.models.PetDbModel
import com.example.vetclinic.data.localSource.database.models.UserDbModel

//@Database(
//    entities = [UserDbModel::class, PetDbModel::class, AppointmentWithDetailsDbModel::class],
//    version = 10,
//    exportSchema = false
//)
//abstract class VetClinicDatabase : RoomDatabase() {
    @Database(
        entities = [UserDbModel::class, PetDbModel::class, AppointmentWithDetailsDbModel::class],
        version = 13,
        exportSchema = false
    )
    abstract class VetClinicDatabase : RoomDatabase() {
        abstract fun vetClinicDao(): VetClinicDao
    }
//    companion object {
//        @Volatile   //"Чтобы переменная не кешировалась в потоках и всегда читалась из памяти" +
//        //"Если не сделать, то один поток обновит значение, а второй все еще видит старое из кеша")
//
//        private var INSTANCE: VetClinicDatabase? = null
//        private val DB_NAME = "VetClinicDb"
//
//        fun getInstance(application: Application): VetClinicDatabase {
//
//            return INSTANCE ?: synchronized(this) {
//                INSTANCE ?: Room.databaseBuilder(
//                    application.applicationContext,
//                    VetClinicDatabase::class.java, DB_NAME
//                ).fallbackToDestructiveMigration().build().also { INSTANCE = it }
//
//            }
//        }
//    }

//
//    abstract fun vetClinicDao(): VetClinicDao
//
//
//}