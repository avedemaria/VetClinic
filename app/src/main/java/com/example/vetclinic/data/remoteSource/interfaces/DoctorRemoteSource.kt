package com.example.vetclinic.data.remoteSource.interfaces

import com.example.vetclinic.data.remoteSource.network.model.DoctorDto

interface DoctorRemoteSource {

    suspend fun getDoctorList(): Result<List<DoctorDto>>
}