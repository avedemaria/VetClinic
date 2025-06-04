package com.example.vetclinic.data.dataSourceImpl.localSourceImpl

import com.example.vetclinic.data.DataSourceUtils
import com.example.vetclinic.data.localSource.database.VetClinicDao
import com.example.vetclinic.data.localSource.database.models.PetDbModel
import com.example.vetclinic.data.localSource.interfaces.PetLocalDataSource
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PetLocalDataSourceImpl @Inject constructor(private val vetClinicDao: VetClinicDao) :
    PetLocalDataSource {


    override suspend fun addPetListToRoom(pets: List<PetDbModel>): Result<Unit> {
        return DataSourceUtils.executeRoomCall { vetClinicDao.insertPets(pets) }
    }

    override suspend fun addPetToRoom(pet: PetDbModel): Result<Unit> {
        return DataSourceUtils.executeRoomCall { vetClinicDao.insertPet(pet) }
    }

    override suspend fun updatePetInRoom(pet: PetDbModel): Result<Unit> {
        return DataSourceUtils.executeRoomCall { vetClinicDao.updatePet(pet) }
    }

    override suspend fun getPetsFromRoom(userId: String): Result<List<PetDbModel>> {
        return withContext(Dispatchers.IO) {
            DataSourceUtils.executeRoomCall {
                vetClinicDao.getPetsByUserId(userId)
            }
        }
    }

    override suspend fun deletePetFromRoom(pet: PetDbModel): Result<Unit> {
        return DataSourceUtils.executeRoomCall { vetClinicDao.deletePet(pet) }
    }
}