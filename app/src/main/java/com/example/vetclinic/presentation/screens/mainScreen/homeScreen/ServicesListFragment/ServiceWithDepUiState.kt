package com.example.vetclinic.presentation.screens.mainScreen.homeScreen.ServicesListFragment

import com.example.vetclinic.domain.entities.department.DepartmentWithServices

sealed class ServiceWithDepUiState {
    data object Loading : ServiceWithDepUiState()
    data class Success(val services: List<DepartmentWithServices>) : ServiceWithDepUiState()
    data class Error(val message: String) : ServiceWithDepUiState()
    data object Empty : ServiceWithDepUiState()


}