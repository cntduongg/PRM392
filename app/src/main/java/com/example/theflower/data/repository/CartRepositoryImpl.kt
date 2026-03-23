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
    override suspend fun getCart(): Result<CartDto> {
        return try {
            val response = apiService.getCart()
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
    override suspend fun addToCart(request: AddToCartRequest): Result<CartDto> {
        return try {
            val response = apiService.addToCart(request)
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
    override suspend fun removeFromCart(cartItemId: String): Result<CartDto> {
        return try {
            val response = apiService.removeFromCart(cartItemId)
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
    override suspend fun updateCartItem(cartItemId: String, quantity: Int): Result<CartDto> {
        return try {
            val response = apiService.updateCartItem(cartItemId, UpdateCartItemDto(quantity))
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
    override suspend fun clearCart(): Result<Unit> {
        return try {
            val response = apiService.clearCart()
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
