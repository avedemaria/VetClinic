package com.example.vetclinic.presentation.adapter

import com.example.vetclinic.domain.entities.Doctor

sealed class DepAndDocItemList {
    data class DepartmentItem(val departmentName: String) : DepAndDocItemList()
    data class DoctorItem(val doctor: Doctor) : DepAndDocItemList()
}