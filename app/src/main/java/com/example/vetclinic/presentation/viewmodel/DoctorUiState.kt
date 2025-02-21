package com.example.vetclinic.presentation.viewmodel

import com.example.vetclinic.domain.selectDoctorFeature.DepartmentWithDoctors

sealed class DoctorUiState {
    object Loading : DoctorUiState()
    data class Success(val doctors: List<DepartmentWithDoctors>) : DoctorUiState()
    data class Error(val message: String) : DoctorUiState()
    object Empty : DoctorUiState()
}