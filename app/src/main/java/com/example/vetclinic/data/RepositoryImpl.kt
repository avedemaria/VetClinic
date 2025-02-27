package com.example.vetclinic.data

import android.util.Log
import com.example.vetclinic.data.database.model.UserDbModel
import com.example.vetclinic.data.database.model.VetClinicDao
import com.example.vetclinic.data.mapper.DepartmentMapper
import com.example.vetclinic.data.mapper.DoctorMapper
import com.example.vetclinic.data.mapper.PetMapper
import com.example.vetclinic.data.mapper.ServiceMapper
import com.example.vetclinic.data.mapper.UserMapper
import com.example.vetclinic.data.network.SupabaseApiService
import com.example.vetclinic.domain.Repository
import com.example.vetclinic.domain.entities.Department
import com.example.vetclinic.domain.entities.Doctor
import com.example.vetclinic.domain.entities.Pet
import com.example.vetclinic.domain.entities.Service
import com.example.vetclinic.domain.entities.User
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.user.UserInfo
import io.github.jan.supabase.auth.user.UserSession
import jakarta.inject.Inject
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import retrofit2.Response


class RepositoryImpl @Inject constructor(
    private val supabaseClient: SupabaseClient,
    private val supabaseApiService: SupabaseApiService,
    private val vetClinicDao: VetClinicDao,
    private val departmentMapper: DepartmentMapper,
    private val userMapper: UserMapper,
    private val petMapper: PetMapper,
    private val doctorMapper: DoctorMapper,
    private val serviceMapper: ServiceMapper
) : Repository {


    override suspend fun loginUser(email: String, password: String): Result<UserSession> {
        return try {

            val deferred = CompletableDeferred<UserSession>()

            Email.login(
                supabaseClient,
                onSuccess = { session ->
                    deferred.complete(session)
                }
            ) {
                this.email = email
                this.password = password
            }
            Result.success(deferred.await())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun registerUser(email: String, password: String): Result<UserSession> {
        return try {

            val deferred = CompletableDeferred<UserSession>()

            Email.signUp(
                supabaseClient,
                onSuccess = { session ->
                    Log.d(TAG, "registerUser onSuccess")
                    deferred.complete(session)
                }
            ) {
                this.email = email
                this.password = password
            }

            Result.success(withTimeout(10_000) { deferred.await() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    override suspend fun logOut() {
        supabaseClient.auth.signOut()
    }

    override fun getCurrentUser(): Result<UserInfo> {
        return supabaseClient.auth.currentUserOrNull()?.let { Result.success(it) }
            ?: Result.failure(Exception("No user found"))
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


    private suspend fun <T, R> addDataToSupabaseDb(
        entity: T,
        apiCall: suspend (R) -> Response<Unit>,
        mapper: (T) -> R
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
    }


    private suspend fun <T, R> fetchData(
        apiCall: suspend () -> Response<T>,
        mapper: (T) -> R,
        entityTag: String
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
        Log.e(TAG, "Error fetching $entityTag {e.message}")
    }


    override suspend fun checkUserSession(): Boolean {
        return try {
            return supabaseClient.auth.currentUserOrNull() != null
        } catch (e: Exception) {
            false
        }
    }


    override suspend fun addUserToRoom(user: User, pet: Pet) {
        vetClinicDao.insertUser(userMapper.userEntityToUserDbModel(user))
        vetClinicDao.insertPet(petMapper.petEntityToPetDbModel(pet))
    }

    override suspend fun getCurrentUserFromRoom(userId: String): Result<User> =
        runCatching {
            withContext(Dispatchers.IO) {
                val userDbModel = vetClinicDao.getUserById(userId)
                    ?: throw NoSuchElementException("User with ID $userId not found in Room")
                Log.d("RoomCheck", "User from Room: $userId")
                userMapper.userDbModelToUserEntity(userDbModel)
            }
        }.onFailure {
            Log.e(TAG, "Error while getting user from Room", it)
        }

    companion object {
        private const val TAG = "RepositoryImpl"
        private const val SERVICE_LIST_TAG = "services"
        private const val DOCTOR_LIST_TAG = "doctors"
        private const val DEPARTMENT_LIST_TAG = "departments"
    }
}









