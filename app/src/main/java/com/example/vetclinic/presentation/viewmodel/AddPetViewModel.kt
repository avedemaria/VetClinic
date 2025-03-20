package com.example.vetclinic.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vetclinic.domain.UserDataStore
import com.example.vetclinic.domain.entities.Pet
import com.example.vetclinic.domain.usecases.AddPetUseCase
import jakarta.inject.Inject
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.UUID

class AddPetViewModel @Inject constructor(
    private val addPetUseCase: AddPetUseCase,
    private val userDataStore: UserDataStore
) : ViewModel() {


    private val _userId = MutableLiveData<String?>()
    val userId: LiveData<String?> get() = _userId

    private val _addPetState = MutableLiveData<AddPetUiState>()
    val addPetState: LiveData<AddPetUiState> get() = _addPetState


    init {
        viewModelScope.launch {
            _userId.value = userDataStore.getUserId()
        }
    }

    fun addPetData(petName: String, petBday: String, petType: String, petGender: String) {

        val currentUserId = _userId.value
        val petId = UUID.randomUUID().toString()

        if (currentUserId == null) {
            _addPetState.value = AddPetUiState.Error("UserId отсутствует")
            return
        }


        val pet = Pet(
            petId = petId,
            userId = currentUserId,
            petName = petName,
            petBDay = petBday,
            petType = petType,
            petGender = petGender,
            petAge = null
        )


        _addPetState.value = AddPetUiState.Loading

        viewModelScope.launch {
            val result = addPetUseCase.addPetToSupabaseDb(pet)

            if (result.isSuccess) {
                addPetUseCase.addPetToRoom(pet)
                _addPetState.value = AddPetUiState.Success
            } else {
                val errorMessage = result.exceptionOrNull()?.message ?: "Неизвестная ошибка"
                _addPetState.value = AddPetUiState.Error(errorMessage)
                Log.e("AddPetViewModel", "Error adding pet: $errorMessage")
            }

        }
    }


//    fun calculatePetAge(petBday: String): Int {
//        try {
//            // Parse the birthday string to LocalDate
//            // Assuming the birthday format is "yyyy-MM-dd"
//            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
//            val birthDate = LocalDate.parse(petBday, formatter)
//            val currentDate = LocalDate.now()
//
//            // Calculate the period between the two dates
//            val period = Period.between(birthDate, currentDate)
//
//            // Return the age in years
//            return period.years
//        } catch (e: Exception) {
//            // Handle parsing errors
//            Log.e("PetViewModel", "Error calculating pet age: ${e.message}")
//            return 0
//        }
//    }

}