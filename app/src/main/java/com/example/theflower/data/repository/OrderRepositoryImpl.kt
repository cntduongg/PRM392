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
            val items = apiService.getOrdersBackend(token)
            val fromIndex = ((pageNumber - 1) * pageSize).coerceAtLeast(0)
            val toIndex = (fromIndex + pageSize).coerceAtMost(items.size)
            val pageItems = if (fromIndex < toIndex) items.subList(fromIndex, toIndex) else emptyList()
            val response = PaginatedResponse(
                items = pageItems,
                pageNumber = pageNumber,
                pageSize = pageSize,
                totalItems = items.size,
                totalPages = if (items.isEmpty()) 0 else (items.size + pageSize - 1) / pageSize
            )
            Result.success(response)
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
            val response = apiService.getOrderDetailBackend(token, orderId)
            Result.success(response)
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
            val response = apiService.createOrderBackend(token, request)
            val mappedOrder = OrderDto(
                id = response.orderId,
                items = emptyList(),
                totalPrice = response.totalAmount,
                recipientAddress = request.billingAddress,
                status = response.orderStatus,
                paymentMethod = response.paymentMethod,
                createdAt = ""
            )
            Result.success(mappedOrder)
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
