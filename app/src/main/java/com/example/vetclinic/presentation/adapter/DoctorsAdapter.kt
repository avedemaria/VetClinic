package com.example.vetclinic.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.vetclinic.CodeReview
import com.example.vetclinic.databinding.ItemDepartmentHeaderBinding
import com.example.vetclinic.databinding.ItemDoctorBinding
import com.example.vetclinic.domain.selectDoctorFeature.Doctor


class DoctorsAdapter(
    private val listener: OnAppointmentClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var items = listOf<DepAndDocItemList>()
        set(value) {
            val callback = DoctorsListDiffCallback(field, value)
            val diffResult = DiffUtil.calculateDiff(callback)
            field = value
            @CodeReview("Сначала обновляем значение. Иначе могут быть старые данные при быстром обновлении")
            diffResult.dispatchUpdatesTo(this)
        }

    override fun getItemViewType(position: Int): Int = when (items[position]) {
        is DepAndDocItemList.DepartmentItem -> TYPE_HEADER
        is DepAndDocItemList.DoctorItem -> TYPE_DOCTOR
        else -> throw IllegalArgumentException("Invalid item type")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            TYPE_HEADER -> DepartmentViewHolder(
                ItemDepartmentHeaderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            TYPE_DOCTOR -> DoctorViewHolder(
                ItemDoctorBinding.inflate(LayoutInflater.from(parent.context), parent, false),
                listener
            )

            else -> throw IllegalArgumentException("Invalid view type")
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        when {
            holder is DepartmentViewHolder && item is DepAndDocItemList.DepartmentItem -> holder.bind(
                item
            )

            holder is DoctorViewHolder && item is DepAndDocItemList.DoctorItem -> holder.bind(item.doctor)
        }
    }

    override fun getItemCount(): Int = items.size


    companion object {
        private const val TYPE_HEADER = 101
        private const val TYPE_DOCTOR = 102
    }
}
