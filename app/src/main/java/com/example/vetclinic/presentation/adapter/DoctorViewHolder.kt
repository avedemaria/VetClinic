package com.example.vetclinic.presentation.adapter

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.vetclinic.CodeReview
import com.example.vetclinic.R
import com.example.vetclinic.databinding.ItemDoctorBinding
import com.example.vetclinic.domain.selectDoctorFeature.Doctor

class DoctorViewHolder(
    private val binding: ItemDoctorBinding,
    private val listener: OnAppointmentClickListener
) : RecyclerView.ViewHolder(binding.root) {

    init {
        @CodeReview("Привязываем один раз")
        binding.bookAppointmentButton.setOnClickListener {
            val doctor = it.tag as? Doctor
            doctor?.let { listener.onBookButtonClick(it) }
        }
    }

    @CodeReview("Каждый раз при вызове bind(doctor) создается новый setOnClickListener()")
// Было: bookAppointmentButton.setOnClickListener {
//                listener.onBookButtonClick(doctor)
//            }
    fun bind(doctor: Doctor) {
        with(binding) {
            nameTextView.text = doctor.doctorName
            roleTextView.text = doctor.role

            @CodeReview("!!! Внутри надо просто отображать данные. Никаких запросов в сеть")
            Glide.with(doctorImageView.context)
                .clear(doctorImageView) // Очищаем перед загрузкой, чтобы не показывать старые картинки
            Glide.with(doctorImageView.context)
                .load(doctor.photoUrl)
                .placeholder(R.drawable.placeholder_circle)
                .circleCrop()
                .into(doctorImageView)

            bookAppointmentButton.tag = doctor  // Для setOnClickListener
        }
    }
}



