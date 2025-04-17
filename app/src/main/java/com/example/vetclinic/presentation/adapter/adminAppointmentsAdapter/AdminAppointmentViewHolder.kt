package com.example.vetclinic.presentation.adapter.adminAppointmentsAdapter

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import com.example.vetclinic.databinding.ItemAppointmentAdminBinding
import com.example.vetclinic.domain.entities.appointment.AppointmentWithDetails

class AdminAppointmentViewHolder(
    private val binding: ItemAppointmentAdminBinding,
    private val listener: OnBellClickListener,
) : RecyclerView.ViewHolder(binding.root) {


    init {
        val adapter = ActiveAppointmentBindingAdapter(binding)
        AdminAppointmentBindingHelper.setupListeners(adapter, listener)

    }

    @SuppressLint("SetTextI18n")
    fun bind(appointment: AppointmentWithDetails) {

        val adapter = ActiveAppointmentBindingAdapter(binding)
        AdminAppointmentBindingHelper.bind(adapter, appointment)

        }
    }




