package com.example.theflower.data.repository

import com.example.theflower.domain.repositories.IProductRepository
import com.example.theflower.data.remote.api.TheFlowerApiService
import com.example.theflower.data.remote.dtos.*
import com.example.theflower.data.exceptions.ApiException
import retrofit2.HttpException

/**
 * Product Repository Implementation
 * Handles all product-related API calls with proper error handling
 */
class ProductRepositoryImpl(
    private val apiService: TheFlowerApiService
) : IProductRepository {
    
    /**
     * Get paginated products with optional filtering
     */
    override suspend fun getProducts(
        pageNumber: Int,
        pageSize: Int,
        categoryId: Int?,
        search: String?
    ): Result<PaginatedResponse<ProductDto>> {
        return try {
            val response = apiService.getProducts(
                pageNumber = pageNumber,
                pageSize = pageSize,
                categoryId = categoryId,
                search = search
            )
            
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
    
    /**
     * Get product details by ID
     */
    override suspend fun getProductDetail(productId: Int): Result<ProductDto> {
        return try {
            val response = apiService.getProductDetail(productId)
            
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(ApiException.NotFound("Product not found"))
            }
        } catch (e: HttpException) {
            Result.failure(ApiException.handleException(e))
        } catch (e: Exception) {
            Result.failure(ApiException.NetworkError(errorCause = e))
        }
    }
}
