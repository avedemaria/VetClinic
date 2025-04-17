package com.example.vetclinic.data.mapper

import com.example.vetclinic.data.network.model.DepartmentDto
import com.example.vetclinic.domain.entities.department.Department
import jakarta.inject.Inject

class DepartmentMapper @Inject constructor() {


    fun departmentDtoToDepartmentEntity(departmentDto: DepartmentDto): Department {
        return Department(
            id = departmentDto.departmentId,
            name = departmentDto.departmentName
        )
    }

    fun departmentDtoListToDepartmentEntityList(departmentDtoList: List<DepartmentDto>):
            List<Department> {
        return departmentDtoList.map(::departmentDtoToDepartmentEntity)
    }
}