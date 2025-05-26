package com.example.vetclinic.data

import com.example.vetclinic.data.localSource.database.VetClinicDao
import com.example.vetclinic.domain.LocalDataCleaner
import jakarta.inject.Inject

class LocalDataCleanerImpl @Inject constructor(
    private val vetClinicDao: VetClinicDao
) : LocalDataCleaner {

    override suspend fun clearAllLocalData() {
        vetClinicDao.clearUserData()
        vetClinicDao.clearAllPets()
        vetClinicDao.clearAllAppointments()
    }
}