package com.example.vetclinic.data.mapper

import com.example.vetclinic.data.localSource.database.models.UserDbModel
import com.example.vetclinic.data.remoteSource.network.model.UserDto
import com.example.vetclinic.domain.entities.user.User
import jakarta.inject.Inject

class UserMapper @Inject constructor() {

    fun userEntityToUserDto(entity: User): UserDto {

        return UserDto(
            uid = entity.uid,
            userName = entity.userName,
            userLastName = entity.userLastName,
            phoneNumber = entity.phoneNumber,
            email = entity.email,
            role = entity.role
        )
    }

    fun userDtoToUserDbModel(dto: UserDto): UserDbModel {
        return UserDbModel(
            uid = dto.uid,
            userName = dto.userName,
            userLastName = dto.userLastName,
            phoneNumber = dto.phoneNumber,
            email = dto.email,
            role = dto.role
        )
    }


    fun userDbModelToUserEntity(dbModel: UserDbModel): User {
        return User(
            uid = dbModel.uid,
            userName = dbModel.userName,
            userLastName = dbModel.userLastName,
            phoneNumber = dbModel.phoneNumber,
            email = dbModel.email,
            role = dbModel.role
        )
    }

    fun userEntityToUserDbModel(entity: User): UserDbModel {
        return UserDbModel(
            uid = entity.uid,
            userName = entity.userName,
            userLastName = entity.userLastName,
            phoneNumber = entity.phoneNumber,
            email = entity.email,
            role = entity.role
        )
    }

    fun userDtoToUserEntity(dto: UserDto): User {
        return User(
            uid = dto.uid,
            userName = dto.userName,
            userLastName = dto.userLastName,
            phoneNumber = dto.phoneNumber,
            email = dto.email,
            role = dto.role
        )
    }

}