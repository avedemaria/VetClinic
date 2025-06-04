package com.example.vetclinic.data.dataSourceImpl.remoteSourceImpl

import com.example.vetclinic.data.DataSourceUtils
import com.example.vetclinic.data.remoteSource.interfaces.ServiceRemoteSource
import com.example.vetclinic.data.remoteSource.network.SupabaseApiService
import com.example.vetclinic.data.remoteSource.network.model.ServiceDto
import jakarta.inject.Inject

class ServiceRemoteSourceImpl @Inject constructor(
    private val supabaseApiService: SupabaseApiService,
) : ServiceRemoteSource {

    override suspend fun getServiceList(): Result<List<ServiceDto>> {
        return DataSourceUtils.executeApiCall { supabaseApiService.getServices() }
    }

    override suspend fun getServicesByDepartmentId(departmentId: String): Result<List<ServiceDto>> {
        return DataSourceUtils.executeApiCall{ supabaseApiService
            .getServicesByDepartmentId("eq.$departmentId") }
    }
}