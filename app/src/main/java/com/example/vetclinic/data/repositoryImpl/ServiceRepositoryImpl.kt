package com.example.vetclinic.data.repositoryImpl

import android.util.Log
import com.example.vetclinic.data.RepositoryUtils
import com.example.vetclinic.data.mapper.ServiceMapper
import com.example.vetclinic.data.remoteSource.network.SupabaseApiService
import com.example.vetclinic.domain.entities.service.Service
import com.example.vetclinic.domain.repository.ServiceRepository
import jakarta.inject.Inject

class ServiceRepositoryImpl @Inject constructor(
    private val supabaseApiService: SupabaseApiService,
    private val serviceMapper: ServiceMapper
): ServiceRepository {

    override suspend fun getServiceList(): Result<List<Service>> =
        RepositoryUtils.fetchData(
            apiCall = { supabaseApiService.getServices() },
            mapper = { serviceMapper.serviceDtoListToServiceEntityList(it) },
            SERVICE_LIST_TAG
        )

    override suspend fun getServicesByDepartmentId(departmentId: String): Result<List<Service>> =
        kotlin.runCatching {

            val departmentIdWithParam = "eq.$departmentId"

            Log.d(TAG, "Fetching services for department: $departmentIdWithParam")
            val response = supabaseApiService.getServicesByDepartmentId(departmentIdWithParam)
            val servicesDto = response.body()
                ?: throw Exception("Empty response body:${response.code()} - ${response.message()}")
            if (response.isSuccessful) {
                Log.d(TAG, "Successfully fetched services by $departmentId in Supabase DB")
                serviceMapper.serviceDtoListToServiceEntityList(servicesDto)
            } else {
                throw Exception("Server's error: ${response.code()} - ${response.message()}")
            }
        }
            .onFailure { e -> Log.e(TAG, "Error fetching serviced {$e.message}") }



    companion object {
        private const val TAG = "ServiceRepositoryImpl"
        private const val SERVICE_LIST_TAG = "services"
    }
}