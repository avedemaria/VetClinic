package com.example.vetclinic.data.mapper

import com.example.vetclinic.data.remoteSource.network.model.ServiceDto
import com.example.vetclinic.domain.entities.service.Service
import jakarta.inject.Inject

class ServiceMapper @Inject constructor() {

    fun serviceDtoToServiceEntity(serviceDto: ServiceDto): Service {
        return Service(
            id = serviceDto.id,
            serviceName = serviceDto.serviceName,
            price = serviceDto.price,
            departmentId = serviceDto.departmentId,
            duration = serviceDto.duration

        )
    }

    fun serviceDtoListToServiceEntityList(serviceDtoList: List<ServiceDto>): List<Service> {
        return serviceDtoList.map(::serviceDtoToServiceEntity)
    }
}