package com.example.vetclinic.presentation.screens.adminScreen

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.vetclinic.R
import com.example.vetclinic.domain.entities.appointment.AppointmentStatus
import com.example.vetclinic.domain.entities.appointment.AppointmentWithDetails


@Composable
fun AdminAppointmentItem(
    appointment: AppointmentWithDetails,
    onToggle: (AppointmentWithDetails) -> Unit
) {

    val context = LocalContext.current

    val statusText = when (appointment.status) {
        AppointmentStatus.SCHEDULED -> stringResource(R.string.status_scheduled)
        AppointmentStatus.COMPLETED -> stringResource(R.string.status_completed)
        AppointmentStatus.CANCELLED -> stringResource(R.string.status_cancelled)
    }
    val backgroundColor = getStatusColor(context, appointment.status)
    val grey = Color(ContextCompat.getColor(context, R.color.grey))

    Card(
        modifier = Modifier
            .padding(12.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = appointment.dateTime,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(Modifier.weight(1f)) {
                    Row {
                        Text(
                            text = appointment.petName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(Modifier.width(4.dp))
                        appointment.petBday?.let {
                            Text(
                                text = it,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }

                    Text(
                        text = context.getString(R.string.owner_full_name),
                        fontSize = 14.sp,
                        color = grey
                    )

                    Text(
                        text = appointment.doctorName,
                        fontSize = 12.sp,
                        color = grey,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = appointment.serviceName,
                        fontSize = 12.sp,
                        color = grey
                    )
                }

                IconButton(onClick = { onToggle(appointment) }) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .background(
                        color = backgroundColor,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = statusText,
                    color = Color.White,
                    fontSize = 12.sp
                )
            }
        }
    }
}


@Composable
fun getStatusColor(context: Context, status: AppointmentStatus): Color {
    val colorRes = when (status) {
        AppointmentStatus.SCHEDULED -> R.color.status_scheduled
        AppointmentStatus.CANCELLED -> R.color.status_cancelled
        AppointmentStatus.COMPLETED -> R.color.dark_grey
    }
    return Color(ContextCompat.getColor(context, colorRes))
}