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
    private val apiService: TheFlowerApiService
) : IPaymentRepository {
    
    /**
     * Create new payment for order
     */
    override suspend fun createPayment(token: String, request: CreatePaymentRequest): Result<PaymentDto> {
        return try {
            val response = apiService.createPayment(token, request)
            
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
     * Get payment status by payment ID
     */
    override suspend fun getPaymentStatus(token: String, paymentId: Int): Result<PaymentDto> {
        return try {
            val response = apiService.getPaymentStatus(token, paymentId)
            
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(ApiException.NotFound("Payment not found"))
            }
        } catch (e: HttpException) {
            Result.failure(ApiException.handleException(e))
        } catch (e: Exception) {
            Result.failure(ApiException.NetworkError(errorCause = e))
        }
    }
}
