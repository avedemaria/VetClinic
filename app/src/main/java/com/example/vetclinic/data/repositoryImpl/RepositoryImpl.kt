package com.example.vetclinic.data.repositoryImpl

import android.util.Log
import com.example.vetclinic.data.database.model.VetClinicDao
import com.example.vetclinic.data.mapper.DepartmentMapper
import com.example.vetclinic.data.mapper.DoctorMapper
import com.example.vetclinic.data.mapper.PetMapper
import com.example.vetclinic.data.mapper.ServiceMapper
import com.example.vetclinic.data.mapper.UserMapper
import com.example.vetclinic.data.network.SupabaseApiService
import com.example.vetclinic.domain.repository.Repository
import com.example.vetclinic.domain.entities.department.Department
import com.example.vetclinic.domain.entities.doctor.Doctor
import com.example.vetclinic.domain.entities.pet.Pet
import com.example.vetclinic.domain.entities.service.Service
import com.example.vetclinic.domain.entities.user.User
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response


class RepositoryImpl @Inject constructor(
    private val supabaseApiService: SupabaseApiService,
    private val vetClinicDao: VetClinicDao,
    private val departmentMapper: DepartmentMapper,
    private val userMapper: UserMapper,
    private val petMapper: PetMapper,
    private val doctorMapper: DoctorMapper,
    private val serviceMapper: ServiceMapper,
) : Repository {



//SupabaseDB

    override suspend fun getUserFromSupabaseDb(userId: String): Result<User?> = runCatching {

        val idWithParameter = "eq.$userId"
        val response = supabaseApiService.getUserFromSupabaseDbById(idWithParameter)

        if (!response.isSuccessful) {
            throw Exception("Server error: ${response.code()} - ${response.errorBody()?.string()}")
        }

        val userDtos = response.body() ?: emptyList()
        val userDto = userDtos.find { it.uid == userId }

        if (userDto != null) {
            val user = userMapper.userDtoToUserDbModel(userDto)
            vetClinicDao.insertUser(user)
        }

        return@runCatching userDto?.let { userMapper.userDtoToUserEntity(it) }
    }.onFailure { e ->
        Log.e("RepositoryImpl", "Error fetching User: ${e.message}", e)
    }


    override suspend fun getPetsFromSupabaseDb(userId: String): Result<List<Pet>> =
        kotlin.runCatching {

            val idWithParameter = "eq.${userId}"

            val response = supabaseApiService.getPetsFromSupabaseDb(idWithParameter)

            if (!response.isSuccessful) {
                throw Exception(
                    "Server error: ${response.code()} - ${response.errorBody()?.string()}"
                )
            }
            val petDtos = response.body() ?: emptyList()
            if (petDtos.isNotEmpty()) {
                val petDbModels = petDtos.map { petMapper.petDtoToPetDbModel(it) }
                Log.d(TAG, "PetDbModels first of map: $petDbModels")
                vetClinicDao.insertPets(petDbModels)

            }

            petDtos.map { petMapper.petDtoToPetEntity(it) }
        }.onFailure { e ->
            Log.e(TAG, "Error fetching Pet: ${e.message}", e)
        }


    override suspend fun addUserToSupabaseDb(user: User): Result<Unit> = addDataToSupabaseDb(
        entity = user,
        apiCall = { userDto -> supabaseApiService.addUser(userDto) },
        mapper = { userMapper.userEntityToUserDto(user) }
    )

    override suspend fun addPetToSupabaseDb(pet: Pet): Result<Unit> = addDataToSupabaseDb(
        entity = pet,
        apiCall = { petDto -> supabaseApiService.addPet(petDto) },
        mapper = { petMapper.petEntityToPetDto(pet) }
    )

    override suspend fun updateUserInSupabaseDb(userId: String, updatedUser: User): Result<Unit> =
        kotlin.runCatching {

            val userIdWithParam = "eq.$userId"
            val updatedUserDto = userMapper.userEntityToUserDto(updatedUser)
            val response = supabaseApiService.updateUser(userIdWithParam, updatedUserDto)

            if (response.isSuccessful) {
                Log.d(TAG, "Successfully updated user in Supabase DB")
                updateUserInRoom(updatedUser)
                Unit
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(TAG, "Failed to update User. Error: $errorBody")
                throw Exception("Failed to update User. ${response.code()} - $errorBody")
            }
        }
            .onFailure { error ->
                Log.e(TAG, "Error while updating User in Supabase DB", error)
            }


    override suspend fun updatePetInSupabaseDb(petId: String, updatedPet: Pet): Result<Unit> =
        kotlin.runCatching {

            Log.d(TAG, "Updating pet with ID: $petId")
            val idWithOperator = "eq.$petId"
            val updatedPetDto = petMapper.petEntityToPetDto(updatedPet)
            val response = supabaseApiService.updatePet(idWithOperator, updatedPetDto)

            if (response.isSuccessful) {
                Log.d(TAG, "Successfully updated pet in Supabase DB")
                updatePetInRoom(updatedPet)
                Unit
            } else {
                val errorBody = response.errorBody()?.string()
                throw Exception("Failed to update pet. ${response.code()} - $errorBody")
            }
        }
            .onFailure { error ->
                Log.e(TAG, "Error while updating Pet in Supabase DB", error)
            }


    override suspend fun deletePetFromSupabaseDb(pet: Pet): Result<Unit> = kotlin.runCatching {
        deletePetFromRoom(pet)

        val idWithOperator = "eq.${pet.petId}"
        val response = supabaseApiService.deletePet(idWithOperator)

        if (!response.isSuccessful) {
            addPetToRoom(pet)
            val errorBody = response.errorBody()?.string()
            throw Exception("Failed to delete pet. ${response.code()} - $errorBody")
        }
        Log.d(TAG, "Successfully deleted pet in Supabase DB")
        Unit
    }.onFailure { error ->
        Log.e(TAG, "Error while deleting Pet in Supabase DB", error)
    }

    override suspend fun getDoctorList(): Result<List<Doctor>> = fetchData(
        apiCall = { supabaseApiService.getDoctors() },
        mapper = { body -> doctorMapper.doctorDtoListToDoctorEntityList(body) },
        DOCTOR_LIST_TAG
    )


    override suspend fun getDepartmentList(): Result<List<Department>> =
        fetchData(
            apiCall = { supabaseApiService.getDepartments() },
            mapper = { departmentMapper.departmentDtoListToDepartmentEntityList(it) },
            DEPARTMENT_LIST_TAG
        )

    override suspend fun getServiceList(): Result<List<Service>> =
        fetchData(
            apiCall = { supabaseApiService.getServices() },
            mapper = { serviceMapper.serviceDtoListToServiceEntityList(it) },
            SERVICE_LIST_TAG
        )

    override suspend fun getServicesByDepartmentId(departmentId: String): Result<List<Service>> =
        kotlin.runCatching {

            val departmentIdWithParam = "eq.$departmentId"

            Log.d(TAG, "Fetching services for department: $departmentIdWithParam")
            val response = supabaseApiService.getServicesByDepartmentId(departmentIdWithParam)
            val servicesDto = response.body()
                ?: throw Exception("Empty response body:${response.code()} - ${response.message()}")
            if (response.isSuccessful) {
                Log.d(TAG, "Successfully fetched services by $departmentId in Supabase DB")
                serviceMapper.serviceDtoListToServiceEntityList(servicesDto)
            } else {
                throw Exception("Server's error: ${response.code()} - ${response.message()}")
            }
        }
            .onFailure { e -> Log.e(TAG, "Error fetching serviced {$e.message}") }

    private suspend fun <T, R> addDataToSupabaseDb(
        entity: T,
        apiCall: suspend (R) -> Response<Unit>,
        mapper: (T) -> R,
    ): Result<Unit> = runCatching {
        val mappedEntity = mapper(entity)
        val response = apiCall(mappedEntity)

        if (response.isSuccessful) {
            Log.d(TAG, "Successfully added $entity to Supabase DB")
            Unit
        } else {
            val errorBody = response.errorBody()?.string()
            Log.e(TAG, "Failed to add $entity. Error: $errorBody")
            throw Exception("Failed to add $entity: ${response.code()} - $errorBody")
        }
    }.onFailure { e ->
        Log.e(TAG, "Error adding entity: ${e.message}")
        e.printStackTrace()
    }


    private suspend fun <T, R> fetchData(
        apiCall: suspend () -> Response<T>,
        mapper: (T) -> R,
        entityTag: String,
    ): Result<R> = kotlin.runCatching {
        val response = apiCall()
        val body = response.body()
            ?: throw Exception("Empty response body:${response.code()} - ${response.message()}")

        if (response.isSuccessful) {
            mapper(body)
        } else {
            throw Exception("Server's error: ${response.code()} - ${response.message()}")
        }
    }.onFailure { e ->
        Log.e(TAG, "Error fetching $entityTag {$e.message}")
    }


//Room

    override suspend fun addUserAndPetToRoom(user: User, pet: Pet) {
        addUserToRoom(user)
        addPetToRoom(pet)
    }


    override suspend fun addUserToRoom(user: User) {
        vetClinicDao.insertUser(userMapper.userEntityToUserDbModel(user))
    }

    override suspend fun addPetToRoom(pet: Pet): Result<Unit> = kotlin.runCatching {

        val models = petMapper.petEntityToPetDbModel(pet)
        Log.d(TAG, "Pet model is: $models")
        vetClinicDao.insertPet(models)

    }
        .onFailure { error ->
            Log.e(TAG, "Error adding pet to Room $error")
        }

    override suspend fun getCurrentUserFromRoom(userId: String): Result<User> =
        runCatching {
            withContext(Dispatchers.IO) {
                val userDbModel = vetClinicDao.getUserById(userId)
                    ?: throw NoSuchElementException("User with ID $userId not found in Room")
                userMapper.userDbModelToUserEntity(userDbModel)
            }
        }.onFailure {
            Log.e(TAG, "Error while getting user from Room", it)
        }

    override suspend fun updateUserInRoom(user: User): Result<Unit> = kotlin.runCatching {

        val userDbModel = userMapper.userEntityToUserDbModel(user)
        vetClinicDao.updateUser(userDbModel)
        Log.d(TAG, "User updated successfully in Room")
        Unit
    }
        .onFailure { error ->
            Log.e(TAG, "Error updating user in Room", error)
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

    override suspend fun clearAllData() {
        vetClinicDao.clearAllPets()
        vetClinicDao.clearUserData()
        vetClinicDao.clearAllAppointments()
    }


    companion object {
        private const val TAG = "RepositoryImpl"
        private const val SERVICE_LIST_TAG = "services"
        private const val DOCTOR_LIST_TAG = "doctors"
        private const val DEPARTMENT_LIST_TAG = "departments"
    }
}









