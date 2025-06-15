package com.example.vetclinic.presentation.screens.mainScreen

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vetclinic.domain.entities.pet.Pet
import com.example.vetclinic.domain.entities.user.User
import com.example.vetclinic.domain.usecases.AppointmentReminderUseCase
import com.example.vetclinic.domain.usecases.AppointmentUseCase
import com.example.vetclinic.domain.usecases.PetUseCase
import com.example.vetclinic.domain.usecases.SessionUseCase
import com.example.vetclinic.domain.usecases.UserUseCase
import com.example.vetclinic.presentation.screens.UiEvent
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class MainViewModel @Inject constructor(
    private val userUseCase: UserUseCase,
    private val petUseCase: PetUseCase,
    private val appointmentUseCase: AppointmentUseCase,
    private val appointmentReminderUseCase: AppointmentReminderUseCase,
    private val sessionUseCase: SessionUseCase,

    ) : ViewModel() {


    private val _mainState = MutableLiveData<MainState>()
    val mainState: LiveData<MainState> get() = _mainState

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    private var storedUser: User? = null
    private var storedPets: List<Pet> = emptyList()


    fun getUserIdAndFetchData() {
        viewModelScope.launch {
            val userId =  sessionUseCase.getUserId() ?: return@launch
            Log.d(TAG, "userId1 $userId")
            getUserAndPet(userId)
            observeAppointmentsByUserId(userId)
        }
    }


    private fun observeAppointmentsByUserId(userId: String) {
        appointmentUseCase.observeAppointmentsInRoomByUserId(userId)
            .onEach { appointments ->
                val activeAppointments = appointments.filter { !it.isArchived }

                if (activeAppointments.isEmpty()) {
                    Log.d(TAG, "No active appointments, skipping WorkManager")
                    return@onEach
                }

                Log.d(TAG, "Sending reminders for ${activeAppointments.size} active appointments")
                appointmentReminderUseCase.invoke(activeAppointments)
            }
            .catch { e ->
                Log.e(TAG, "Error loading appointments: ${e.message}")
            }
            .launchIn(viewModelScope)
    }



    private suspend fun getUserAndPet(userId: String) {
        _mainState.value = MainState.Loading

        val user = userUseCase.getUserFromSupabaseDb(userId)
            .onFailure {
                _mainState.value = MainState.Error
                _uiEvent.emit(UiEvent.ShowSnackbar("Ошибка загрузки пользователя: ${it.message}"))
            }
            .getOrNull() ?: return

        storedUser = user

        val pets = petUseCase.getPetsFromSupabaseDb(userId)
            .onFailure {
                _mainState.value = MainState.Error
                _uiEvent.emit(UiEvent.ShowSnackbar("Ошибка загрузки питомцев: ${it.message}"))
            }
            .getOrNull()
            .orEmpty()

        storedPets = pets
        updateResultState()
    }


    private fun updateResultState() {
        _mainState.value = MainState.Result(storedUser, storedPets)
    }


    companion object {
        private const val TAG = "MainViewModel"
    }
}









