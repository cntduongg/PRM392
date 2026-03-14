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
            val response = apiService.getCategories()
            
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
