package com.example.theflower.data.exceptions

import retrofit2.HttpException

/**
 * Base sealed class for API exceptions
 * Provides type-safe error handling throughout the app
 */
sealed class ApiException(message: String, cause: Throwable? = null) : Exception(message, cause) {
    
    // Network level errors
    data class NetworkError(val errorMessage: String? = null, val errorCause: Throwable? = null) : 
        ApiException("Network error: ${errorMessage ?: errorCause?.message ?: "Unknown"}", errorCause)
    
    // HTTP 400 - Bad Request / Validation errors
    data class ValidationError(val errorMessage: String? = null) : 
        ApiException("Validation error: $errorMessage")
    
    // HTTP 401 - Unauthorized
    data class Unauthorized(val errorMessage: String? = null) : 
        ApiException("Unauthorized: ${errorMessage ?: "Invalid credentials"}")
    
    // HTTP 403 - Forbidden
    data class Forbidden(val errorMessage: String? = null) : 
        ApiException("Forbidden: $errorMessage")
    
    // HTTP 404 - Not Found
    data class NotFound(val errorMessage: String? = null) : 
        ApiException("Not found: $errorMessage")
    
    // HTTP 409 - Conflict (e.g., duplicate email)
    data class ConflictError(val errorMessage: String? = null) : 
        ApiException("Conflict: $errorMessage")
    
    // HTTP 429 - Rate Limited
    data class RateLimitError(val retryAfter: Long = 0) : 
        ApiException("Rate limited. Retry after: $retryAfter seconds")
    
    // HTTP 500 - Server Error
    data class ServerError(val code: Int = 500, val errorMessage: String? = null) : 
        ApiException("Server error ($code): ${errorMessage ?: "Unknown"}")
    
    // Token expiration (commonly 401 but specific handling)
    data class TokenExpired(val errorMessage: String? = null) : 
        ApiException("Token expired: $errorMessage")
    
    // Generic HTTP error for unmapped status codes
    data class HttpError(val code: Int, val errorMessage: String? = null) : 
        ApiException("HTTP error ($code): $errorMessage")
    
    // Timeout errors
    data class TimeoutError(val errorMessage: String? = null) : 
        ApiException("Request timeout: ${errorMessage ?: "Connection timed out"}")
    
    // Local/Parsing errors
    data class ParsingError(val errorMessage: String? = null) : 
        ApiException("Failed to parse response: $errorMessage")
    
    companion object {
        /**
         * Convert HTTP exceptions to typed ApiException
         * Handles various HTTP status codes with appropriate exception types
         */
        fun handleException(exception: HttpException): ApiException {
            return when (exception.code()) {
                400 -> ValidationError(exception.message())
                401 -> Unauthorized(exception.message())
                403 -> Forbidden(exception.message())
                404 -> NotFound(exception.message())
                409 -> ConflictError(exception.message())
                429 -> RateLimitError()
                in 500..599 -> ServerError(exception.code(), exception.message())
                else -> HttpError(exception.code(), exception.message())
            }
        }
    }
}
