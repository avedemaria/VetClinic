package com.example.vetclinic.presentation.adapter.timeSlotsAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.vetclinic.databinding.ItemTimeBinding
import com.example.vetclinic.databinding.ItemTimeClickedBinding
import com.example.vetclinic.domain.entities.TimeSlot

class TimeSlotAdapter(private val onTimeSlotClickedListener: OnTimeSlotClickedListener) :
    ListAdapter<TimeSlot, TimeSlotViewHolder>(TimeSlotItemDiffCallback()) {


    override fun getItemViewType(position: Int): Int {
        val timeSlot = getItem(position)

        return if (timeSlot.isSelected) {
            TYPE_TIME_SLOT
        } else {
            TYPE_TIME_SLOT_CLICKED
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeSlotViewHolder {

        return when (viewType) {
            TYPE_TIME_SLOT -> TimeSlotViewHolder(
                ItemTimeBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ), onTimeSlotClickedListener
            )

            TYPE_TIME_SLOT_CLICKED -> TimeSlotViewHolder(
                ItemTimeClickedBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ), onTimeSlotClickedListener
            )

            else -> throw IllegalArgumentException("Invalid view type")


        }


    }

    override fun onBindViewHolder(holder: TimeSlotViewHolder, position: Int) {

        val timeSlot = getItem(position)
        holder.onBind(timeSlot)
    }


    companion object {
        private const val TYPE_TIME_SLOT = 137
        private const val TYPE_TIME_SLOT_CLICKED = 138
    }
}