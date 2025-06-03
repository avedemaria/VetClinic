package com.example.vetclinic.data.repositoryImpl

import android.util.Log
import com.example.vetclinic.data.localSource.database.VetClinicDao
import com.example.vetclinic.data.mapper.PetMapper
import com.example.vetclinic.data.remoteSource.interfaces.PetRemoteDataSource
import com.example.vetclinic.domain.entities.pet.Pet
import com.example.vetclinic.domain.repository.PetRepository
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class PetRepositoryImpl @Inject constructor(
    private val petRemoteDataSource: PetRemoteDataSource,
    private val petMapper: PetMapper,
    private val vetClinicDao: VetClinicDao,
) : PetRepository {


    override suspend fun getPetsFromSupabaseDb(userId: String): Result<List<Pet>> {
        return petRemoteDataSource.getPetsFromSupabaseDb(userId)
            .mapCatching { petDtos ->
                petDtos.also {
                    if (it.isNotEmpty()) {
                        val petDbModels = it.map { dto -> petMapper.petDtoToPetDbModel(dto) }
                        vetClinicDao.insertPets(petDbModels)
                    }
                }
                    .map { dto -> petMapper.petDtoToPetEntity(dto) }
            }
    }


    override suspend fun addPetToSupabaseDb(pet: Pet): Result<Unit> {
        return runCatching {
            val dto = petMapper.petEntityToPetDto(pet)
           petRemoteDataSource.addPetToSupabaseDb(dto).getOrThrow()
        }
    }


    override suspend fun updatePetInSupabaseDb(petId: String, updatedPet: Pet): Result<Unit> {
        return runCatching {
            val petDto = petMapper.petEntityToPetDto(updatedPet)
            petRemoteDataSource.updatePetInSupabaseDb(petId, petDto).getOrThrow()
                .also { updatePetInRoom(updatedPet) }
        }
    }

    override suspend fun deletePetFromSupabaseDb(pet: Pet): Result<Unit> {
        return petRemoteDataSource.deletePetFromSupabaseDb(pet.petId)
            .mapCatching {
                deletePetFromRoom(pet)
                Timber.d("Successfully deleted pet in Supabase DB")
            }
            .onFailure { error ->
                Timber.e(error, "Error while deleting Pet in Supabase DB")
            }
    }


    override suspend fun addPetToRoom(pet: Pet): Result<Unit> = kotlin.runCatching {

        val models = petMapper.petEntityToPetDbModel(pet)
        Log.d(TAG, "Pet model is: $models")
        vetClinicDao.insertPet(models)

    }
        .onFailure { error ->
            Log.e(TAG, "Error adding pet to Room $error")
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