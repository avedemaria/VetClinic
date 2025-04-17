package com.example.vetclinic.presentation.adapter.petAdapter

import com.example.vetclinic.domain.entities.pet.Pet

interface OnEditClickListener {

    fun onEditClick (pet: Pet, parameter: String)
}