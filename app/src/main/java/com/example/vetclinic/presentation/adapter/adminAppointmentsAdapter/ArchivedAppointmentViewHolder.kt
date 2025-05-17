package com.example.vetclinic.presentation.adapter.adminAppointmentsAdapter

import androidx.recyclerview.widget.RecyclerView
import com.example.vetclinic.databinding.ItemArchivedAppointmentAdminBinding
import com.example.vetclinic.domain.entities.appointment.AppointmentWithDetails

class ArchivedAppointmentViewHolder(
    private val binding: ItemArchivedAppointmentAdminBinding,
     listener: OnBellClickListener
) : RecyclerView.ViewHolder(binding.root) {

    init {
        val adapter = ArchivedAppointmentBindingAdapter(binding)
        AdminAppointmentBindingHelper.setupListeners(adapter, listener)
    }

    fun bind(appointment: AppointmentWithDetails) {
        val adapter = ArchivedAppointmentBindingAdapter(binding)
        AdminAppointmentBindingHelper.bind(adapter, appointment)
    }
}