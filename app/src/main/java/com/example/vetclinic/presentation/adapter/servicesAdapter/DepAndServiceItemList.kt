package com.example.vetclinic.presentation.adapter.servicesAdapter

import com.example.vetclinic.domain.entities.Service

sealed class DepAndServiceItemList {

    data class DepartmentItem(val departmentName: String) : DepAndServiceItemList()
    data class ServiceItem(val service: Service) : DepAndServiceItemList()
}