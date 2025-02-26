package com.example.vetclinic.domain.usecases

import com.example.vetclinic.domain.Repository
import com.example.vetclinic.domain.entities.Doctor
import jakarta.inject.Inject

class GetDoctorListUseCase @Inject constructor(private val repository: Repository) {

    suspend fun getDoctorList(): Result<List<Doctor>> {
        return repository.getDoctorList()
    }

}