package com.example.vetclinic.domain.selectDoctorFeature

import com.example.vetclinic.domain.entities.Doctor

data class DepartmentWithDoctors (
    val department: String,
    val doctors: List<Doctor>
)