package com.example.vetclinic.presentation.adapter

import com.example.vetclinic.domain.entities.Service

interface OnServiceClickListener {

    fun onServiceClick(service: Service)
}