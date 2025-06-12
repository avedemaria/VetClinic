package com.example.vetclinic.presentation.screens.mainScreen.homeScreen.servicesListFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vetclinic.domain.entities.department.Department
import com.example.vetclinic.domain.entities.department.DepartmentWithServices
import com.example.vetclinic.domain.entities.service.Service
import com.example.vetclinic.domain.usecases.DepartmentUseCase
import com.example.vetclinic.domain.usecases.ServiceUseCase
import jakarta.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import timber.log.Timber

class ServiceWithDepViewModel @Inject constructor(
    private val serviceUseCase: ServiceUseCase,
    private val departmentUseCase: DepartmentUseCase,
) : ViewModel() {


    private val _serviceState = MutableLiveData<ServiceWithDepUiState>()
    val serviceState: LiveData<ServiceWithDepUiState> get() = _serviceState


    init {
        fetchServicesWithDep()
    }


    private fun fetchServicesWithDep() {
        _serviceState.value = ServiceWithDepUiState.Loading

        viewModelScope.launch {
            val servicesResult = async { serviceUseCase.getServiceList() }.await()
            val departmentsResult = async { departmentUseCase.getDepartmentList() }.await()

            if (servicesResult.isSuccess && departmentsResult.isSuccess) {
                val services = servicesResult.getOrThrow()
                val departments = departmentsResult.getOrThrow()

                if (services.isEmpty() || departments.isEmpty()) {
                    _serviceState.value = ServiceWithDepUiState.Empty
                    return@launch
                }
                val groupedServices = groupServicesByDepartment(services, departments)
                _serviceState.value = ServiceWithDepUiState.Success(groupedServices)
            } else {
                val error = servicesResult.exceptionOrNull()?.message
                    ?: departmentsResult.exceptionOrNull()?.message ?: "Неизвестная ошибка"

                Timber.tag("ServiceWithDepViewModel").e("Error while fetching" +
                            " services and departments: $error"
                )

                _serviceState.value = ServiceWithDepUiState.Error(error)
            }

        }
    }


    private fun groupServicesByDepartment(services: List<Service>, departments: List<Department>)
            : List<DepartmentWithServices> {
        return departments.map { department ->
            val servicesInDepartment = services.filter { it.departmentId == department.id }
            DepartmentWithServices(
                department = department,
                services = servicesInDepartment.sortedBy { it.serviceName }
            )
        }.filter { it.services.isNotEmpty() }
    }

}