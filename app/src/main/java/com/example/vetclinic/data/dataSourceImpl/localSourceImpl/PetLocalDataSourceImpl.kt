package com.example.vetclinic.data.dataSourceImpl.localSourceImpl

import com.example.vetclinic.data.localSource.database.VetClinicDao
import com.example.vetclinic.data.localSource.database.models.PetDbModel
import com.example.vetclinic.data.localSource.interfaces.PetLocalDataSource
import jakarta.inject.Inject

class PetLocalDataSourceImpl @Inject constructor(private val vetClinicDao: VetClinicDao) :
    PetLocalDataSource {

    override suspend fun addPetToRoom(pet: PetDbModel): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun updatePetInRoom(pet: PetDbModel): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun getPetsFromRoom(userId: String): Result<List<PetDbModel>> {
        TODO("Not yet implemented")
    }

    override suspend fun deletePetFromRoom(pet: PetDbModel) {
        TODO("Not yet implemented")
    }
}