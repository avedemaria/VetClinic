package com.example.vetclinic.domain.usecases

import com.example.vetclinic.domain.entities.doctor.Doctor
import com.example.vetclinic.domain.repository.DoctorRepository
import jakarta.inject.Inject

class DoctorsUseCase @Inject constructor(private val repository: DoctorRepository) {

    suspend fun getDoctorList(): Result<List<Doctor>> {
        return repository.getDoctorList()
    }

}