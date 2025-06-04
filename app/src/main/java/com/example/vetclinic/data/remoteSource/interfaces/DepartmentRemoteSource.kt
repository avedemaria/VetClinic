package com.example.vetclinic.data.remoteSource.interfaces

import com.example.vetclinic.data.remoteSource.network.model.DepartmentDto

interface DepartmentRemoteSource {

    suspend fun getDepartmentList(): Result<List<DepartmentDto>>
}