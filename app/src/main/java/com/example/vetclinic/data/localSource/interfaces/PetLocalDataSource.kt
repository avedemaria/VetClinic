package com.example.vetclinic.data.localSource.interfaces

import com.example.vetclinic.data.localSource.database.models.PetDbModel

interface PetLocalDataSource {

    suspend fun addPetListToRoom(pets: List<PetDbModel>): Result<Unit>

    suspend fun addPetToRoom(pet: PetDbModel): Result<Unit>

    suspend fun updatePetInRoom(pet: PetDbModel): Result<Unit>

    suspend fun getPetsFromRoom(userId: String): Result<List<PetDbModel>>

    suspend fun deletePetFromRoom(pet: PetDbModel): Result<Unit>
}