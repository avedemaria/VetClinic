package com.example.vetclinic.presentation.adapter.adminAppointmentsAdapter

import androidx.core.content.ContextCompat
import com.example.vetclinic.R
import com.example.vetclinic.domain.entities.appointment.AppointmentStatus
import com.example.vetclinic.domain.entities.appointment.AppointmentWithDetails
import com.example.vetclinic.utils.extractTime

object AdminAppointmentBindingHelper {


    fun setupListeners(
        binding: AppointmentViewBinding,
        listener: OnBellClickListener
    ) {
        binding.ivBell.setOnLongClickListener {
            val appointment = it.tag as? AppointmentWithDetails
            appointment?.let { listener.onBellClicked(it) }
            true
        }
    }



    fun bind(
        binding: AppointmentViewBinding,
        appointment: AppointmentWithDetails,
    ) {
        with(binding) {
            tvPetName.text = appointment.petName
            tvOwnerName.text = "Владелец: ${appointment.userName} ${appointment.userLastName}"
            tvDoctorName.text = root.context.getString(
                R.string.doctor_role_and_name,
                appointment.doctorRole,
                appointment.doctorName
            )
            tvServiceName.text = appointment.serviceName
            tvTime.text = appointment.dateTime.extractTime()
            tvPetAge.text = ", ${appointment.petBday}"

            tvStatus.text = when (appointment.status) {
                AppointmentStatus.SCHEDULED -> "Приём назначен"
                AppointmentStatus.CANCELLED -> "Приём отменён"
                AppointmentStatus.COMPLETED -> "Приём завершён"
            }

            tvStatus.background = ContextCompat.getDrawable(
                root.context,
                when (appointment.status) {
                    AppointmentStatus.SCHEDULED -> R.drawable.status_background_scheduled
                    AppointmentStatus.CANCELLED -> R.drawable.status_background_cancelled
                    AppointmentStatus.COMPLETED -> R.drawable.status_background_completed
                }
            )

            ivBell.isSelected = appointment.isConfirmed

            ivBell.tag = appointment
        }
    }
}