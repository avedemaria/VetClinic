package com.example.vetclinic.presentation.adapter.doctorsAdapter

import androidx.recyclerview.widget.RecyclerView
import com.example.vetclinic.databinding.ItemDepartmentHeaderBinding

class DepartmentViewHolder(val binding: ItemDepartmentHeaderBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(item: DepAndDocItemList.DepartmentItem) {
        binding.departmentTextView.text = item.departmentName
    }
}