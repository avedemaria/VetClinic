package com.example.vetclinic.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.vetclinic.domain.interfaces.UserDataStore
import com.example.vetclinic.domain.authFeature.LogInUserUseCase
import com.example.vetclinic.domain.usecases.GetAppointmentUseCase
import com.example.vetclinic.domain.usecases.UpdateAppointmentUseCase
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date


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
//        getAppointmentsByDate(date)
    }


    private fun getAppointmentsByDate(date: LocalDate) {
        _adminState.value = AdminHomeState.Loading
        selectedDate = date

        val formattedDate = date.format(DateTimeFormatter.ISO_DATE)
        val nowIso = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        viewModelScope.launch {
            try {
                getAppointmentUseCase.getAppointmentsByDate(formattedDate, nowIso)
                    .cachedIn(viewModelScope)
                    .collect { pagingData ->
                        Log.d("getAppointmentsByDate", "Paging collected again!")
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