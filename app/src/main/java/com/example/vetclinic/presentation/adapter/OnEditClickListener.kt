package com.example.vetclinic.presentation.adapter

import com.example.vetclinic.domain.entities.Pet

interface OnEditClickListener {

    fun onEditClick (pet: Pet, parameter: String)
}