package com.example.vetclinic.data.dataSourceImpl.remoteSourceImpl

import com.example.vetclinic.data.DataSourceUtils
import com.example.vetclinic.data.remoteSource.interfaces.PetRemoteDataSource
import com.example.vetclinic.data.remoteSource.network.SupabaseApiService
import com.example.vetclinic.data.remoteSource.network.model.PetDto
import jakarta.inject.Inject

class PetRemoteDataSourceImpl @Inject constructor(private val supabaseApiService: SupabaseApiService) :
    PetRemoteDataSource {

    override suspend fun updatePetInSupabaseDb(petId: String, updatedPet: PetDto): Result<Unit> {
        return DataSourceUtils.executeApiCall {
            supabaseApiService.updatePet(
                "eq.$petId",
                updatedPet
            )
        }.mapCatching { petList ->
            if (petList.isEmpty()) {
                throw Exception("No pets updated - possible RLS block or incorrect ID")
            } else {
                Unit
            }
        }
    }

    override suspend fun addPetToSupabaseDb(pet: PetDto): Result<Unit> {
        return DataSourceUtils.executeApiCall {
            supabaseApiService.addPet(pet)
        }
    }

    override suspend fun getPetsFromSupabaseDb(userId: String): Result<List<PetDto>> {
        return DataSourceUtils.executeApiCall {
            supabaseApiService.getPetsFromSupabaseDb("eq.$userId")
        }
    }

    override suspend fun deletePetFromSupabaseDb(petId: String): Result<Unit> {
        return DataSourceUtils.executeApiCall {
            supabaseApiService.deletePet("eq.$petId")
        }.mapCatching { petList ->
            if (petList.isEmpty()) {
                throw Exception("No pets updated - possible RLS block or incorrect ID")
            } else {
                Unit
            }
        }
    }
}