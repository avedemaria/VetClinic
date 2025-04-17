package com.example.vetclinic.presentation.adapter.appointmentsAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.vetclinic.databinding.AppointmentItemBinding
import com.example.vetclinic.databinding.AppointmentItemWithMenuBinding
import com.example.vetclinic.domain.entities.appointment.AppointmentWithDetails

class AppointmentsAdapter(private val listener: OnAppointmentMenuClickListener) :
    ListAdapter<AppointmentWithDetails, RecyclerView.ViewHolder>(AppointmentItemDiffCallback()) {


    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        return if (item.isArchived) VIEW_TYPE_ARCHIVED else VIEW_TYPE_REGULAR
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_REGULAR -> AppointmentWithMenuViewHolder(
                AppointmentItemWithMenuBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ), listener
            )

            VIEW_TYPE_ARCHIVED -> AppointmentViewHolder(
                AppointmentItemBinding.inflate(LayoutInflater.from(parent.context),
                    parent, false)
            )

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val appointment = getItem(position)
        when (holder) {
            is AppointmentWithMenuViewHolder -> holder.bind(appointment)
            is AppointmentViewHolder -> holder.bind(appointment)
        }
    }


    companion object {
        private const val VIEW_TYPE_REGULAR = 141
        private const val VIEW_TYPE_ARCHIVED = 142
    }

}