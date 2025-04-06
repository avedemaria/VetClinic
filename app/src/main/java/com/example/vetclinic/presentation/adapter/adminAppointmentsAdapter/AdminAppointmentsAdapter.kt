package com.example.vetclinic.presentation.adapter.adminAppointmentsAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.vetclinic.databinding.ItemAppointmentAdminBinding
import com.example.vetclinic.domain.entities.AppointmentWithDetails

class AdminAppointmentsAdapter(private val listener: OnBellClickListener) :
    PagingDataAdapter<AppointmentWithDetails, AdminAppointmentViewHolder>(
        AdminAppointmentItemDiffCallback()
    ) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminAppointmentViewHolder {
        return AdminAppointmentViewHolder(
            ItemAppointmentAdminBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), listener
        )
    }


    override fun onBindViewHolder(holder: AdminAppointmentViewHolder, position: Int) {
        val appointment = getItem(position)
        if (appointment != null) {
            holder.bind(appointment)
        }
    }
}
