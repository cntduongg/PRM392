package com.example.theflower.data.repository

import com.example.theflower.data.exceptions.ApiException
import com.example.theflower.data.remote.api.TheFlowerApiService
import com.example.theflower.data.remote.dtos.*
import com.example.theflower.domain.repositories.IAdminRepository
import retrofit2.HttpException

class AdminRepositoryImpl(
    private val apiService: TheFlowerApiService
) : IAdminRepository {

    override suspend fun getUsers(): Result<List<AdminUserDto>> {
        return try {
            val response = apiService.getUsersAdmin()
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

    override suspend fun createUser(request: CreateAdminUserRequest): Result<AdminUserDto> {
        return try {
            val response = apiService.createUserAdmin(request)
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

    override suspend fun updateUser(userId: String, request: UpdateAdminUserRequest): Result<AdminUserDto> {
        return try {
            val response = apiService.updateUserAdmin(userId, request)
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

    override suspend fun deleteUser(userId: String): Result<Unit> {
        return try {
            val response = apiService.deleteUserAdmin(userId)
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

    override suspend fun createProduct(request: ProductUpsertRequest): Result<Unit> {
        return try {
            val response = apiService.createProductAdmin(request)
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

    override suspend fun updateProduct(productId: String, request: UpdateProductRequest): Result<Unit> {
        return try {
            val response = apiService.updateProductAdmin(productId, request)
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

    override suspend fun deleteProduct(productId: String): Result<Unit> {
        return try {
            val response = apiService.deleteProductAdmin(productId)
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

    override suspend fun createCategory(request: CategoryUpsertRequest): Result<CategoryDto> {
        return try {
            val response = apiService.createCategoryAdmin(request)
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

    override suspend fun updateCategory(categoryId: String, request: CategoryUpsertRequest): Result<Unit> {
        return try {
            val response = apiService.updateCategoryAdmin(categoryId, request)
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

    override suspend fun deleteCategory(categoryId: String): Result<Unit> {
        return try {
            val response = apiService.deleteCategoryAdmin(categoryId)
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

    override suspend fun updateOrderStatus(orderId: String, status: String): Result<Unit> {
        return try {
            val response = apiService.updateOrderStatusAdmin(orderId, UpdateOrderStatusRequest(status))
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

    override suspend fun getOrders(): Result<List<com.example.theflower.data.remote.dtos.OrderDto>> {
        return try {
            val response = apiService.getOrdersAdmin()
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

    override suspend fun getDashboardStats(): Result<com.example.theflower.data.remote.dtos.DashboardStatsDto> {
        return try {
            val response = apiService.getDashboardStatsAdmin()
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
}
