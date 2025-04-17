package com.example.vetclinic.presentation.adapter.doctorsAdapter

import com.example.vetclinic.domain.entities.doctor.Doctor

sealed class DepAndDocItemList {
    data class DepartmentItem(val departmentName: String) : DepAndDocItemList()
    data class DoctorItem(val doctor: Doctor) : DepAndDocItemList()
}