package com.example.vetclinic.presentation.adapter.timeSlotsAdapter

import androidx.recyclerview.widget.DiffUtil
import com.example.vetclinic.domain.entities.timeSlot.TimeSlot

class TimeSlotItemDiffCallback : DiffUtil.ItemCallback<TimeSlot>() {
    override fun areItemsTheSame(oldItem: TimeSlot, newItem: TimeSlot): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: TimeSlot, newItem: TimeSlot): Boolean =
        oldItem == newItem
}