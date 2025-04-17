package com.example.vetclinic.domain.entities.department

import android.os.Parcelable
import com.example.vetclinic.domain.entities.service.Service
import kotlinx.parcelize.Parcelize

@Parcelize
data class DepartmentWithServices(
    val department: Department,
    val services: List<Service>
) : Parcelable