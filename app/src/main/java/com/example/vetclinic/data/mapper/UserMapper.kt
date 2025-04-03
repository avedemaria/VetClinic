package com.example.vetclinic.data.mapper

import com.example.vetclinic.data.database.model.UserDbModel
import com.example.vetclinic.data.network.model.UserDTO
import com.example.vetclinic.domain.entities.User
import jakarta.inject.Inject

class UserMapper @Inject constructor() {

    fun userEntityToUserDto(entity: User): UserDTO {

        return UserDTO(
            uid = entity.uid,
            userName = entity.userName,
            userLastName = entity.userLastName,
            phoneNumber = entity.phoneNumber,
            email = entity.email,
            role = entity.role
        )
    }

    fun userDtoToUserDbModel(dto: UserDTO): UserDbModel {
        return UserDbModel(
            uid = dto.uid,
            userName = dto.userName,
            userLastName = dto.userLastName,
            phoneNumber = dto.phoneNumber,
            email = dto.email,
            role = dto.role
        )
    }

//    fun userDtoListToUserEntityList (): List<User> {
//
//    }

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

    fun userDtoToUserEntity(dto: UserDTO): User {
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