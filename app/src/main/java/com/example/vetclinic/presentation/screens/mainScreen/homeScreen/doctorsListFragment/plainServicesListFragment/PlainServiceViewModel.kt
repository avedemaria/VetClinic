package com.example.vetclinic.presentation.screens.mainScreen.homeScreen.doctorsListFragment.plainServicesListFragment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vetclinic.domain.usecases.ServiceUseCase
import jakarta.inject.Inject
import kotlinx.coroutines.launch

class PlainServiceViewModel @Inject constructor(
    private val serviceUseCase: ServiceUseCase
) : ViewModel() {

    private val _serviceState = MutableLiveData<ServiceUiState>()
    val serviceState: LiveData<ServiceUiState> get() = _serviceState



    fun getServicesByDepartmentId(departmentId: String) {
        Log.d(TAG, "Calling getServicesByDepartmentId with: $departmentId")
        _serviceState.value = ServiceUiState.Loading

        viewModelScope.launch {
            val servicesResult = serviceUseCase.getServicesByDepartmentId(departmentId)

            if (servicesResult.isSuccess) {
                val services = servicesResult.getOrThrow()
                _serviceState.value = ServiceUiState.Success(services)
            } else {
                val error = servicesResult.exceptionOrNull()?.message ?: "Неизвестная ошибка"
                Log.e(
                    TAG, "Error while fetching" +
                            " services: $error"
                )
                _serviceState.value = ServiceUiState.Error(error)
            }
        }
    }


    companion object {
        private const val TAG = "PlainServicesViewModel"
    }
}