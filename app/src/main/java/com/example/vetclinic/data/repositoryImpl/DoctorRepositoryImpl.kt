package com.example.vetclinic.data.repositoryImpl

import com.example.vetclinic.data.mapper.DoctorMapper
import com.example.vetclinic.data.remoteSource.network.SupabaseApiService
import com.example.vetclinic.domain.entities.doctor.Doctor
import com.example.vetclinic.domain.repository.DoctorRepository
import jakarta.inject.Inject

class DoctorRepositoryImpl @Inject constructor(
    private val supabaseApiService: SupabaseApiService,
    private val doctorMapper: DoctorMapper,
) : DoctorRepository {

    override suspend fun getDoctorList(): Result<List<Doctor>> = RepositoryUtils.fetchData(
        apiCall = { supabaseApiService.getDoctors() },
        mapper = { body -> doctorMapper.doctorDtoListToDoctorEntityList(body) },
        DOCTOR_LIST_TAG
    )

    companion object {
        private const val DOCTOR_LIST_TAG = "doctors"
    }
}