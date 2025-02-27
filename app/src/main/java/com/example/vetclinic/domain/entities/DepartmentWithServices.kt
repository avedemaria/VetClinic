package com.example.vetclinic.domain.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DepartmentWithServices(
    val department: Department,
    val services: List<Service>
) : Parcelable