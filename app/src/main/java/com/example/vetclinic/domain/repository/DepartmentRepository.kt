package com.example.vetclinic.domain.repository

import com.example.vetclinic.domain.entities.department.Department

interface DepartmentRepository {

    suspend fun getDepartmentList(): Result<List<Department>>
}