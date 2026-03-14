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
    private val apiService: TheFlowerApiService
) : IOrderRepository {
    
    /**
     * Get paginated list of user's orders
     */
    override suspend fun getOrders(token: String, pageNumber: Int, pageSize: Int): Result<PaginatedResponse<OrderDto>> {
        return try {
            val response = apiService.getOrders(token, pageNumber, pageSize)
            
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
     * Get order details by ID
     */
    override suspend fun getOrderDetail(token: String, orderId: Int): Result<OrderDto> {
        return try {
            val response = apiService.getOrderDetail(token, orderId)
            
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(ApiException.NotFound("Order not found"))
            }
        } catch (e: HttpException) {
            Result.failure(ApiException.handleException(e))
        } catch (e: Exception) {
            Result.failure(ApiException.NetworkError(errorCause = e))
        }
    }
    
    /**
     * Create new order from cart
     */
    override suspend fun createOrder(token: String, request: CreateOrderRequest): Result<OrderDto> {
        return try {
            val response = apiService.createOrder(token, request)
            
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
     * Cancel existing order
     */
    override suspend fun cancelOrder(token: String, orderId: Int): Result<OrderDto> {
        return try {
            val response = apiService.cancelOrder(token, orderId)
            
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
