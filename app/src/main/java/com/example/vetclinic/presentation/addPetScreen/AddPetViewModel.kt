package com.example.vetclinic.presentation.addPetScreen

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vetclinic.domain.interfaces.UserDataStore
import com.example.vetclinic.domain.entities.pet.Pet
import com.example.vetclinic.domain.usecases.PetUseCase
import jakarta.inject.Inject
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.UUID

class AddPetViewModel @Inject constructor(
    private val petUseCase: PetUseCase,
    private val userDataStore: UserDataStore,

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

    fun addPetData(petName: String, petType: String, petGender: String, petBDay: String) {

        val currentUserId = _userId.value
        val petId = UUID.randomUUID().toString()

        if (currentUserId == null) {
            _addPetState.value = AddPetUiState.Error("UserId отсутствует")
            return
        }

        val petAge = calculatePetAge(petBDay)


        val pet = Pet(
            petId = petId,
            userId = currentUserId,
            petName = petName,
            petBDay = petBDay,
            petType = petType,
            petGender = petGender,
            petAge = petAge
        )


        _addPetState.value = AddPetUiState.Loading

        viewModelScope.launch {
            val result = petUseCase.addPetToSupabaseDb(pet)

            if (result.isSuccess) {
                petUseCase.addPetToRoom(pet)
                _addPetState.value = AddPetUiState.Success
            } else {
                val errorMessage = result.exceptionOrNull()?.message ?: "Неизвестная ошибка"
                _addPetState.value = AddPetUiState.Error(errorMessage)
                Log.e("AddPetViewModel", "Error adding pet: $errorMessage")
            }

        }
    }


    private fun calculatePetAge(petBday: String): String {
        return try {
            val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
            val birthDate = LocalDate.parse(petBday, formatter)
            val currentDate = LocalDate.now()
            val period = Period.between(birthDate, currentDate)

            val year = period.years
            val month = period.months


            val yearText = if (year > 0) {
                "$year ${getYearSuffix(year)}"
            } else {
                ""
            }

            val monthText = if (month > 0 || year == 0) {
                "$month ${getMonthSuffix(month)}"
            } else {
                ""
            }

            listOf(yearText, monthText).filter {
                it.isNotEmpty()
            }.joinToString(" ")

        } catch (e: Exception) {
            Log.e("PetViewModel", "Error calculating pet age: ${e.message}")
            "0 мес."
        }
    }


    private fun getYearSuffix(years: Int): String {
        return when {
            years % 10 == 1 && years % 100 != 11 -> "год"
            years % 10 in 2..4 && (years % 100 !in 12..14) -> "года"
            else -> "лет"
        }
    }

    private fun getMonthSuffix(month: Int): String {
        return "мес."
    }

}