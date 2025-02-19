package com.example.vetclinic.data.mapper

import com.example.vetclinic.data.network.model.DoctorDto
import com.example.vetclinic.domain.selectDoctorFeature.Doctor
import jakarta.inject.Inject

class DoctorMapper @Inject constructor() {

    fun doctorDtoToDoctorEntity(doctorDto: DoctorDto): Doctor {
        return Doctor(
            uid = doctorDto.uid,
            doctorName = doctorDto.doctorName,
            doctorLastName = doctorDto.doctorLastName,
            department = doctorDto.department,
            role = doctorDto.role,
            photoUrl = doctorDto.photoUrl
        )
    }


    fun doctorDtoListToDoctorEntityList(doctorDtoList: List<DoctorDto>): List<Doctor> {
        return doctorDtoList.map { doctorDtoToDoctorEntity(it) }
    }
}