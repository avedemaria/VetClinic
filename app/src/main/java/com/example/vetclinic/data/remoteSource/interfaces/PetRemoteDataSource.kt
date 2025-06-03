package com.example.vetclinic.data.remoteSource.interfaces

import com.example.vetclinic.data.remoteSource.network.model.PetDto

interface PetRemoteDataSource {

    suspend fun updatePetInSupabaseDb(petId: String, updatedPet: PetDto): Result<Unit>

    suspend fun addPetToSupabaseDb(pet: PetDto): Result<Unit>

    suspend fun getPetsFromSupabaseDb(userId: String): Result<List<PetDto>>

    suspend fun deletePetFromSupabaseDb(petId: String): Result<Unit>
}