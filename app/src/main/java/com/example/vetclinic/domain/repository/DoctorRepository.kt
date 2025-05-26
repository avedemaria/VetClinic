package com.example.vetclinic.domain.repository

import com.example.vetclinic.domain.entities.doctor.Doctor

interface DoctorRepository {
    suspend fun getDoctorList(): Result<List<Doctor>>
}