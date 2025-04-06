package com.example.vetclinic.data

import AppointmentRemoteMediator
import com.example.vetclinic.data.database.model.VetClinicDao
import com.example.vetclinic.data.mapper.AppointmentMapper
import com.example.vetclinic.data.mapper.DoctorMapper
import com.example.vetclinic.data.mapper.PetMapper
import com.example.vetclinic.data.mapper.ServiceMapper
import com.example.vetclinic.data.mapper.UserMapper
import com.example.vetclinic.data.network.SupabaseApiService
import jakarta.inject.Inject

class AppointmentRemoteMediatorFactory @Inject constructor(
    private val vetClinicDao: VetClinicDao,
    private val supabaseApiService: SupabaseApiService,
    private val appointmentMapper: AppointmentMapper,
    private val petMapper: PetMapper,
    private val userMapper: UserMapper,
    private val doctorMapper: DoctorMapper,
    private val serviceMapper: ServiceMapper,
) {

    fun create(
        selectedDate: String,
    ): AppointmentRemoteMediator {
        return AppointmentRemoteMediator(
            selectedDate = selectedDate,
            vetClinicDao = vetClinicDao,
            supabaseApiService = supabaseApiService,
            appointmentMapper = appointmentMapper,
            petMapper = petMapper,
            userMapper = userMapper,
            doctorMapper = doctorMapper,
            serviceMapper = serviceMapper
        )
    }
}