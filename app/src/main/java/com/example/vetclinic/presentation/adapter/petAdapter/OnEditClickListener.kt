package com.example.vetclinic.presentation.adapter.petAdapter

import com.example.vetclinic.domain.entities.Pet

interface OnEditClickListener {

    fun onEditClick (pet: Pet, parameter: String)
}