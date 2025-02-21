package com.example.vetclinic.presentation.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.vetclinic.CodeReview

class DoctorsListDiffCallback(
    private val oldList: List<DepAndDocItemList>,
    private val newList: List<DepAndDocItemList>
) : DiffUtil.Callback() {


    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    @CodeReview("Можно убрать дублирование val newItem = newList[newItemPosition]")
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return when (val oldItem = oldList[oldItemPosition]) {
            is DepAndDocItemList.DepartmentItem ->
                newList[newItemPosition] is DepAndDocItemList.DepartmentItem
                        && oldItem.department == (newList[newItemPosition] as DepAndDocItemList.DepartmentItem).department

            is DepAndDocItemList.DoctorItem ->
                newList[newItemPosition] is DepAndDocItemList.DoctorItem
                        && oldItem.doctor.uid == (newList[newItemPosition] as DepAndDocItemList.DoctorItem).doctor.uid

            else -> false
        }
    }


    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        @CodeReview("== уже сравнивает содержимое")
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

    @CodeReview("Позволяет обновлять только измененные поля, а не перерисовывать всё")
    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]

        return if (oldItem != newItem) newItem else null
    }
}

