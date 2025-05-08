package com.example.vetclinic.presentation.adapter.appointmentsAdapter

import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.vetclinic.R
import com.example.vetclinic.databinding.AppointmentItemBinding
import com.example.vetclinic.domain.entities.appointment.AppointmentStatus
import com.example.vetclinic.domain.entities.appointment.AppointmentWithDetails
import com.example.vetclinic.presentation.utils.formatAppointmentDateTime

class AppointmentViewHolder(
    private val binding: AppointmentItemBinding,
) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(appointment: AppointmentWithDetails) {

        with(binding) {
            tvDoctorName.text = appointment.doctorName
            tvDoctorRole.text = appointment.doctorRole
            tvServiceName.text = appointment.serviceName
            tvPetOwner.text = appointment.userName
            tvPet.text = appointment.petName
            tvAppointmentDateTime.text = appointment.dateTime.formatAppointmentDateTime()



            when (appointment.status) {
                AppointmentStatus.SCHEDULED -> {
                    tvAppointmentStatus.text = "Приём назначен"
                    tvAppointmentStatus.background = ContextCompat.getDrawable(
                        binding.root.context,
                        R.drawable.status_background_scheduled
                    )
                }

                AppointmentStatus.CANCELLED -> {
                    tvAppointmentStatus.text = "Приём отменён"
                    tvAppointmentStatus.background = ContextCompat.getDrawable(
                        binding.root.context,
                        R.drawable.status_background_cancelled
                    )
                }

                AppointmentStatus.COMPLETED -> {
                    tvAppointmentStatus.text = "Приём завершён"
                    tvAppointmentStatus.background = ContextCompat.getDrawable(
                        binding.root.context,
                        R.drawable.status_background_completed
                    )
                }
            }
        }
    }

}