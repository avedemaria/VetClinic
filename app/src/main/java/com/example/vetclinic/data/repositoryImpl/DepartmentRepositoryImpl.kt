package com.example.vetclinic.data.repositoryImpl

import com.example.vetclinic.data.RepositoryUtils
import com.example.vetclinic.data.mapper.DepartmentMapper
import com.example.vetclinic.data.remoteSource.network.SupabaseApiService
import com.example.vetclinic.domain.entities.department.Department
import com.example.vetclinic.domain.repository.DepartmentRepository
import jakarta.inject.Inject

class DepartmentRepositoryImpl @Inject constructor(
    private val supabaseApiService: SupabaseApiService,
    private val departmentMapper: DepartmentMapper,
) : DepartmentRepository {

    override suspend fun getDepartmentList(): Result<List<Department>> =
        RepositoryUtils.fetchData(
            apiCall = { supabaseApiService.getDepartments() },
            mapper = { departmentMapper.departmentDtoListToDepartmentEntityList(it) },
            DEPARTMENT_LIST_TAG
        )

    companion object {
        private const val DEPARTMENT_LIST_TAG = "departments"
    }
}