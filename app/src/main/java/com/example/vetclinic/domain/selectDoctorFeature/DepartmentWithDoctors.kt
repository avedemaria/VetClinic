package com.example.vetclinic.domain.selectDoctorFeature

data class DepartmentWithDoctors (
    val department: String,
    val doctors: List<Doctor>
)