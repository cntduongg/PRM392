package com.example.theflower.data.repository

import com.example.theflower.data.exceptions.ApiException
import com.example.theflower.data.remote.api.TheFlowerApiService
import com.example.theflower.data.remote.dtos.CreatePaymentRequest
import com.example.theflower.data.remote.dtos.PaymentDto
import com.example.theflower.domain.repositories.IPaymentRepository

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
        return Result.failure(
            ApiException.ValidationError("Payment endpoints are not defined in the current swagger spec.")
        )
    }
    
    /**
     * Get payment status by payment ID
     */
    override suspend fun getPaymentStatus(token: String, paymentId: String): Result<PaymentDto> {
        return Result.failure(
            ApiException.ValidationError("Payment endpoints are not defined in the current swagger spec.")
        )
    }
}
