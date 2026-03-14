package com.example.theflower.data.repository

import com.example.theflower.domain.repositories.ICartRepository
import com.example.theflower.data.remote.api.TheFlowerApiService
import com.example.theflower.data.remote.dtos.*
import com.example.theflower.data.exceptions.ApiException
import retrofit2.HttpException

/**
 * Cart Repository Implementation
 * Handles all shopping cart operations with error handling
 */
class CartRepositoryImpl(
    private val apiService: TheFlowerApiService,
    private val token: String
) : ICartRepository {
    
    private val authHeader get() = token
    
    /**
     * Get current cart contents
     */
    override suspend fun getCart(): Result<CartDto> {
        return try {
            val response = apiService.getCart(authHeader)
            
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(ApiException.ServerError(message = response.message))
            }
        } catch (e: HttpException) {
            Result.failure(ApiException.handleException(e))
        } catch (e: Exception) {
            Result.failure(ApiException.NetworkError(cause = e))
        }
    }
    
    /**
     * Add product to cart
     */
    override suspend fun addToCart(productId: Int, quantity: Int): Result<CartDto> {
        return try {
            val request = AddToCartRequest(productId = productId, quantity = quantity)
            val response = apiService.addToCart(authHeader, request)
            
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(ApiException.ValidationError(message = response.message))
            }
        } catch (e: HttpException) {
            Result.failure(ApiException.handleException(e))
        } catch (e: Exception) {
            Result.failure(ApiException.NetworkError(cause = e))
        }
    }
    
    /**
     * Remove item from cart
     */
    override suspend fun removeFromCart(itemId: Int): Result<CartDto> {
        return try {
            val response = apiService.removeFromCart(authHeader, itemId)
            
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(ApiException.ServerError(message = response.message))
            }
        } catch (e: HttpException) {
            Result.failure(ApiException.handleException(e))
        } catch (e: Exception) {
            Result.failure(ApiException.NetworkError(cause = e))
        }
    }
    
    /**
     * Update quantity of cart item
     */
    override suspend fun updateCartItem(itemId: Int, quantity: Int): Result<CartDto> {
        return try {
            val quantityMap = mapOf("quantity" to quantity)
            val response = apiService.updateCartItem(authHeader, itemId, quantityMap)
            
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(ApiException.ServerError(message = response.message))
            }
        } catch (e: HttpException) {
            Result.failure(ApiException.handleException(e))
        } catch (e: Exception) {
            Result.failure(ApiException.NetworkError(cause = e))
        }
    }
    
    /**
     * Clear entire cart
     */
    override suspend fun clearCart(): Result<Unit> {
        return try {
            val response = apiService.clearCart(authHeader)
            
            if (response.success) {
                Result.success(Unit)
            } else {
                Result.failure(ApiException.ServerError(message = response.message))
            }
        } catch (e: HttpException) {
            Result.failure(ApiException.handleException(e))
        } catch (e: Exception) {
            Result.failure(ApiException.NetworkError(cause = e))
        }
    }
}
