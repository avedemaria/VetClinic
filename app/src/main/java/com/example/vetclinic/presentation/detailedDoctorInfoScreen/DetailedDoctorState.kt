package com.example.vetclinic.presentation.detailedDoctorInfoScreen

import com.example.vetclinic.domain.entities.service.Service

sealed class DetailedDoctorState{

    data object Loading : DetailedDoctorState()
    data class Success(val services: List<Service>) : DetailedDoctorState()
    data class Error(val message: String) : DetailedDoctorState()

}