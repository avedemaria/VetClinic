package com.example.vetclinic.presentation.screens.mainScreen.homeScreen

import android.content.Context
import android.os.PowerManager
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vetclinic.domain.repository.DialogDataStore
import com.example.vetclinic.domain.repository.UserDataStore
import com.example.vetclinic.domain.usecases.UserUseCase
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class HomeViewModel @Inject constructor(
    private val userUseCase: UserUseCase,
    private val userDataStore: UserDataStore,
    private val dialogDataStore: DialogDataStore,
) : ViewModel() {

    private val _homeState = MutableLiveData<HomeState>()
    val homeState: LiveData<HomeState> get() = _homeState

    private val _showDialogEvent = MutableSharedFlow<Unit>()
    val showDialogEvent = _showDialogEvent.asSharedFlow()

    private var storedUserName: String = ""

    fun getUserIdAndLoadUserName() {
        viewModelScope.launch {
            val userId = userDataStore.getUserId() ?: return@launch
            Log.d(TAG, "userId2 $userId")

            if (userId.isNotEmpty()) {
                loadUserName(userId)
            } else {
                _homeState.value = HomeState.Error("User not found")
            }
        }
    }


    private fun loadUserName(userId: String) {
        _homeState.value = HomeState.Loading
        viewModelScope.launch {
            userUseCase.getUserFromRoom(userId).onSuccess { user ->
                Log.d(TAG, "Loaded user: $user")
                storedUserName = user.userName
                updateResultState()
            }.onFailure { error ->
                Log.e(TAG, "Error loading user: $error")
                _homeState.value = HomeState.Error(error.message ?: "Неизвестная ошибка")
            }
        }
    }

    fun checkShouldShowDialog(context: Context) {
        viewModelScope.launch {
            val lastShown = dialogDataStore.getLastShownDialog()
            val currentTime = System.currentTimeMillis()
            val oneDayMillis = 24 * 60 * 60 * 1000
            val isDisabled = dialogDataStore.getDisableDialogForeverFlag()

            val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            val isBatteryOptimized =
                !powerManager.isIgnoringBatteryOptimizations(context.packageName)

            when {
                isDisabled -> return@launch

                !isBatteryOptimized -> return@launch

                lastShown == null || currentTime - lastShown >= oneDayMillis -> {
                    dialogDataStore.putLastShowDialog(currentTime)
                    _showDialogEvent.emit(Unit)
                }

            }
        }
    }




    fun disableDialogForever() {
        viewModelScope.launch {
            dialogDataStore.putDisableDialogForeverFlag()
        }
    }


    private fun updateResultState() {
        _homeState.value = HomeState.Result(storedUserName)
    }


    companion object {
        private const val TAG = "HomeViewModel"
    }

}






