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
            petBday = entity.petBDay,
            petType = entity.petType,
            petGender = entity.petGender,
            petAge = entity.petAge
        )
    }

    fun petEntityToPetDto(entity: Pet): PetDto {
        return PetDto(
            petId = entity.petId,
            userId = entity.userId,
            petName = entity.petName,
            petType = entity.petType,
            petAge = entity.petAge,
            petBday = entity.petBDay,
            petGender = entity.petGender
        )
    }

    fun petDtoToPetEntity(dto: PetDto): Pet {
        return Pet(
            petId = dto.petId,
            userId = dto.userId,
            petName = dto.petName,
            petBDay = dto.petBday,
            petType = dto.petType,
            petGender = dto.petGender,
            petAge = dto.petAge
        )
    }


    fun petDbModelToPetEntity(dbModel: PetDbModel): Pet {
        return Pet(
            petId = dbModel.petId,
            userId = dbModel.userId,
            petName = dbModel.petName,
            petBDay = dbModel.petBday,
            petType = dbModel.petType,
            petAge = dbModel.petAge,
            petGender = dbModel.petGender
        )
    }

    fun petDbModelListToPetEntityList(petDbModelList: List<PetDbModel>): List<Pet> {
        return petDbModelList.map(::petDbModelToPetEntity)
    }
}