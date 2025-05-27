package com.example.vetclinic.data.repositoryImpl

import android.util.Log
import com.example.vetclinic.data.localSource.database.VetClinicDao
import com.example.vetclinic.data.mapper.PetMapper
import com.example.vetclinic.data.remoteSource.network.SupabaseApiService
import com.example.vetclinic.domain.entities.pet.Pet
import com.example.vetclinic.domain.repository.PetRepository
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PetRepositoryImpl @Inject constructor(
    private val supabaseApiService: SupabaseApiService,
    private val petMapper: PetMapper,
    private val vetClinicDao: VetClinicDao,
) : PetRepository {

    override suspend fun getPetsFromSupabaseDb(userId: String): Result<List<Pet>> =
        kotlin.runCatching {

            val idWithParameter = "eq.${userId}"

            val response = supabaseApiService.getPetsFromSupabaseDb(idWithParameter)

            if (!response.isSuccessful) {
                throw Exception(
                    "Server error: ${response.code()} - ${response.errorBody()?.string()}"
                )
            }
            val petDtos = response.body() ?: emptyList()
            if (petDtos.isNotEmpty()) {
                val petDbModels = petDtos.map { petMapper.petDtoToPetDbModel(it) }
                Log.d(TAG, "PetDbModels first of map: $petDbModels")
                vetClinicDao.insertPets(petDbModels)

            }

            petDtos.map { petMapper.petDtoToPetEntity(it) }
        }.onFailure { e ->
            Log.e(TAG, "Error fetching Pet: ${e.message}", e)
        }


    override suspend fun addPetToSupabaseDb(pet: Pet): Result<Unit> =
        RepositoryUtils.addDataToSupabaseDb(
            entity = pet,
            apiCall = { petDto -> supabaseApiService.addPet(petDto) },
            mapper = { petMapper.petEntityToPetDto(pet) }
        )


    override suspend fun updatePetInSupabaseDb(petId: String, updatedPet: Pet): Result<Unit> =
        kotlin.runCatching {

            Log.d(TAG, "Updating pet with ID: $petId")
            val idWithOperator = "eq.$petId"
            val updatedPetDto = petMapper.petEntityToPetDto(updatedPet)
            val response = supabaseApiService.updatePet(idWithOperator, updatedPetDto)

            if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                Log.d(TAG, "Successfully updated pet in Supabase DB")
                updatePetInRoom(updatedPet)
                Unit
            } else {
                val errorBody = response.errorBody()?.string()
                throw Exception("Pet update likely blocked by RLS or ID not matched." +
                        " ${response.code()} - $errorBody")
            }
        }
            .onFailure { error ->
                Log.e(TAG, "Error while updating Pet in Supabase DB", error)
            }


    override suspend fun addPetToRoom(pet: Pet): Result<Unit> = kotlin.runCatching {

        val models = petMapper.petEntityToPetDbModel(pet)
        Log.d(TAG, "Pet model is: $models")
        vetClinicDao.insertPet(models)

    }
        .onFailure { error ->
            Log.e(TAG, "Error adding pet to Room $error")
        }


    override suspend fun deletePetFromSupabaseDb(pet: Pet): Result<Unit> = kotlin.runCatching {
        val idWithOperator = "eq.${pet.petId}"
        val response = supabaseApiService.deletePet(idWithOperator)

        if (!response.isSuccessful || response.body().isNullOrEmpty()) {
            val errorBody = response.errorBody()?.string()
            throw Exception("Failed to delete pet. Possibly blocked by RLS." +
                    " ${response.code()} - $errorBody")
        }
        deletePetFromRoom(pet)
        Log.d(TAG, "Successfully deleted pet in Supabase DB")
        Unit
    }.onFailure { error ->
        Log.e(TAG, "Error while deleting Pet in Supabase DB", error)
    }

    override suspend fun updatePetInRoom(pet: Pet): Result<Unit> = kotlin.runCatching {
        val petDbModel = petMapper.petEntityToPetDbModel(pet)
        vetClinicDao.updatePet(petDbModel)
        Log.d(TAG, "Pet updated successfully in Room")
        Unit
    }
        .onFailure { error ->
            Log.e(TAG, "Error updating pet in Room", error)
        }


    override suspend fun getPetsFromRoom(userId: String): Result<List<Pet>> =
        kotlin.runCatching {
            withContext(Dispatchers.IO) {
                val petDbModelList = vetClinicDao.getPetsByUserId(userId)

                if (petDbModelList.isEmpty()) {
                    emptyList<Pet>()
                }

                petMapper.petDbModelListToPetEntityList(petDbModelList)
            }
        }.onFailure {
            Log.e(TAG, "Error while getting pets from Room", it)
        }


    override suspend fun deletePetFromRoom(pet: Pet) {
        try {
            vetClinicDao.deletePet(petMapper.petEntityToPetDbModel(pet))
            Log.d(TAG, "Pet was deleted successfully from Room")
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting pet from Room", e)
        }

    }


    companion object {
        private const val TAG = "PetRepositoryImpl"
    }
}