package com.example.vetclinic.data.network.model

data class DoctorWithDepartmentDto (
    val doctorDto: List<DoctorDto>,
    val departmentDto: DepartmentDto
)