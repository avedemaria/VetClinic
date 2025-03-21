package com.example.vetclinic.presentation.adapter.timeSlotsAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.vetclinic.databinding.ItemDayBinding
import com.example.vetclinic.databinding.ItemDayClickedBinding
import com.example.vetclinic.domain.entities.Day

class DaysAdapter(private val onDayClickedListener: OnDayClickedListener) :
    ListAdapter<Day, DayViewHolder>(DayItemDiffCallback()) {


    override fun getItemViewType(position: Int): Int {
        val dayItem = getItem(position)

        return if (dayItem.isSelected) {
            TYPE_DAY
        } else {
            TYPE_DAY_CLICKED
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {

        return when (viewType) {
            TYPE_DAY -> {
                DayViewHolder(
                    ItemDayBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ), onDayClickedListener
                )
            }

            TYPE_DAY_CLICKED -> {
                DayViewHolder(
                    ItemDayClickedBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ), onDayClickedListener
                )
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }


    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val dayItem = getItem(position)
        holder.onBind(dayItem)
    }


    companion object {
        private const val TYPE_DAY = 128
        private const val TYPE_DAY_CLICKED = 129
    }
}