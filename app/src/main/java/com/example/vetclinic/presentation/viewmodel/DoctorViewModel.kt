package com.example.vetclinic.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vetclinic.domain.selectDoctorFeature.DepartmentWithDoctors
import com.example.vetclinic.domain.selectDoctorFeature.Doctor
import com.example.vetclinic.domain.selectDoctorFeature.GetDoctorListUseCase
import jakarta.inject.Inject
import kotlinx.coroutines.launch

class DoctorViewModel @Inject constructor(
    private val getDoctorListUseCase: GetDoctorListUseCase
) : ViewModel() {

    private val _doctorState = MutableLiveData<DoctorUiState>(DoctorUiState.Empty)
    val doctorState: LiveData<DoctorUiState> = _doctorState


    init {
        fetchDoctors()
    }

    fun fetchDoctors() {
        _doctorState.value = DoctorUiState.Loading
        viewModelScope.launch {
            runCatching {
                getDoctorListUseCase.getDoctorList()
            }
                .onSuccess { doctors ->
                    _doctorState.value = if (doctors.isNotEmpty()) {
                        val groupedDoctors = groupDoctorsByDepartment(doctors)
                        DoctorUiState.Success(groupedDoctors)
                    } else {
                        DoctorUiState.Empty
                    }
                }
                .onFailure { error ->
                    _doctorState.value = DoctorUiState.Error(error.message ?: "Ошибка загрузки")
                    Log.e("DoctorViewModel", "Error while fetching doctors", error)
                }
        }
    }

    private fun groupDoctorsByDepartment(doctors: List<Doctor>): List<DepartmentWithDoctors> {
        return doctors
            .groupBy { it.department }
            .map { (department, doctorsList) ->
                DepartmentWithDoctors(
                    department = department,
                    doctors = doctorsList.sortedBy { it.doctorLastName }
                )
            }
            .sortedBy { it.department }
    }


}