package com.example.vetclinic.presentation.viewmodel

import com.example.vetclinic.domain.entities.DepartmentWithServices
import com.example.vetclinic.domain.entities.Service

sealed class ServiceUiState {

    object Loading : ServiceUiState()
    data class Success(val services: List<Service>) : ServiceUiState()
    data class Error(val message: String) : ServiceUiState()
    object Empty : ServiceUiState()

}