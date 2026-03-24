package com.example.theflower.data.repository

import com.example.theflower.data.exceptions.ApiException
import com.example.theflower.data.remote.api.TheFlowerApiService
import com.example.theflower.data.remote.dtos.CreateOrderRequest
import com.example.theflower.data.remote.dtos.OrderDto
import com.example.theflower.data.remote.dtos.PaginatedResponse
import com.example.theflower.domain.repositories.IOrderRepository
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
    override suspend fun getOrders(pageNumber: Int, pageSize: Int): Result<PaginatedResponse<OrderDto>> {
        return try {
            val response = apiService.getOrders()
            val items = if (response.success) response.data.orEmpty() else emptyList()
            if (!response.success) {
                return Result.failure(ApiException.ServerError(500, response.message))
            }
            val fromIndex = ((pageNumber - 1) * pageSize).coerceAtLeast(0)
            val toIndex = (fromIndex + pageSize).coerceAtMost(items.size)
            val pageItems = if (fromIndex < toIndex) items.subList(fromIndex, toIndex) else emptyList()
            val paginatedResponse = PaginatedResponse(
                items = pageItems,
                pageNumber = pageNumber,
                pageSize = pageSize,
                totalItems = items.size,
                totalPages = if (items.isEmpty()) 0 else (items.size + pageSize - 1) / pageSize
            )
            Result.success(paginatedResponse)
        } catch (e: HttpException) {
            Result.failure(ApiException.handleException(e))
        } catch (e: Exception) {
            Result.failure(ApiException.NetworkError(errorCause = e))
        }
    }
    
    /**
     * Get order details by ID
     */
    override suspend fun getOrderDetail(orderId: String): Result<OrderDto> {
        return try {
            val response = apiService.getOrderDetail(orderId)
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
    override suspend fun createOrder(request: CreateOrderRequest): Result<OrderDto> {
        return try {
            val response = apiService.createOrder(request)
            if (response.success && response.data != null) {
                val mappedOrder = OrderDto(
                    id = response.data.orderId,
                    items = emptyList(),
                    totalPrice = response.data.totalAmount,
                    recipientAddress = request.billingAddress,
                    status = response.data.orderStatus,
                    paymentMethod = response.data.paymentMethod,
                    paymentUrl = response.data.paymentUrl,
                    createdAt = ""
                )
                Result.success(mappedOrder)
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
    override suspend fun cancelOrder(orderId: String): Result<OrderDto> {
        return Result.failure(ApiException.ValidationError("Order cancellation is not defined in the current swagger spec."))
    }
}
