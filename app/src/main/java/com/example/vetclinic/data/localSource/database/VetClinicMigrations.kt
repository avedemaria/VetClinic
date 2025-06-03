package com.example.vetclinic.data.localSource.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object VetClinicMigrations {

    val Migration_1_2 = object: Migration (1,2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("""
            CREATE TABLE pets_new (
                pet_id TEXT NOT NULL,
                user_id TEXT NOT NULL,
                pet_name TEXT NOT NULL,
                pet_birthday TEXT,
                pet_type TEXT,
                pet_gender TEXT,
                PRIMARY KEY(pet_id),
                FOREIGN KEY(user_id) REFERENCES users(uid) ON DELETE CASCADE
            )
        """.trimIndent())

            db.execSQL("""
            INSERT INTO pets_new (
                pet_id, user_id, pet_name, pet_birthday, pet_type, pet_gender
            )
            SELECT 
                pet_id, user_id, pet_name, pet_bday, pet_type, pet_gender
            FROM pets
        """.trimIndent())

            db.execSQL("DROP TABLE pets")

            db.execSQL("ALTER TABLE pets_new RENAME TO pets")

            db.execSQL("CREATE INDEX index_pets_user_id ON pets(user_id)")
        }
    }
}