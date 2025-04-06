package com.example.vetclinic.data

import com.example.vetclinic.domain.entities.Doctor
import com.example.vetclinic.domain.entities.Pet
import com.example.vetclinic.domain.entities.Service
import com.example.vetclinic.domain.entities.User

interface DetailedInfo {

    suspend fun getDoctorById(doctorId: String): Doctor

    suspend fun getServiceById(serviceId: String): Service

    suspend fun getPetFromSupabaseById(petId: String): Pet

    suspend fun getUserFromSupabaseById(userId: String): User
}