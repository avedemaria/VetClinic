package com.example.vetclinic.presentation.adapter.timeSlotsAdapter

import androidx.recyclerview.widget.DiffUtil
import com.example.vetclinic.domain.entities.timeSlot.Day

class DayItemDiffCallback : DiffUtil.ItemCallback<Day>() {


    override fun areItemsTheSame(oldItem: Day, newItem: Day): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Day, newItem: Day): Boolean =
        oldItem == newItem
}