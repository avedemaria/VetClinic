package com.example.vetclinic.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(
    tableName = "appointments",
    indices = [Index(value = ["user_id"]), Index(value = ["is_archived"])]
)

data class AppointmentWithDetailsDbModel(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "pet_id") val petId: String,
    @ColumnInfo(name = "doctor_id") val doctorId: String,
    @ColumnInfo(name = "service_id") val serviceId: String,
    @ColumnInfo(name = "date_time") val dateTime: String,
    @ColumnInfo(name = "status") val status: String,
    @ColumnInfo(name = "is_archived") val isArchived: Boolean,
    @ColumnInfo(name = "is_confirmed") val isConfirmed: Boolean,
    @ColumnInfo(name = "service_name") val serviceName: String,
    @ColumnInfo(name = "doctor_name") val doctorName: String,
    @ColumnInfo(name = "doctor_role") val doctorRole: String,
    @ColumnInfo(name = "pet_name") val petName: String,
    @ColumnInfo(name = "user_name") val userName: String,
    @ColumnInfo(name = "user_last_name") val userLastName: String,
    @ColumnInfo(name = "pet_age") val petAge: String,
) {


}