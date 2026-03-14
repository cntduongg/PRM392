package com.example.theflower.data.repository

import com.example.theflower.domain.repositories.IPaymentRepository
import com.example.theflower.data.remote.api.TheFlowerApiService
import com.example.theflower.data.remote.dtos.*
import com.example.theflower.data.exceptions.ApiException
import retrofit2.HttpException

/**
 * Payment Repository Implementation
 * Handles payment processing operations
 */
class PaymentRepositoryImpl(
    private val apiService: TheFlowerApiService,
    private val token: String
) : IPaymentRepository {
    
    private val authHeader get() = token
    
    /**
     * Create new payment for order
     */
    override suspend fun createPayment(
        orderId: Int,
        amount: Int,
        paymentMethod: String
    ): Result<PaymentDto> {
        return try {
            val request = CreatePaymentRequest(
                orderId = orderId,
                amount = amount,
                paymentMethod = paymentMethod
            )
            val response = apiService.createPayment(authHeader, request)
            
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
     * Get payment status by payment ID
     */
    override suspend fun getPaymentStatus(paymentId: Int): Result<PaymentDto> {
        return try {
            val response = apiService.getPaymentStatus(authHeader, paymentId)
            
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(ApiException.NotFound(message = "Payment not found"))
            }
        } catch (e: HttpException) {
            Result.failure(ApiException.handleException(e))
        } catch (e: Exception) {
            Result.failure(ApiException.NetworkError(cause = e))
        }
    }
}
