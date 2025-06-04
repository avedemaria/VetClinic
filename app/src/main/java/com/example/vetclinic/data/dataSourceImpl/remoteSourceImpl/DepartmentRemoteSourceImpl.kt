package com.example.vetclinic.data.dataSourceImpl.remoteSourceImpl

import com.example.vetclinic.data.DataSourceUtils
import com.example.vetclinic.data.remoteSource.interfaces.DepartmentRemoteSource
import com.example.vetclinic.data.remoteSource.network.SupabaseApiService
import com.example.vetclinic.data.remoteSource.network.model.DepartmentDto
import jakarta.inject.Inject

class DepartmentRemoteSourceImpl @Inject constructor(
    private val supabaseApiService: SupabaseApiService,
) : DepartmentRemoteSource {

    override suspend fun getDepartmentList(): Result<List<DepartmentDto>> {
        return DataSourceUtils.executeApiCall { supabaseApiService.getDepartments() }
    }
}