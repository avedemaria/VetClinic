package com.example.vetclinic.presentation.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.vetclinic.databinding.ItemDepartmentHeaderBinding
import com.example.vetclinic.databinding.ItemServiceBinding

sealed class ServicesWithDepViewHolder(view: View) : RecyclerView.ViewHolder(view) {


    class DepartmentWithDepViewHolder(private val binding: ItemDepartmentHeaderBinding) :
        ServicesWithDepViewHolder(binding.root) {
        fun bind(departmentItem: DepAndServiceItemList.DepartmentItem) {
            binding.departmentTextView.text = departmentItem.departmentName
        }
    }

    class ServiceWithDepViewHolder(
        private val binding: ItemServiceBinding,
        private val listener: OnServiceClickListener
    ) :
        ServicesWithDepViewHolder(binding.root) {


        init {
            binding.root.setOnClickListener {
                val serviceItem = it.tag as? DepAndServiceItemList.ServiceItem
                serviceItem?.let { item ->
                    listener.onServiceClick(item.service)
                }
            }
        }

        fun bind(serviceItem: DepAndServiceItemList.ServiceItem) {
            binding.tvServicePrice.text = serviceItem.service.price + " руб"
            binding.tvServiceName.text = serviceItem.service.serviceName
            binding.tvServiceDuration.text = serviceItem.service.duration.toString() + " мин"
            binding.root.tag = serviceItem
        }
    }
}