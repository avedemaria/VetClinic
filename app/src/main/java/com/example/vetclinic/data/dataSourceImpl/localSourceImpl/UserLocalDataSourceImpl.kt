package com.example.vetclinic.data.dataSourceImpl.localSourceImpl

import com.example.vetclinic.data.DataSourceUtils
import com.example.vetclinic.data.localSource.database.VetClinicDao
import com.example.vetclinic.data.localSource.database.models.UserDbModel
import com.example.vetclinic.data.localSource.interfaces.UserLocalDataSource
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserLocalDataSourceImpl @Inject constructor(
    private val vetClinicDao: VetClinicDao,
) : UserLocalDataSource {

    override suspend fun updateUser(user: UserDbModel): Result<Unit> {
        return DataSourceUtils.executeRoomCall { vetClinicDao.updateUser(user) }
    }

    override suspend fun getCurrentUser(userId: String): Result<UserDbModel?> {
        return withContext(Dispatchers.IO) {
            DataSourceUtils.executeRoomCall { vetClinicDao.getUserById(userId) }
        }
    }

    override suspend fun addUser(user: UserDbModel): Result<Unit> {
        return DataSourceUtils.executeRoomCall { vetClinicDao.insertUser(user) }
    }
}