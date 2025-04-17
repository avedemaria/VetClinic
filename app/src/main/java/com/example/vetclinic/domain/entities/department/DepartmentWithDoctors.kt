package com.example.vetclinic.domain.entities.department

import android.os.Parcelable
import com.example.vetclinic.domain.entities.doctor.Doctor
import kotlinx.parcelize.Parcelize

@Parcelize
data class DepartmentWithDoctors(
    val department: Department,
    val doctors: List<Doctor>
) : Parcelable