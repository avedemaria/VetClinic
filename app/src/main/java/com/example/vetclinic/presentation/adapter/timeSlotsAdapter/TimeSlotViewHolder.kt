package com.example.vetclinic.presentation.adapter.timeSlotsAdapter

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.vetclinic.databinding.ItemTimeBinding
import com.example.vetclinic.databinding.ItemTimeClickedBinding
import com.example.vetclinic.domain.entities.timeSlot.TimeSlot

class TimeSlotViewHolder(
    private val binding: ViewBinding,
    private val onTimeSlotClickedListener: OnTimeSlotClickedListener
) : RecyclerView.ViewHolder(binding.root) {


    fun onBind(timeSlot: TimeSlot) {

        when (binding) {
            is ItemTimeBinding -> {
                binding.tvTime.text = timeSlot.startTime
            }

            is ItemTimeClickedBinding -> {
                binding.tvTimeClicked.text = timeSlot.startTime
            }
        }


        binding.root.setOnClickListener {
            if (!timeSlot.isSelected) {
                onTimeSlotClickedListener.onTimeSlotClicked(timeSlot)
            }
        }


    }

}