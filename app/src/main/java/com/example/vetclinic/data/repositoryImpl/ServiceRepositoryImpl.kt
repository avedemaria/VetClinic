package com.example.vetclinic.data.repositoryImpl

import com.example.vetclinic.data.mapper.ServiceMapper
import com.example.vetclinic.data.remoteSource.interfaces.ServiceRemoteSource
import com.example.vetclinic.domain.entities.service.Service
import com.example.vetclinic.domain.repository.ServiceRepository
import jakarta.inject.Inject

class ServiceRepositoryImpl @Inject constructor(
    private val remoteSource: ServiceRemoteSource,
    private val serviceMapper: ServiceMapper,
) : ServiceRepository {

    override suspend fun getServiceList(): Result<List<Service>> =
        remoteSource.getServiceList().mapCatching {
            serviceMapper.serviceDtoListToServiceEntityList(it)
        }

    override suspend fun getServicesByDepartmentId(departmentId: String): Result<List<Service>> =
       remoteSource.getServicesByDepartmentId(departmentId).mapCatching {
           serviceMapper.serviceDtoListToServiceEntityList(it)
       }

}