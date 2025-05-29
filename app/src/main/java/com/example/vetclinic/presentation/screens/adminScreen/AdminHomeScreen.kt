package com.example.vetclinic.presentation.screens.adminScreen

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.example.vetclinic.R
import kotlinx.coroutines.flow.flowOf
import java.time.LocalDate
import java.util.Calendar

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AdminHomeScreen(
    viewModel: AdminHomeViewModel,
    onLogout: () -> Unit,
) {
    val context = LocalContext.current
    val state by viewModel.adminState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val showDatePicker = remember { mutableStateOf(false) }

    val pullRefreshState = rememberPullRefreshState(
        refreshing = state is AdminHomeState.Loading,
        onRefresh = { viewModel.refreshAppointments() }
    )

    if (showDatePicker.value) {
        val currentDate = viewModel.getCurrentDate()
        if (currentDate != null) {
            val calendar = Calendar.getInstance().apply {
                set(currentDate.year, currentDate.monthValue - 1, currentDate.dayOfMonth)
            }

            DatePickerDialog(
                context,
                { _, year, month, day ->
                    viewModel.setUpSelectedDate(LocalDate.of(year, month + 1, day))
                    showDatePicker.value = false
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).apply {
                setOnDismissListener { showDatePicker.value = false }
            }.show()
        } else {
            LaunchedEffect(Unit) {
                snackbarHostState.showSnackbar("Приёмы не найдены")
                showDatePicker.value = false
            }
        }
    }

    Scaffold(
        topBar = {
            AdminHomeTopBar(
                onCalendarClick = { showDatePicker.value = true },
                onLogoutClick = { viewModel.logOut() }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .pullRefresh(pullRefreshState)
        ) {
            when (state) {
                is AdminHomeState.Empty -> {
                    Text(
                        text = stringResource(R.string.no_available_appointments),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is AdminHomeState.Error -> {
                    val errorMessage = (state as AdminHomeState.Error).message
                    LaunchedEffect(errorMessage) {
                        snackbarHostState.showSnackbar("Ошибка: $errorMessage")
                    }
                }

                is AdminHomeState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                is AdminHomeState.LoggedOut -> {
                    LaunchedEffect(Unit) {
                        onLogout()
                        viewModel.afterLogout()
                    }
                }

                is AdminHomeState.Success -> {
                    val appointments = (state as AdminHomeState.Success).appointments
                    AdminAppointmentList(
                        appointments = flowOf(appointments),
                        onToggle = { viewModel.toggleAppointmentStatus(it) }
                    )
                }
            }

            PullRefreshIndicator(
                refreshing = state is AdminHomeState.Loading,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}
