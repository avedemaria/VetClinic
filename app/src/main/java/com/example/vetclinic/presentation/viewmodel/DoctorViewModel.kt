package com.example.vetclinic.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vetclinic.domain.entities.Department
import com.example.vetclinic.domain.entities.DepartmentWithDoctors
import com.example.vetclinic.domain.entities.Doctor
import com.example.vetclinic.domain.usecases.GetDepartmentListUseCase
import com.example.vetclinic.domain.usecases.GetDoctorListUseCase
import jakarta.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class DoctorViewModel @Inject constructor(
    private val getDoctorListUseCase: GetDoctorListUseCase,
    private val getDepartmentListUseCase: GetDepartmentListUseCase
) : ViewModel() {

    private val _doctorState = MutableLiveData<DoctorUiState>()
    val doctorState: LiveData<DoctorUiState> = _doctorState


    init {
        fetchDoctors()
    }

    fun fetchDoctors() {
        _doctorState.value = DoctorUiState.Loading
        viewModelScope.launch {

            val doctorsResult = async { getDoctorListUseCase.getDoctorList() }.await()
            val departmentsResult = async { getDepartmentListUseCase.getDepartmentList() }.await()

            if (doctorsResult.isSuccess && departmentsResult.isSuccess) {
                val doctors = doctorsResult.getOrThrow()
                val departments = departmentsResult.getOrThrow()

                val groupedDoctors = groupDoctorsByDepartment(doctors, departments)
                _doctorState.value = DoctorUiState.Success(groupedDoctors)

            } else {
                val errorMessage = doctorsResult.exceptionOrNull()?.message
                    ?: departmentsResult.exceptionOrNull()?.message
                    ?: "Неизвестная ошибка"

                _doctorState.value = DoctorUiState.Error("Ошибка загрузки: $errorMessage")
                Log.e("DoctorViewModel", "Error while fetching" +
                        " doctors and departments: $errorMessage")
            }
        }
    }

    private fun groupDoctorsByDepartment(
        doctors: List<Doctor>,
        departments: List<Department>
    ): List<DepartmentWithDoctors> {
        return departments.map { department ->
            val doctorsInDepartment = doctors.filter { it.departmentId == department.id }
            DepartmentWithDoctors(
                department = department,
                doctors = doctorsInDepartment.sortedBy { it.doctorName }
            )
        }.filter { it.doctors.isNotEmpty() }
    }

}
