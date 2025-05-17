package com.example.vetclinic.domain.usecases

import android.util.Log
import com.example.vetclinic.domain.repository.Repository
import com.example.vetclinic.domain.entities.service.Service
import jakarta.inject.Inject

class ServiceUseCase @Inject constructor(private val repository: Repository) {

    suspend fun getServiceList(): Result<List<Service>> {
        return repository.getServiceList()
    }


    suspend fun getServicesByDepartmentId(departmentId: String): Result<List<Service>> {
        return repository.getServicesByDepartmentId(departmentId)
    }
}