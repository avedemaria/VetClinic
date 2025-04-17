package com.example.vetclinic.presentation.doctorsScreen

import com.example.vetclinic.domain.entities.department.DepartmentWithDoctors

sealed class DoctorUiState {
    data object Loading : DoctorUiState()
    data class Success(val doctors: List<DepartmentWithDoctors>) : DoctorUiState()
    data class Error(val message: String) : DoctorUiState()
}