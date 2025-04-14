package com.example.vetclinic.presentation.adapter.adminAppointmentsAdapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import com.example.vetclinic.databinding.ItemAppointmentAdminBinding
import com.example.vetclinic.domain.entities.AppointmentWithDetails

class AdminAppointmentsAdapter(private val listener: OnBellClickListener) :
    PagingDataAdapter<AppointmentWithDetails, AdminAppointmentViewHolder>(
        AdminAppointmentItemDiffCallback()
    ) {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminAppointmentViewHolder {
        Log.d(TAG, "Creating viewholder")
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
        Log.d(TAG, "Binding item at position $position: $appointment")
        if (appointment != null) {
            holder.bind(appointment)
        } else {
            Log.d(TAG, "Appointment at position $position is null.")
        }
    }


  companion object {
      private const val TAG = "AdminAppointmentsAdapter"
  }
}
