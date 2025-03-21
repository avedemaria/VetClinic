package com.example.vetclinic.presentation.adapter.petAdapter

import androidx.recyclerview.widget.DiffUtil
import com.example.vetclinic.domain.entities.Pet

class PetItemDiffCallback: DiffUtil.ItemCallback<Pet>() {

    override fun areItemsTheSame(oldItem: Pet, newItem: Pet): Boolean {
        return oldItem.petId == newItem.petId
    }

    override fun areContentsTheSame(oldItem: Pet, newItem: Pet): Boolean {
        return oldItem == newItem
    }
}