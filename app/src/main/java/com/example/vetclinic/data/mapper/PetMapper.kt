package com.example.vetclinic.data.mapper

import com.example.vetclinic.data.database.model.PetDbModel
import com.example.vetclinic.data.network.model.PetDto
import com.example.vetclinic.domain.entities.Pet
import jakarta.inject.Inject

class PetMapper @Inject constructor() {

    fun petEntityToPetDbModel(entity: Pet): PetDbModel {
        return PetDbModel(
            petId = entity.petId,
            userId = entity.userId,
            petName = entity.petName,
            petType = entity.petType,
            petAge = entity.petAge
        )
    }

    fun petEntityToPetDto(entity: Pet): PetDto {
        return PetDto(
            petId = entity.petId,
            userId = entity.userId,
            petName = entity.petName,
            petType = entity.petType,
            petAge = entity.petAge
        )
    }
}