package com.example.vetclinic.presentation.adapter

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.vetclinic.R
import com.example.vetclinic.databinding.ItemDoctorBinding
import com.example.vetclinic.domain.selectDoctorFeature.Doctor

class DoctorViewHolder(
    val binding: ItemDoctorBinding,
    private val listener: OnBookButtonClickListener
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(doctor: Doctor) {

        with(binding) {
            nameTextView.text = doctor.doctorName
            roleTextView.text = doctor.role


            Glide.with(doctorImageView.context)
                .load(doctor.photoUrl)
                .placeholder(R.drawable.placeholder_circle)
                .circleCrop()
                .into(doctorImageView)


            bookAppointmentButton.setOnClickListener {
                listener.onBookButtonClick(doctor)
            }
        }


    }

    interface OnBookButtonClickListener {
        fun onBookButtonClick(doctor: Doctor)
    }
}