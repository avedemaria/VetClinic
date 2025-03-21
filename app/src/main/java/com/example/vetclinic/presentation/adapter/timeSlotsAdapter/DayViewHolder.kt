package com.example.vetclinic.presentation.adapter.timeSlotsAdapter

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.vetclinic.databinding.ItemDayBinding
import com.example.vetclinic.databinding.ItemDayClickedBinding
import com.example.vetclinic.domain.entities.Day

class DayViewHolder(
    private val binding: ViewBinding,
    private val onDayClickedListener: OnDayClickedListener
) : RecyclerView.ViewHolder(binding.root) {




    fun onBind(day: Day) {

        when (binding) {
            is ItemDayBinding -> {
                binding.tvDay.text = day.date
            }

            is ItemDayClickedBinding -> {
                binding.tvDayClicked.text = day.date
            }
        }

        binding.root.setOnClickListener{
            if (!day.isSelected) {
                onDayClickedListener.onDayClicked(day)
            }
        }

    }

}