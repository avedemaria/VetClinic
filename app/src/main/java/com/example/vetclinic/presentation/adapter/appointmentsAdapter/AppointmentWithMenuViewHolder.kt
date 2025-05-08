package com.example.vetclinic.presentation.adapter.appointmentsAdapter

import android.view.MenuItem
import android.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.vetclinic.R
import com.example.vetclinic.databinding.AppointmentItemWithMenuBinding
import com.example.vetclinic.domain.entities.appointment.AppointmentStatus
import com.example.vetclinic.domain.entities.appointment.AppointmentWithDetails
import com.example.vetclinic.presentation.utils.formatAppointmentDateTime

class AppointmentWithMenuViewHolder(
    private val binding: AppointmentItemWithMenuBinding,
    private val listener: OnAppointmentMenuClickListener
) :
    RecyclerView.ViewHolder(binding.root) {


    init {
        binding.btnMore.setOnClickListener {
            showPopupMenu(it)
        }
    }


    fun bind(appointment: AppointmentWithDetails) {


        with(binding) {
            tvDoctorName.text = appointment.doctorName
            tvDoctorRole.text = appointment.doctorRole
            tvServiceName.text = appointment.serviceName
            tvPetOwner.text = appointment.userName
            tvPet.text = appointment.petName
            tvAppointmentDateTime.text = appointment.dateTime.formatAppointmentDateTime()
            btnMore.tag = appointment


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

    private fun showPopupMenu(view: android.view.View) {
        val popupMenu = PopupMenu(view.context, view)

        popupMenu.menuInflater.inflate(R.menu.appointment_menu, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.item_cancel_appointment -> {
                    val appointment = view.tag as? AppointmentWithDetails
                    appointment?.let { listener.onAppointmentMenuClicked(it) }
                    true
                }

                else -> false
            }
        }
        popupMenu.show()
    }

}