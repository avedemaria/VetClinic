package com.example.vetclinic.presentation.adapter.adminAppointmentsAdapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.vetclinic.databinding.ItemAppointmentAdminBinding
import com.example.vetclinic.databinding.ItemArchivedAppointmentAdminBinding
import com.example.vetclinic.domain.entities.AppointmentWithDetails

class AdminAppointmentsAdapter(private val listener: OnBellClickListener) :
    PagingDataAdapter<AppointmentWithDetails, RecyclerView.ViewHolder>(
        AdminAppointmentItemDiffCallback()
    ) {

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position)?.isArchived == true) ARCHIVED_VIEW_TYPE else CURRENT_VIEW_TYPE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ARCHIVED_VIEW_TYPE -> {
                ArchivedAppointmentViewHolder(
                    ItemArchivedAppointmentAdminBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ), listener
                )

            }

            CURRENT_VIEW_TYPE -> {
                AdminAppointmentViewHolder(
                    ItemAppointmentAdminBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ), listener
                )
            }

            else -> throw IllegalArgumentException("invalid viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val appointment = getItem(position)
        Log.d(TAG, "Binding item at position $position: $appointment")
        if (appointment != null) {
            when (holder) {
                is AdminAppointmentViewHolder -> holder.bind(appointment)
                is ArchivedAppointmentViewHolder -> holder.bind(appointment)
            }

        } else {
            Log.d(TAG, "Appointment at position $position is null.")
        }
    }


    companion object {
        private const val TAG = "AdminAppointmentsAdapter"
        private const val CURRENT_VIEW_TYPE = 124
        private const val ARCHIVED_VIEW_TYPE = 125
    }

}


