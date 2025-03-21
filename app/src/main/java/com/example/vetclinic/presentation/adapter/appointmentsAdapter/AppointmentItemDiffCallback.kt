package com.example.vetclinic.presentation.adapter.appointmentsAdapter

import androidx.recyclerview.widget.DiffUtil
import com.example.vetclinic.domain.entities.Appointment

class AppointmentItemDiffCallback : DiffUtil.ItemCallback<Appointment>() {
    override fun areItemsTheSame(oldItem: Appointment, newItem: Appointment): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Appointment, newItem: Appointment): Boolean =
        oldItem == newItem
}