package com.example.vetclinic.presentation.adapter.petAdapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.vetclinic.databinding.PetItemBinding
import com.example.vetclinic.domain.entities.Pet

class PetAdapter(private val listener: OnEditClickListener) :
    ListAdapter<Pet, PetViewHolder>(PetItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PetViewHolder {
        return PetViewHolder(
            PetItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ), listener
        )
    }

    override fun onBindViewHolder(holder: PetViewHolder, position: Int) {
        val pet = getItem(position)
        Log.d("PetAdapter", "onBindViewHolder - position: $position, pet: $pet")
        holder.bind(pet)

    }
}