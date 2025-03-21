package com.example.vetclinic.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vetclinic.domain.usecases.GetServiceUseCase
import jakarta.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlainServiceViewModel @Inject constructor(
    private val getServiceUseCase: GetServiceUseCase
) : ViewModel() {

    private val _serviceState = MutableLiveData<ServiceUiState>()
    val serviceState: LiveData<ServiceUiState> get() = _serviceState

    init {
        fetchServices()
    }


    fun fetchServices() {


        viewModelScope.launch {
            _serviceState.value = ServiceUiState.Loading
            delay(1000)
            val servicesResult = getServiceUseCase.getServiceList()

            if (servicesResult.isSuccess) {
                val services = servicesResult.getOrThrow()

                if (services.isEmpty()) {
                    _serviceState.value = ServiceUiState.Empty
                    return@launch
                }
                _serviceState.value = ServiceUiState.Success(services)
            } else {
                val error = servicesResult.exceptionOrNull()?.message ?: "Неизвестная ошибка"
                Log.e(
                    "PlainServiceViewModel", "Error while fetching" +
                            " services: $error"
                )
                _serviceState.value = ServiceUiState.Error(error)
            }

        }
    }
}