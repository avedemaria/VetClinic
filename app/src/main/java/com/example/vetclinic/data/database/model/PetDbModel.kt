package com.example.vetclinic.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "pets",
/*    foreignKeys = [ForeignKey(
        entity = UserDbModel::class,
        parentColumns = ["uid"],
        childColumns = ["user_id"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["user_id"])]*/
)
data class PetDbModel(
    @PrimaryKey
    @ColumnInfo(name = "pet_id") val petId: String,
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "pet_name") val petName: String,
    @ColumnInfo(name = "pet_bday") val petBday: String? = null,
    @ColumnInfo(name = "pet_type") val petType: String? = null,
    @ColumnInfo(name = "pet_gender") val petGender: String? = null,
    @ColumnInfo(name = "pet_age") val petAge: String? = null
)