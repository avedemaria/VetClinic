package com.example.vetclinic.domain.selectDoctorFeature

import com.example.vetclinic.domain.Repository
import jakarta.inject.Inject

class GetDoctorListUseCase @Inject constructor(private val repository: Repository) {

    suspend fun getDoctorList(): List<Doctor> {
        return repository.getDoctorList()
    }

}