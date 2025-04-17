package com.example.vetclinic.presentation.adapter.servicesAdapter

import com.example.vetclinic.domain.entities.service.Service

interface OnServiceClickListener {

    fun onServiceClick(service: Service)
}