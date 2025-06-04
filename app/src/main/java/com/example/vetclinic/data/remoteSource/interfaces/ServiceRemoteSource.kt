package com.example.vetclinic.data.remoteSource.interfaces

import com.example.vetclinic.data.remoteSource.network.model.ServiceDto
import com.example.vetclinic.domain.entities.service.Service

interface ServiceRemoteSource {

    suspend fun getServiceList(): Result<List<ServiceDto>>

    suspend fun getServicesByDepartmentId(departmentId: String): Result<List<ServiceDto>>
}