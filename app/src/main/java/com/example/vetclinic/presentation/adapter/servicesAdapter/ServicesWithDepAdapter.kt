package com.example.vetclinic.presentation.adapter.servicesAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.vetclinic.databinding.ItemDepartmentHeaderBinding
import com.example.vetclinic.databinding.ItemServiceBinding

class ServicesWithDepAdapter(
    private val listener: OnServiceClickListener
) :
    ListAdapter<DepAndServiceItemList, ServicesWithDepViewHolder>(ServiceItemDiffCallback()) {


    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is DepAndServiceItemList.DepartmentItem -> TYPE_HEADER
            is DepAndServiceItemList.ServiceItem -> TYPE_SERVICE
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServicesWithDepViewHolder {
        return when (viewType) {
            TYPE_HEADER -> ServicesWithDepViewHolder.DepartmentWithDepViewHolder(
                ItemDepartmentHeaderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            TYPE_SERVICE -> ServicesWithDepViewHolder.ServiceWithDepViewHolder(
                ItemServiceBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ), listener
            )

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: ServicesWithDepViewHolder, position: Int) {

        val item = getItem(position)

        when {
            holder is ServicesWithDepViewHolder.DepartmentWithDepViewHolder
                    && item is DepAndServiceItemList.DepartmentItem -> holder.bind(item)


            holder is ServicesWithDepViewHolder.ServiceWithDepViewHolder
                    && item is DepAndServiceItemList.ServiceItem
                -> holder.bind(item)
        }
    }


    companion object {
        private const val TYPE_HEADER = 104
        private const val TYPE_SERVICE = 105
    }
}