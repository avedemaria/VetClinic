package com.example.vetclinic.presentation.mainScreen.homeScreen.doctorsListFragment.plainServicesListFragment

import com.example.vetclinic.domain.entities.service.Service

sealed class ServiceUiState {

    data object Loading : ServiceUiState()
    data class Success(val services: List<Service>) : ServiceUiState()
    data class Error(val message: String) : ServiceUiState()

}