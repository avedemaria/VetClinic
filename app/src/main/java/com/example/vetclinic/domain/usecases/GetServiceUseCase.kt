package com.example.vetclinic.domain.usecases

import android.util.Log
import com.example.vetclinic.domain.interfaces.Repository
import com.example.vetclinic.domain.entities.Service
import jakarta.inject.Inject

class GetServiceUseCase @Inject constructor(private val repository: Repository) {

    suspend fun getServiceList(): Result<List<Service>> {
        return repository.getServiceList()
    }


    suspend fun getServicesByDepartmentId(departmentId: String): Result<List<Service>> {
        Log.d(
            "GetServiceUseCase",
            "Inside UseCase: fetching services for department: $departmentId"
        )
        return repository.getServicesByDepartmentId(departmentId)
    }
}