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
                -> oldItem.department == newItem.department

            oldItem is DepAndDocItemList.DoctorItem && newItem is DepAndDocItemList.DoctorItem ->
                oldItem.doctor.uid == newItem.doctor.uid

            else -> false
        }
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {

        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]

        return when {
            oldItem is DepAndDocItemList.DepartmentItem && newItem
                    is DepAndDocItemList.DepartmentItem ->
                oldItem == newItem

            oldItem is DepAndDocItemList.DoctorItem && newItem is DepAndDocItemList.DoctorItem ->
                oldItem.doctor == newItem.doctor

            else -> false
        }
    }
}

