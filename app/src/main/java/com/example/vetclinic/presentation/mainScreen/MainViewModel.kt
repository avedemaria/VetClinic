package com.example.vetclinic.presentation.mainScreen

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vetclinic.domain.interfaces.UserDataStore
import com.example.vetclinic.domain.entities.pet.Pet
import com.example.vetclinic.domain.entities.user.User
import com.example.vetclinic.domain.usecases.AppointmentReminderUseCase
import com.example.vetclinic.domain.usecases.AppointmentUseCase
import com.example.vetclinic.domain.usecases.PetUseCase
import com.example.vetclinic.domain.usecases.UserUseCase
import jakarta.inject.Inject
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class MainViewModel @Inject constructor(
    private val userUseCase: UserUseCase,
    private val petUseCase: PetUseCase,
    private val appointmentUseCase: AppointmentUseCase,
    private val appointmentReminderUseCase: AppointmentReminderUseCase,
    private val userDataStore: UserDataStore,

    ) : ViewModel() {


    private val _mainState = MutableLiveData<MainState>()
    val mainState: LiveData<MainState> get() = _mainState


    private var storedUser: User? = null
    private var storedPets: List<Pet> = emptyList()


    fun getUserIdAndFetchData() {
        viewModelScope.launch {
            val userId = userDataStore.getUserId() ?: return@launch
            Log.d("MainViewModel", "userId1 $userId")
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

        val userResult = userUseCase.getUserFromSupabaseDb(userId)
        if (userResult.isSuccess) {
            storedUser = userResult.getOrNull()
            fetchAndProcessPetData(userId, storedUser)
        } else {
            _mainState.value = MainState.Error(
                "Ошибка загрузки пользователя: " +
                        "${userResult.exceptionOrNull()?.message}"
            )
        }
    }


    private suspend fun fetchAndProcessPetData(userId: String, user: User?) {

        if (user == null) {
            _mainState.value = MainState.Error("User is null")
            return
        }

        val petResult = petUseCase.getPetsFromSupabaseDb(userId)

        when {
            petResult.isSuccess -> {
                val pets = petResult.getOrNull()
                if (!pets.isNullOrEmpty()) {
                    Log.d("MainViewModel", "pets: $pets")
                    storedPets = pets
                    updateResultState()
                } else {
                    _mainState.value = MainState.Error("No pets found")
                }
            }

            petResult.isFailure -> {
                _mainState.value = MainState.Error(
                    "Ошибка загрузки питомца: " +
                            "${petResult.exceptionOrNull()?.message}"
                )
            }
        }
    }


    private fun updateResultState() {
        _mainState.value = MainState.Result(storedUser, storedPets)
    }


    companion object {
        private const val TAG = "MainViewModel"
    }
}









