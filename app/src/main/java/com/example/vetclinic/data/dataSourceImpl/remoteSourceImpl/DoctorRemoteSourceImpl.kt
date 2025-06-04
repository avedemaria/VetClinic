package com.example.vetclinic.data.dataSourceImpl.remoteSourceImpl

import com.example.vetclinic.data.DataSourceUtils
import com.example.vetclinic.data.remoteSource.interfaces.DoctorRemoteSource
import com.example.vetclinic.data.remoteSource.network.SupabaseApiService
import com.example.vetclinic.data.remoteSource.network.model.DoctorDto
import jakarta.inject.Inject

class DoctorRemoteSourceImpl @Inject constructor(
    private val supabaseApiService: SupabaseApiService,
) : DoctorRemoteSource {

    override suspend fun getDoctorList(): Result<List<DoctorDto>> {
        return DataSourceUtils.executeApiCall { supabaseApiService.getDoctors() }
    }
}