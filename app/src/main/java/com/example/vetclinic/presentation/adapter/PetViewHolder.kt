package com.example.vetclinic.presentation.adapter

import androidx.recyclerview.widget.RecyclerView
import com.example.vetclinic.databinding.PetItemBinding
import com.example.vetclinic.domain.entities.Pet
import com.example.vetclinic.presentation.PetParameter

class PetViewHolder(
    private val binding: PetItemBinding,
    private val listener: OnEditClickListener
) : RecyclerView.ViewHolder(binding.root) {


    init {

        binding.btnEditPetName.setOnClickListener {
            val pet = it.tag as? Pet
            pet?.let { listener.onEditClick(it, PetParameter.NAME.name) }
        }

        binding.btnEditPetBday.setOnClickListener {
            val pet = it.tag as? Pet
            pet?.let { listener.onEditClick(it, PetParameter.BDAY.name) }
        }

        binding.btnEditPetType.setOnClickListener {
            val pet = it.tag as? Pet
            pet?.let { listener.onEditClick(it, PetParameter.TYPE.name) }
        }
    }

    fun bind(pet: Pet) {

        with(binding) {
            tvPetName.text = pet.petName
            tvPetBDay.text = pet.petBDay
            tvPetType.text = pet.petType

            btnEditPetName.tag = pet
            btnEditPetBday.tag = pet
            btnEditPetType.tag = pet

        }

    }




}