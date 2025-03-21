package com.example.vetclinic.presentation.adapter.doctorsAdapter

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.vetclinic.R
import com.example.vetclinic.databinding.ItemDoctorBinding
import com.example.vetclinic.domain.entities.Doctor

class DoctorViewHolder(
    private val binding: ItemDoctorBinding,
    private val listener: OnAppointmentClickListener
) : RecyclerView.ViewHolder(binding.root) {


    init {
        binding.bookAppointmentButton.setOnClickListener {
            val doctor = it.tag as? Doctor
            doctor?.let { listener.onBookButtonClick(it) }

        }
    }

    fun bind(doctor: Doctor) {

        with(binding) {
            nameTextView.text = doctor.doctorName
            roleTextView.text = doctor.role


            Glide.with(doctorImageView.context)
                .load(doctor.photoUrl)
                .placeholder(R.drawable.placeholder_circle)
                .circleCrop()
                .into(doctorImageView)


            bookAppointmentButton.tag = doctor
        }

    }

}