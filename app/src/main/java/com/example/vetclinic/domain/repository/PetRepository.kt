package com.example.vetclinic.domain.repository

import com.example.vetclinic.domain.entities.pet.Pet

interface PetRepository {

    suspend fun updatePetInSupabaseDb(petId: String, updatedPet: Pet): Result<Unit>

    suspend fun addPetToSupabaseDb(pet: Pet): Result<Unit>

    suspend fun getPetsFromSupabaseDb(userId: String): Result<List<Pet>>

    suspend fun deletePetFromSupabaseDb(pet: Pet): Result<Unit>

    suspend fun addPetToRoom(pet: Pet): Result<Unit>

    suspend fun updatePetInRoom(pet: Pet): Result<Unit>

    suspend fun getPetsFromRoom(userId: String): Result<List<Pet>>

    suspend fun deletePetFromRoom(pet: Pet)


}