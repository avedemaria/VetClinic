package com.example.vetclinic.presentation.adapter.servicesAdapter

import com.example.vetclinic.domain.entities.Service

interface OnServiceClickListener {

    fun onServiceClick(service: Service)
}