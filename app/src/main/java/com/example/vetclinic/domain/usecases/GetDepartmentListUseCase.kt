package com.example.vetclinic.domain.usecases

import com.example.vetclinic.domain.interfaces.Repository
import com.example.vetclinic.domain.entities.Department
import jakarta.inject.Inject

class GetDepartmentListUseCase @Inject constructor(private val repository: Repository) {

    suspend fun getDepartmentList(): Result<List<Department>> {
        return repository.getDepartmentList()
    }
}