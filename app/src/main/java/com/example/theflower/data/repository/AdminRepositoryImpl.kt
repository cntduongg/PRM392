package com.example.theflower.data.repository

import com.example.theflower.data.exceptions.ApiException
import com.example.theflower.data.remote.api.TheFlowerApiService
import com.example.theflower.data.remote.dtos.AdminUserDto
import com.example.theflower.data.remote.dtos.CreateAdminUserRequest
import com.example.theflower.data.remote.dtos.ProductUpsertRequest
import com.example.theflower.data.remote.dtos.UpdateProductRequest
import com.example.theflower.data.remote.dtos.UpdateAdminUserRequest
import com.example.theflower.domain.repositories.IAdminRepository
import retrofit2.HttpException

class AdminRepositoryImpl(
    private val apiService: TheFlowerApiService
) : IAdminRepository {

    override suspend fun getUsers(token: String): Result<List<AdminUserDto>> {
        return try {
            val response = apiService.getUsersAdmin(token)
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(ApiException.ServerError(500, response.message))
            }
        } catch (e: HttpException) {
            Result.failure(ApiException.handleException(e))
        } catch (e: Exception) {
            Result.failure(ApiException.NetworkError(errorCause = e))
        }
    }

    override suspend fun createUser(token: String, request: CreateAdminUserRequest): Result<AdminUserDto> {
        return try {
            val response = apiService.createUserAdmin(token, request)
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(ApiException.ValidationError(response.message))
            }
        } catch (e: HttpException) {
            Result.failure(ApiException.handleException(e))
        } catch (e: Exception) {
            Result.failure(ApiException.NetworkError(errorCause = e))
        }
    }

    override suspend fun updateUser(token: String, userId: String, request: UpdateAdminUserRequest): Result<AdminUserDto> {
        return try {
            val response = apiService.updateUserAdmin(token, userId, request)
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(ApiException.ValidationError(response.message))
            }
        } catch (e: HttpException) {
            Result.failure(ApiException.handleException(e))
        } catch (e: Exception) {
            Result.failure(ApiException.NetworkError(errorCause = e))
        }
    }

    override suspend fun deleteUser(token: String, userId: String): Result<Unit> {
        return try {
            val response = apiService.deleteUserAdmin(token, userId)
            if (response.success) {
                Result.success(Unit)
            } else {
                Result.failure(ApiException.ValidationError(response.message))
            }
        } catch (e: HttpException) {
            Result.failure(ApiException.handleException(e))
        } catch (e: Exception) {
            Result.failure(ApiException.NetworkError(errorCause = e))
        }
    }

    override suspend fun createProduct(token: String, request: ProductUpsertRequest): Result<Unit> {
        return try {
            val response = apiService.createProductAdmin(token, request)
            if (response.success) {
                Result.success(Unit)
            } else {
                Result.failure(ApiException.ValidationError(response.message))
            }
        } catch (e: HttpException) {
            Result.failure(ApiException.handleException(e))
        } catch (e: Exception) {
            Result.failure(ApiException.NetworkError(errorCause = e))
        }
    }

    override suspend fun updateProduct(token: String, productId: String, request: UpdateProductRequest): Result<Unit> {
        return try {
            val response = apiService.updateProductAdmin(token, productId, request)
            if (response.success) {
                Result.success(Unit)
            } else {
                Result.failure(ApiException.ValidationError(response.message))
            }
        } catch (e: HttpException) {
            Result.failure(ApiException.handleException(e))
        } catch (e: Exception) {
            Result.failure(ApiException.NetworkError(errorCause = e))
        }
    }

    override suspend fun deleteProduct(token: String, productId: String): Result<Unit> {
        return try {
            val response = apiService.deleteProductAdmin(token, productId)
            if (response.success) {
                Result.success(Unit)
            } else {
                Result.failure(ApiException.ValidationError(response.message))
            }
        } catch (e: HttpException) {
            Result.failure(ApiException.handleException(e))
        } catch (e: Exception) {
            Result.failure(ApiException.NetworkError(errorCause = e))
        }
    }
}
