package com.example.vetclinic.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.cachedIn
import com.example.vetclinic.domain.UserDataStore
import com.example.vetclinic.domain.authFeature.LogInUserUseCase
import com.example.vetclinic.domain.entities.AppointmentWithDetails
import com.example.vetclinic.domain.usecases.GetAppointmentUseCase
import com.example.vetclinic.domain.usecases.UpdateAppointmentUseCase
import com.example.vetclinic.formatDateTime
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class AdminHomeViewModel @Inject constructor(
    private val getAppointmentUseCase: GetAppointmentUseCase,
    private val updateAppointmentUseCase: UpdateAppointmentUseCase,
    private val loginUseCase: LogInUserUseCase,
    private val userDataStore: UserDataStore,


    ) : ViewModel() {

    private val _adminState = MutableStateFlow<AdminHomeState>(AdminHomeState.Empty)
    val adminState: MutableStateFlow<AdminHomeState> get() = _adminState

    private var selectedDate: LocalDate? = null
    private var currentDate: LocalDate? = null


    init {
        val today = LocalDate.now()
        currentDate = today
        getAppointmentsByDate(today)
    }


    fun getCurrentDate(): LocalDate? {
        return currentDate
    }


    fun setUpSelectedDate(date: LocalDate) {
        selectedDate = date
        _adminState.value.let { currentState ->
            if (currentState is AdminHomeState.Success) {
                _adminState.value =
                    AdminHomeState.Success(currentState.appointments, selectedDate.toString())
            }
        }
    }

//    private fun getAppointmentsByDate(date: String) {
//        _adminState.value = AdminHomeState.Loading
//
//        viewModelScope.launch {
//            val result = getAppointmentUseCase.getAppointmentsByDate(date)
//            if (result.isSuccess) {
//                val appointments = result.getOrNull() ?: emptyList()
//                _adminState.value = AdminHomeState.Success(appointments, null)
//            }
//        }
//    }
//
//    fun getAppointmentsByDate(pageIndex: Int) {
//        _adminState.value = AdminHomeState.Loading
//
//        val selectedDate = "2025-04-09 10:00:00"
//        viewModelScope.launch {
//            try {
//                getAppointmentUseCase.getAppointmentsByDate(selectedDate)
//
//              _adminState.value = AdminHomeState.Success(pagingData, selectedDate)
//
//            } catch (e: Exception) {
//                _adminState.value = AdminHomeState.Error("Ошибка загрузки: ${e.message}")
//            }
//        }
//    }

    private fun getAppointmentsByDate(date: LocalDate) {
        _adminState.value = AdminHomeState.Loading
        selectedDate = date

        val formattedDate = date.format(DateTimeFormatter.ISO_DATE)
        viewModelScope.launch {
            try {
                getAppointmentUseCase.getAppointmentsByDate(formattedDate)
                    .cachedIn(viewModelScope)
                    .collect { pagingData ->
                        currentDate = date
                        _adminState.value = AdminHomeState.Success(pagingData, formattedDate)
                    }

            } catch (e: Exception) {
                _adminState.value = AdminHomeState.Error("Ошибка загрузки: ${e.message}")
            }
        }
    }


//    fun updateAppointmentStatus(updatedAppointment: AppointmentWithDetails) {
//        _adminState.value = AdminHomeState.Loading
//
//        viewModelScope.launch {
//            val date = LocalDate.now().toString()
//            val result = updateAppointmentUseCase.updateAppointmentStatus(updatedAppointment)
//            if (result.isSuccess) {
//                getAppointmentsByDate(date)
//            } else {
//                _adminState.value =
//                    AdminHomeState.Error(result.exceptionOrNull()?.message.toString())
//            }
//        }
//
//    }

    fun logOut() {
        _adminState.value = AdminHomeState.Loading

        viewModelScope.launch {
            val result = loginUseCase.logOut()
            if (result.isSuccess) {
                userDataStore.clearUserSession()
                _adminState.value = AdminHomeState.LoggedOut
            } else {
                val errorMessage = result.exceptionOrNull()?.message ?: "Неизвестная ошибка"
                _adminState.value = AdminHomeState.Error(errorMessage)
            }
        }
    }
}