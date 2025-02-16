package com.example.vetclinic.data.network

import com.example.vetclinic.data.network.model.UserDTO
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson

class SingleUserAdapter {
    @FromJson
    fun fromJson(jsonList: List<UserDTO>?): UserDTO? {
        return jsonList?.firstOrNull()
    }

    @ToJson
    fun toJson(user: UserDTO?): List<UserDTO> {
        return user?.let { listOf(it) } ?: emptyList()
    }
}