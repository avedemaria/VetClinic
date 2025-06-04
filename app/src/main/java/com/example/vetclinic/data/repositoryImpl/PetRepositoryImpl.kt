package com.example.vetclinic.data.repositoryImpl

import com.example.vetclinic.data.localSource.interfaces.PetLocalDataSource
import com.example.vetclinic.data.mapper.PetMapper
import com.example.vetclinic.data.remoteSource.interfaces.PetRemoteDataSource
import com.example.vetclinic.domain.entities.pet.Pet
import com.example.vetclinic.domain.repository.PetRepository
import jakarta.inject.Inject
import timber.log.Timber

class PetRepositoryImpl @Inject constructor(
    private val petRemoteDataSource: PetRemoteDataSource,
    private val petLocalDataSource: PetLocalDataSource,
    private val petMapper: PetMapper,
) : PetRepository {


    override suspend fun getPetsFromSupabaseDb(userId: String): Result<List<Pet>> {
        return petRemoteDataSource.getPetsFromSupabaseDb(userId)
            .mapCatching { petDtos ->
                if (petDtos.isNotEmpty()) {
                    val petDbModels = petDtos.map { dto -> petMapper.petDtoToPetDbModel(dto) }
                    petLocalDataSource.addPetListToRoom(petDbModels).getOrThrow()
                }
                petDtos.map { dto -> petMapper.petDtoToPetEntity(dto) }
            }
            .onFailure { error ->
                Timber.tag(TAG).e(error, "Failed to get pets from Supabase")
            }
    }


    override suspend fun addPetToSupabaseDb(pet: Pet): Result<Unit> {
        return runCatching {
            val dto = petMapper.petEntityToPetDto(pet)
            petRemoteDataSource.addPetToSupabaseDb(dto).getOrThrow()
        }.onFailure {
            Timber.tag(TAG).e(it, "Failed to add pet to Supabase")
        }
    }


    override suspend fun updatePetInSupabaseDb(petId: String, updatedPet: Pet): Result<Unit> {
        return petMapper.petEntityToPetDto(updatedPet).let { petDto ->
            petRemoteDataSource.updatePetInSupabaseDb(petId, petDto)
                .mapCatching {
                    updatePetInRoom(updatedPet).getOrThrow()
                }
                .onFailure {
                    Timber.tag(TAG).e(it, "Failed to update pet in Supabase")
                }
        }
    }

    override suspend fun deletePetFromSupabaseDb(pet: Pet): Result<Unit> {
        return petRemoteDataSource.deletePetFromSupabaseDb(pet.petId)
            .mapCatching {
                deletePetFromRoom(pet).getOrThrow()
            }
            .onFailure { error ->
                Timber.tag(TAG).e(error, "Error while deleting Pet in Supabase DB")
            }
    }


    override suspend fun addPetToRoom(pet: Pet): Result<Unit> = runCatching {
        val petDbModel = petMapper.petEntityToPetDbModel(pet)
        petLocalDataSource.addPetToRoom(petDbModel).getOrThrow()
    }.onFailure {
        Timber.tag(TAG).e(it, "Failed to add pet to Room")
    }


    override suspend fun updatePetInRoom(pet: Pet): Result<Unit> = runCatching {
        val petDbModel = petMapper.petEntityToPetDbModel(pet)
        petLocalDataSource.updatePetInRoom(petDbModel).getOrThrow()
    }.onFailure {
        Timber.tag(TAG).e(it, "Failed to update pet in Room")
    }


    override suspend fun getPetsFromRoom(userId: String): Result<List<Pet>> =
        petLocalDataSource.getPetsFromRoom(userId)
            .mapCatching { petDbModels ->
                if (petDbModels.isNotEmpty()) {
                    petMapper.petDbModelListToPetEntityList(petDbModels)
                } else {
                    emptyList()
                }
            }
            .onFailure { error ->
                Timber.tag(TAG).e(error, "Error while fetching pets from Room")
            }


    override suspend fun deletePetFromRoom(pet: Pet): Result<Unit> = runCatching {
        val petDbModel = petMapper.petEntityToPetDbModel(pet)
        petLocalDataSource.deletePetFromRoom(petDbModel).getOrThrow()
    }.onFailure {
        Timber.tag(TAG).e(it, "Failed to delete pet from Room")
    }

    companion object {
        private const val TAG = "PetRepositoryImpl"
    }
}