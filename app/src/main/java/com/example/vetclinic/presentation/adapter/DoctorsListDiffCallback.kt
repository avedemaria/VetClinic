package com.example.vetclinic.presentation.adapter

import androidx.recyclerview.widget.DiffUtil

class DoctorsListDiffCallback(
    private val oldList: List<DepAndDocItemList>,
    private val newList: List<DepAndDocItemList>
) : DiffUtil.Callback() {


    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return when {
            oldItem is DepAndDocItemList.DepartmentItem
                    && newItem is DepAndDocItemList.DepartmentItem
                -> oldItem.departmentName == newItem.departmentName

            oldItem is DepAndDocItemList.DoctorItem && newItem is DepAndDocItemList.DoctorItem ->
                oldItem.doctor.uid == newItem.doctor.uid

            else -> false
        }
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return if (oldItem != newItem) newItem else null
    }
}

