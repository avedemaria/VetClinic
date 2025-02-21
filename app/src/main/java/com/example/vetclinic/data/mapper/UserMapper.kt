package com.example.vetclinic.data.mapper

import com.example.vetclinic.data.database.model.UserDbModel
import com.example.vetclinic.data.network.model.UserDTO
import com.example.vetclinic.domain.authFeature.User
import jakarta.inject.Inject

class UserMapper @Inject constructor() {

    fun userEntityToUserDto(entity: User): UserDTO {

        return UserDTO(
            uid = entity.uid,
            userName = entity.userName,
            userLastName = entity.userLastName,
            petName = entity.petName,
            phoneNumber = entity.phoneNumber,
            email = entity.email
        )
    }

    fun userDtoToUserDbModel(dto: UserDTO): UserDbModel {
        return UserDbModel(
            uid = dto.uid,
            userName = dto.userName,
            userLastName = dto.userLastName,
            petName = dto.petName,
            phoneNumber = dto.phoneNumber,
            email = dto.email
        )
    }
}