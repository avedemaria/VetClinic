package com.example.vetclinic.domain.usecases

import com.example.vetclinic.domain.entities.doctor.Doctor
import com.example.vetclinic.domain.interfaces.Repository
import jakarta.inject.Inject

class DoctorsUseCase @Inject constructor(private val repository: Repository) {

    suspend fun getDoctorList(): Result<List<Doctor>> {
        return repository.getDoctorList()
    }

}