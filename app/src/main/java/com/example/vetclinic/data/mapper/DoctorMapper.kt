package com.example.vetclinic.data.mapper

import com.example.vetclinic.CodeReview
import com.example.vetclinic.data.network.model.DoctorDto
import com.example.vetclinic.domain.selectDoctorFeature.Doctor
import jakarta.inject.Inject

class DoctorMapper @Inject constructor() {

    fun doctorDtoToDoctorEntity(doctorDto: DoctorDto): Doctor {
        return Doctor(
            uid = doctorDto.uid,
            doctorName = doctorDto.doctorName,
            department = doctorDto.department,
            role = doctorDto.role,
            photoUrl = doctorDto.photoUrl
        )
    }


    fun doctorDtoListToDoctorEntityList(doctorDtoList: List<DoctorDto>): List<Doctor> {
        @CodeReview("Можно не создавать анонимную функцию, а использовать существующую")
        return doctorDtoList.map(::doctorDtoToDoctorEntity) // Разница заметна только на больших списках

    }
}