package com.example.vetclinic.presentation.screens.mainScreen.homeScreen.doctorsListFragment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vetclinic.domain.entities.department.Department
import com.example.vetclinic.domain.entities.department.DepartmentWithDoctors
import com.example.vetclinic.domain.entities.doctor.Doctor
import com.example.vetclinic.domain.usecases.DepartmentUseCase
import com.example.vetclinic.domain.usecases.DoctorsUseCase
import jakarta.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class DoctorViewModel @Inject constructor(
    private val getDoctorListUseCase: DoctorsUseCase,
    private val departmentUseCase: DepartmentUseCase
) : ViewModel() {

    private val _doctorState = MutableLiveData<DoctorUiState>()
    val doctorState: LiveData<DoctorUiState> = _doctorState


    init {
        fetchDoctors()
    }

   private fun fetchDoctors() {
        _doctorState.value = DoctorUiState.Loading
        viewModelScope.launch {

            val doctorsResult = async { getDoctorListUseCase.getDoctorList() }.await()
            val departmentsResult = async { departmentUseCase.getDepartmentList() }.await()

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
