package com.example.vetclinic.presentation.screens.adminScreen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.paging.PagingData
import com.example.vetclinic.domain.entities.appointment.AppointmentWithDetails
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.Flow


@Composable
fun AdminAppointmentList(
    appointments: Flow<PagingData<AppointmentWithDetails>>,
    onToggle: (AppointmentWithDetails) -> Unit,
) {
    val lazyPagingItems = appointments.collectAsLazyPagingItems()

    Scaffold { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            items(lazyPagingItems.itemCount) { index ->
                val appointment = lazyPagingItems[index]
                if (appointment != null) {
                    AdminAppointmentItem(
                        appointment = appointment,
                        onToggle = { onToggle(appointment) })
                }
            }
        }
    }
}