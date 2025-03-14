package com.example.vetclinic.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.vetclinic.databinding.AppointmentItemBinding
import com.example.vetclinic.domain.entities.Appointment

class AppointmentsAdapter :
    ListAdapter<Appointment, AppointmentViewHolder>(AppointmentItemDiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        return AppointmentViewHolder(
            AppointmentItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        TODO("Not yet implemented")
    }
}