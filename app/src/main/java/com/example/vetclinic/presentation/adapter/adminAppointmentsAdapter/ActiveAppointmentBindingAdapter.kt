package com.example.vetclinic.presentation.adapter.adminAppointmentsAdapter

import com.example.vetclinic.databinding.ItemAppointmentAdminBinding

class ActiveAppointmentBindingAdapter (
     binding:ItemAppointmentAdminBinding
): AppointmentViewBinding {

    override val root = binding.root
    override val tvPetName = binding.tvPetName
    override val tvOwnerName = binding.tvOwnerName
    override val tvDoctorName = binding.tvDoctorName
    override val tvServiceName = binding.tvServiceName
    override val tvTime = binding.tvTime
    override val tvPetAge = binding.tvPetAge
    override val tvStatus = binding.tvStatus
    override val ivBell = binding.ivBell
}