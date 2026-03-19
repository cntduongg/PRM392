package com.example.theflower.data.repository

import com.example.theflower.data.exceptions.ApiException
import com.example.theflower.data.remote.api.TheFlowerApiService
import com.example.theflower.data.remote.dtos.AdminUserDto
import com.example.theflower.data.remote.dtos.CreateAdminUserRequest
import com.example.theflower.data.remote.dtos.ProductDto
import com.example.theflower.data.remote.dtos.ProductUpsertRequest
import com.example.theflower.data.remote.dtos.UpdateAdminUserRequest
import com.example.theflower.domain.repositories.IAdminRepository
import retrofit2.HttpException

class AdminRepositoryImpl(
    private val apiService: TheFlowerApiService
) : IAdminRepository {

    override suspend fun getUsers(token: String): Result<List<AdminUserDto>> {
        return try {
            Result.success(apiService.getUsersAdmin(token))
        } catch (e: HttpException) {
            Result.failure(ApiException.handleException(e))
        } catch (e: Exception) {
            Result.failure(ApiException.NetworkError(errorCause = e))
        }
    }

    override suspend fun createUser(token: String, request: CreateAdminUserRequest): Result<AdminUserDto> {
        return try {
            Result.success(apiService.createUserAdmin(token, request))
        } catch (e: HttpException) {
            Result.failure(ApiException.handleException(e))
        } catch (e: Exception) {
            Result.failure(ApiException.NetworkError(errorCause = e))
        }
    }

    override suspend fun updateUser(token: String, userId: Int, request: UpdateAdminUserRequest): Result<AdminUserDto> {
        return try {
            Result.success(apiService.updateUserAdmin(token, userId, request))
        } catch (e: HttpException) {
            Result.failure(ApiException.handleException(e))
        } catch (e: Exception) {
            Result.failure(ApiException.NetworkError(errorCause = e))
        }
    }

    override suspend fun deleteUser(token: String, userId: Int): Result<Unit> {
        return try {
            apiService.deleteUserAdmin(token, userId)
            Result.success(Unit)
        } catch (e: HttpException) {
            Result.failure(ApiException.handleException(e))
        } catch (e: Exception) {
            Result.failure(ApiException.NetworkError(errorCause = e))
        }
    }

    override suspend fun createProduct(token: String, request: ProductUpsertRequest): Result<ProductDto> {
        return try {
            Result.success(apiService.createProductAdmin(token, request))
        } catch (e: HttpException) {
            Result.failure(ApiException.handleException(e))
        } catch (e: Exception) {
            Result.failure(ApiException.NetworkError(errorCause = e))
        }
    }

    override suspend fun updateProduct(token: String, productId: Int, request: ProductUpsertRequest): Result<ProductDto> {
        return try {
            Result.success(apiService.updateProductAdmin(token, productId, request))
        } catch (e: HttpException) {
            Result.failure(ApiException.handleException(e))
        } catch (e: Exception) {
            Result.failure(ApiException.NetworkError(errorCause = e))
        }
    }

    override suspend fun deleteProduct(token: String, productId: Int): Result<Unit> {
        return try {
            apiService.deleteProductAdmin(token, productId)
            Result.success(Unit)
        } catch (e: HttpException) {
            Result.failure(ApiException.handleException(e))
        } catch (e: Exception) {
            Result.failure(ApiException.NetworkError(errorCause = e))
        }
    }
}
