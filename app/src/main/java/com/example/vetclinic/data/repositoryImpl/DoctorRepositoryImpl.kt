package com.example.vetclinic.data.repositoryImpl

import com.example.vetclinic.data.mapper.DoctorMapper
import com.example.vetclinic.data.remoteSource.interfaces.DoctorRemoteSource
import com.example.vetclinic.domain.entities.doctor.Doctor
import com.example.vetclinic.domain.repository.DoctorRepository
import jakarta.inject.Inject

class DoctorRepositoryImpl @Inject constructor(
    private val remoteSource: DoctorRemoteSource,
    private val doctorMapper: DoctorMapper,
) : DoctorRepository {

    override suspend fun getDoctorList(): Result<List<Doctor>> =
        remoteSource.getDoctorList().mapCatching {
            doctorMapper.doctorDtoListToDoctorEntityList(it)
        }
}