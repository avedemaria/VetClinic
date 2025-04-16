package com.example.vetclinic.presentation.adapter.adminAppointmentsAdapter

import androidx.recyclerview.widget.DiffUtil
import com.example.vetclinic.domain.entities.AppointmentWithDetails

class AdminAppointmentItemDiffCallback : DiffUtil.ItemCallback<AppointmentWithDetails>() {
    override fun areItemsTheSame(
        oldItem: AppointmentWithDetails,
        newItem: AppointmentWithDetails
    ): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(
        oldItem: AppointmentWithDetails,
        newItem: AppointmentWithDetails
    ): Boolean =
        oldItem == newItem


}
