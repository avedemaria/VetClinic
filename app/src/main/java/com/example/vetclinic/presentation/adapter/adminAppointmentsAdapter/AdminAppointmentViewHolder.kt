package com.example.vetclinic.presentation.adapter.adminAppointmentsAdapter

import android.annotation.SuppressLint
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.vetclinic.R
import com.example.vetclinic.databinding.ItemAppointmentAdminBinding
import com.example.vetclinic.domain.entities.AppointmentStatus
import com.example.vetclinic.domain.entities.AppointmentWithDetails
import com.example.vetclinic.domain.entities.Doctor
import com.example.vetclinic.extractTime
import kotlinx.serialization.StringFormat

class AdminAppointmentViewHolder(
    private val binding: ItemAppointmentAdminBinding,
    private val listener: OnBellClickListener,
) : RecyclerView.ViewHolder(binding.root) {




    init {
        binding.ivBell.setOnLongClickListener { bellView ->
//            val newState = binding.ivBell.isSelected == !binding.ivBell.isSelected
            val appointment = bellView.tag as? AppointmentWithDetails
            
            appointment?.let {listener.onBellClicked(it) }
            true
        }

    }


    @SuppressLint("SetTextI18n")
    fun bind(appointment: AppointmentWithDetails) {
        with(binding) {
            tvPetName.text = appointment.petName
            tvOwnerName.text = appointment.userName
            tvDoctorName.text = binding.root.context.getString(
                R.string.doctor_role_and_name,
                appointment.doctorRole,
                appointment.doctorName
            )
            tvServiceName.text = appointment.serviceName
            tvTime.text = appointment.dateTime.extractTime()
            tvPetAge.text = ", ${appointment.petAge}"



            when (appointment.status) {
                AppointmentStatus.SCHEDULED -> {
                    tvStatus.text = "Приём назначен"
                    tvStatus.background = ContextCompat.getDrawable(
                        binding.root.context,
                        R.drawable.status_background_scheduled
                    )
                }

                AppointmentStatus.CANCELLED -> {
                    tvStatus.text = "Приём отменён"
                    tvStatus.background = ContextCompat.getDrawable(
                        binding.root.context,
                        R.drawable.status_background_cancelled
                    )
                }

                AppointmentStatus.COMPLETED -> {
                    tvStatus.text = "Приём завершён"
                    tvStatus.background = ContextCompat.getDrawable(
                        binding.root.context,
                        R.drawable.status_background_completed
                    )
                }
            }

            ivBell.isSelected = appointment.isConfirmed

            ivBell.tag = appointment


            if (appointment.isArchived) {
                root.alpha = ARCHIVED_ALPHA
            } else {
               root.alpha = CURRENT_ALPHA
            }

        }
    }


    companion object {
        private const val ARCHIVED_ALPHA = 0.5f
        private const val CURRENT_ALPHA = 1f
    }
}


