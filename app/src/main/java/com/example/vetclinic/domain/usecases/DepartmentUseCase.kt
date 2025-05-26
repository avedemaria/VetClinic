package com.example.vetclinic.domain.usecases

import com.example.vetclinic.domain.entities.department.Department
import com.example.vetclinic.domain.repository.DepartmentRepository
import jakarta.inject.Inject

class DepartmentUseCase @Inject constructor(private val repository: DepartmentRepository) {

    suspend fun getDepartmentList(): Result<List<Department>> {
        return repository.getDepartmentList()
    }
}