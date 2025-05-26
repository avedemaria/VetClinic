package com.example.vetclinic.domain.repository

import com.example.vetclinic.domain.entities.service.Service

interface ServiceRepository {

    suspend fun getServiceList(): Result<List<Service>>

    suspend fun getServicesByDepartmentId(departmentId: String): Result<List<Service>>

}