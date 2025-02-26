package com.example.vetclinic.domain.entities

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DepartmentWithDoctors(
    val department: Department,
    val doctors: List<Doctor>
) : Parcelable