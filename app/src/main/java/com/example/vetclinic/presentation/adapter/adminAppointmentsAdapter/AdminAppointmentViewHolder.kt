package com.example.vetclinic.presentation.adapter.adminAppointmentsAdapter

import android.annotation.SuppressLint
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.vetclinic.R
import com.example.vetclinic.databinding.ItemAppointmentAdminBinding
import com.example.vetclinic.domain.entities.AppointmentStatus
import com.example.vetclinic.domain.entities.AppointmentWithDetails
import com.example.vetclinic.extractTime

class AdminAppointmentViewHolder(
    private val binding: ItemAppointmentAdminBinding,
    private val listener: OnBellClickListener,
) : RecyclerView.ViewHolder(binding.root) {



    init {
        binding.ivBell.setOnLongClickListener { bellView ->
            val appointment = bellView.tag as? AppointmentWithDetails
            appointment?.let {listener.onBellClicked(it) }
            true
        }

    }


    @SuppressLint("SetTextI18n")
    fun bind(appointment: AppointmentWithDetails) {

        Log.d("BindDebug", "Item ${appointment.id}, archived: ${appointment.isArchived}")

        with(binding) {
            tvPetName.text = appointment.petName
            tvOwnerName.text = "Владелец: ${appointment.userName} ${appointment.userLastName}"
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


           root.alpha = if (appointment.isArchived) ARCHIVED_ALPHA else CURRENT_ALPHA
            Log.d("BindDebug", "Item ${appointment.id}: alpha = ${binding.root.alpha}")

            binding.ivBell.isSelected = appointment.isConfirmed
            ivBell.tag = appointment

        }
    }


    companion object {
        private const val ARCHIVED_ALPHA = 0.5f
        private const val CURRENT_ALPHA = 1f
    }
}


