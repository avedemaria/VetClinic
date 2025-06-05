package com.example.vetclinic.presentation.adapter.adminAppointmentsAdapter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.vetclinic.R
import com.example.vetclinic.domain.entities.appointment.AppointmentStatus
import com.example.vetclinic.domain.entities.appointment.AppointmentWithDetails
import com.example.vetclinic.utils.extractTime
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel

object AdminAppointmentBindingHelper {


    fun setupListeners(
        binding: AppointmentViewBinding,
        listener: OnBellClickListener,
    ) {
        binding.ivBell.setOnLongClickListener {
            val appointment = it.tag as? AppointmentWithDetails
            appointment?.let { listener.onBellClicked(it) }
            true
        }
    }


    fun bind(
        binding: AppointmentViewBinding,
        appointment: AppointmentWithDetails,
    ) {
        with(binding) {
            tvPetName.text = appointment.petName
            tvOwnerName.text = root.context.getString(
                R.string.owner_full_name,
                appointment.userName,
                appointment.userLastName
            )
            tvDoctorName.text = root.context.getString(
                R.string.doctor_role_and_name,
                appointment.doctorRole,
                appointment.doctorName
            )
            tvServiceName.text = appointment.serviceName
            tvTime.text = appointment.dateTime.extractTime()
            tvPetAge.text = ", ${appointment.petBday}"

            tvStatus.text = when (appointment.status) {
                AppointmentStatus.SCHEDULED -> root.context.getString(R.string.status_scheduled)
                AppointmentStatus.CANCELLED -> root.context.getString(R.string.status_cancelled)
                AppointmentStatus.COMPLETED -> root.context.getString(R.string.status_completed)
            }

            tvStatus.background = createStatusBackground(
                context = root.context,
                status = appointment.status
            )
            Log.d("APPOINTMENT_BIND", "Binding id=${appointment.id}, isConfirmed=${appointment.isConfirmed}")

            ivBell.isSelected = appointment.isConfirmed

            ivBell.tag = appointment
        }
    }


    private fun createStatusBackground(
        context: Context,
        status: AppointmentStatus,
    ): Drawable {
        val color = when (status) {
            AppointmentStatus.SCHEDULED -> R.color.status_scheduled
            AppointmentStatus.CANCELLED -> R.color.status_cancelled
            AppointmentStatus.COMPLETED -> R.color.dark_grey
        }

        return MaterialShapeDrawable().apply {
            fillColor = ColorStateList.valueOf(ContextCompat.getColor(context, color))
            shapeAppearanceModel = ShapeAppearanceModel().withCornerSize(16f)
        }
    }

}
