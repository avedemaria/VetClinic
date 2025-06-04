package com.example.vetclinic.data.repositoryImpl

import com.example.vetclinic.data.mapper.DepartmentMapper
import com.example.vetclinic.data.remoteSource.interfaces.DepartmentRemoteSource
import com.example.vetclinic.domain.entities.department.Department
import com.example.vetclinic.domain.repository.DepartmentRepository
import jakarta.inject.Inject

class DepartmentRepositoryImpl @Inject constructor(
    private val remoteSource: DepartmentRemoteSource,
    private val departmentMapper: DepartmentMapper,
) : DepartmentRepository {

    override suspend fun getDepartmentList(): Result<List<Department>> =
        remoteSource.getDepartmentList().mapCatching {
            departmentMapper.departmentDtoListToDepartmentEntityList(it)
        }

}