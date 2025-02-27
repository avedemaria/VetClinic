package com.example.vetclinic.presentation.viewmodel

import com.example.vetclinic.domain.entities.DepartmentWithServices

sealed class ServiceWithDepUiState {
    object Loading : ServiceWithDepUiState()
    data class Success(val services: List<DepartmentWithServices>) : ServiceWithDepUiState()
    data class Error(val message: String) : ServiceWithDepUiState()
    object Empty : ServiceWithDepUiState()


}