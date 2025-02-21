package com.example.vetclinic.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.vetclinic.databinding.ItemDepartmentHeaderBinding
import com.example.vetclinic.databinding.ItemDoctorBinding
import com.example.vetclinic.domain.selectDoctorFeature.Doctor


class DoctorsAdapter(
    private val listener: OnAppointmentClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var items = listOf<DepAndDocItemList>()
        set(value) {
            val callback = DoctorsListDiffCallback(items, value)
            val diffResult = DiffUtil.calculateDiff(callback)
            diffResult.dispatchUpdatesTo(this)
            field = value
        }


    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is DepAndDocItemList.DepartmentItem -> TYPE_HEADER
            is DepAndDocItemList.DoctorItem -> TYPE_DOCTOR
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_HEADER -> {
                val binding = ItemDepartmentHeaderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                DepartmentViewHolder(binding)
            }

            TYPE_DOCTOR -> {
                val binding = ItemDoctorBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                DoctorViewHolder(binding, listener)
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is DepAndDocItemList.DepartmentItem -> (holder as DepartmentViewHolder).bind(item)
            is DepAndDocItemList.DoctorItem -> (holder as DoctorViewHolder).bind(item.doctor)
        }
    }


    override fun getItemCount(): Int = items.size




    companion object {
        private const val TYPE_HEADER = 101
        private const val TYPE_DOCTOR = 102
    }


}

