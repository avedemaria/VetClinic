package com.example.vetclinic.presentation.screens.mainScreen.homeScreen.doctorsListFragment.detailedDoctorInfoFragment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vetclinic.domain.usecases.ServiceUseCase
import jakarta.inject.Inject
import kotlinx.coroutines.launch

class DetailedDoctorInfoViewModel @Inject constructor(
    private val serviceUseCase: ServiceUseCase
) : ViewModel() {

    private val _detailedDoctorState = MutableLiveData<DetailedDoctorState>()
    val detailedDoctorState: LiveData<DetailedDoctorState> get() = _detailedDoctorState



    fun getServicesByDepartmentId(departmentId: String) {
        Log.d(TAG, "Calling getServicesByDepartmentId with: $departmentId")
        _detailedDoctorState.value = DetailedDoctorState.Loading

        viewModelScope.launch {
            val servicesResult = serviceUseCase.getServicesByDepartmentId(departmentId)

            if (servicesResult.isSuccess) {
                val services = servicesResult.getOrThrow()
                _detailedDoctorState.value = DetailedDoctorState.Success(services)
            } else {
                val error = servicesResult.exceptionOrNull()?.message ?: "Неизвестная ошибка"
                Log.e(
                    TAG, "Error while fetching" +
                            " services: $error"
                )
                _detailedDoctorState.value = DetailedDoctorState.Error(error)
            }
        }
    }


    companion object {
        private const val TAG = "PlainServicesViewModel"
    }
}