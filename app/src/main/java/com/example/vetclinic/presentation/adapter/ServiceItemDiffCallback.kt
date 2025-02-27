package com.example.vetclinic.presentation.adapter

import androidx.recyclerview.widget.DiffUtil

class ServiceItemDiffCallback : DiffUtil.ItemCallback<DepAndServiceItemList>() {

    override fun areItemsTheSame(
        oldItem: DepAndServiceItemList,
        newItem: DepAndServiceItemList
    ): Boolean {

        return when {
            oldItem is DepAndServiceItemList.DepartmentItem
                    && newItem is DepAndServiceItemList.DepartmentItem
                -> oldItem.departmentName == newItem.departmentName

            oldItem is DepAndServiceItemList.ServiceItem && newItem
                    is DepAndServiceItemList.ServiceItem ->
                oldItem.service.id == newItem.service.id

            else -> false
        }
    }


    override fun areContentsTheSame(
        oldItem: DepAndServiceItemList,
        newItem: DepAndServiceItemList
    ): Boolean {
        return oldItem == newItem
    }
}