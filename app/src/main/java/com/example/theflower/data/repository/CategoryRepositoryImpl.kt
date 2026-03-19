package com.example.theflower.data.repository

import com.example.theflower.domain.repositories.ICategoryRepository
import com.example.theflower.data.remote.api.TheFlowerApiService
import com.example.theflower.data.remote.dtos.CategoryDto
import com.example.theflower.data.exceptions.ApiException
import retrofit2.HttpException

/**
 * Category Repository Implementation
 * Handles fetching product categories
 */
class CategoryRepositoryImpl(
    private val apiService: TheFlowerApiService
) : ICategoryRepository {
    
    /**
     * Get all product categories
     */
    override suspend fun getCategories(): Result<List<CategoryDto>> {
        return try {
            Result.success(apiService.getCategoriesBackend())
        } catch (e: HttpException) {
            // Fallback for deployments that still return ApiResponse<List<CategoryDto>> on /api/categories.
            try {
                val response = apiService.getCategories()
                if (response.success && response.data != null) {
                    Result.success(response.data)
                } else {
                    Result.failure(ApiException.ServerError(500, response.message))
                }
            } catch (fallbackError: HttpException) {
                Result.failure(ApiException.handleException(fallbackError))
            } catch (fallbackError: Exception) {
                Result.failure(ApiException.NetworkError(errorCause = fallbackError))
            }
        } catch (e: Exception) {
            Result.failure(ApiException.NetworkError(errorCause = e))
        }
    }
}
