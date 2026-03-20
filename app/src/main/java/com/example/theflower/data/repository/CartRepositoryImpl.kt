package com.example.theflower.data.repository

import com.example.theflower.data.exceptions.ApiException
import com.example.theflower.data.remote.api.TheFlowerApiService
import com.example.theflower.data.remote.dtos.AddToCartRequest
import com.example.theflower.data.remote.dtos.CartDto
import com.example.theflower.data.remote.dtos.UpdateCartItemDto
import com.example.theflower.domain.repositories.ICartRepository
import retrofit2.HttpException

/**
 * Cart Repository Implementation
 * Handles all shopping cart operations with error handling
 */
class CartRepositoryImpl(
    private val apiService: TheFlowerApiService
) : ICartRepository {
    
    /**
     * Get current cart contents
     */
    override suspend fun getCart(token: String): Result<CartDto> {
        return try {
            val response = apiService.getCart(token)
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
     * Add product to cart
     */
    override suspend fun addToCart(token: String, request: AddToCartRequest): Result<CartDto> {
        return try {
            val response = apiService.addToCart(token, request)
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
    
    /**
     * Remove item from cart
     */
    override suspend fun removeFromCart(token: String, cartItemId: String): Result<CartDto> {
        return try {
            val response = apiService.removeFromCart(token, cartItemId)
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
    
    /**
     * Update quantity of cart item
     */
    override suspend fun updateCartItem(token: String, cartItemId: String, quantity: Int): Result<CartDto> {
        return try {
            val response = apiService.updateCartItem(token, cartItemId, UpdateCartItemDto(quantity))
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
    
    /**
     * Clear entire cart
     */
    override suspend fun clearCart(token: String): Result<Unit> {
        return try {
            val response = apiService.clearCart(token)
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
