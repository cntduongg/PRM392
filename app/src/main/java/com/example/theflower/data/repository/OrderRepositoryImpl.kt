package com.example.theflower.data.repository

import com.example.theflower.domain.repositories.IOrderRepository
import com.example.theflower.data.remote.api.TheFlowerApiService
import com.example.theflower.data.remote.dtos.*
import com.example.theflower.data.exceptions.ApiException
import retrofit2.HttpException

/**
 * Order Repository Implementation
 * Handles all order management operations
 */
class OrderRepositoryImpl(
    private val apiService: TheFlowerApiService,
    private val token: String
) : IOrderRepository {
    
    private val authHeader get() = token
    
    /**
     * Get paginated list of user's orders
     */
    override suspend fun getOrders(pageNumber: Int, pageSize: Int): Result<PaginatedResponse<OrderDto>> {
        return try {
            val response = apiService.getOrders(authHeader, pageNumber, pageSize)
            
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
     * Get order details by ID
     */
    override suspend fun getOrderDetail(orderId: Int): Result<OrderDto> {
        return try {
            val response = apiService.getOrderDetail(authHeader, orderId)
            
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(ApiException.NotFound(message = "Order not found"))
            }
        } catch (e: HttpException) {
            Result.failure(ApiException.handleException(e))
        } catch (e: Exception) {
            Result.failure(ApiException.NetworkError(cause = e))
        }
    }
    
    /**
     * Create new order from cart
     */
    override suspend fun createOrder(
        items: List<CartItemDto>,
        recipientName: String,
        recipientPhone: String,
        recipientAddress: String,
        occasion: String?,
        message: String?,
        paymentMethod: String
    ): Result<OrderDto> {
        return try {
            val request = CreateOrderRequest(
                items = items,
                recipientName = recipientName,
                recipientPhone = recipientPhone,
                recipientAddress = recipientAddress,
                occasion = occasion,
                message = message,
                paymentMethod = paymentMethod
            )
            val response = apiService.createOrder(authHeader, request)
            
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
     * Cancel existing order
     */
    override suspend fun cancelOrder(orderId: Int): Result<OrderDto> {
        return try {
            val response = apiService.cancelOrder(authHeader, orderId)
            
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
}
