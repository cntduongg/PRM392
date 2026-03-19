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
    private val apiService: TheFlowerApiService
) : ICartRepository {
    
    /**
     * Get current cart contents
     */
    override suspend fun getCart(token: String): Result<CartDto> {
        return try {
            val response = apiService.getCartBackend(token)
            Result.success(response)
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
            val response = apiService.addToCartBackend(token, request)
            Result.success(response)
        } catch (e: HttpException) {
            Result.failure(ApiException.handleException(e))
        } catch (e: Exception) {
            Result.failure(ApiException.NetworkError(errorCause = e))
        }
    }
    
    /**
     * Remove item from cart
     */
    override suspend fun removeFromCart(token: String, itemId: Int): Result<CartDto> {
        return try {
            val response = apiService.removeFromCartBackend(token, itemId)
            Result.success(response)
        } catch (e: HttpException) {
            Result.failure(ApiException.handleException(e))
        } catch (e: Exception) {
            Result.failure(ApiException.NetworkError(errorCause = e))
        }
    }
    
    /**
     * Update quantity of cart item
     */
    override suspend fun updateCartItem(token: String, itemId: Int, quantity: Int): Result<CartDto> {
        return try {
            val quantityMap = mapOf("quantity" to quantity)
            val response = apiService.updateCartItemBackend(token, itemId, quantityMap)
            Result.success(response)
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
            apiService.clearCartBackend(token)
            Result.success(Unit)
        } catch (e: HttpException) {
            Result.failure(ApiException.handleException(e))
        } catch (e: Exception) {
            Result.failure(ApiException.NetworkError(errorCause = e))
        }
    }
}
