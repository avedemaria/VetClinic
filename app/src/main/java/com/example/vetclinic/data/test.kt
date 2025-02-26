package com.example.vetclinic.data

import com.example.vetclinic.data.network.SupabaseApiFactory
import com.example.vetclinic.data.network.SupabaseApiService
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
//
//fun main() = runBlocking {
//    println(fetchServicesNames())
//}
//
//suspend fun fetchServicesNames(): List<String>? {
//    val api = SupabaseApiFactory.apiService
//    val response = api.getServicesName()
//
//    return if (response.isSuccessful) {
//        response.body()
//            ?.map { it.serviceName }
//    } else {
//        null
//    }
//}



