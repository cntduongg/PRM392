package com.example.theflower.data.exceptions

import retrofit2.HttpException

/**
 * Base sealed class for API exceptions
 * Provides type-safe error handling throughout the app
 */
sealed class ApiException(message: String, cause: Throwable? = null) : Exception(message, cause) {
    
    // Network level errors
    data class NetworkError(val message: String? = null, val cause: Throwable? = null) : 
        ApiException("Network error: ${message ?: cause?.message ?: "Unknown"}", cause)
    
    // HTTP 400 - Bad Request / Validation errors
    data class ValidationError(val message: String? = null) : 
        ApiException("Validation error: $message")
    
    // HTTP 401 - Unauthorized
    data class Unauthorized(val message: String? = null) : 
        ApiException("Unauthorized: ${message ?: "Invalid credentials"}")
    
    // HTTP 403 - Forbidden
    data class Forbidden(val message: String? = null) : 
        ApiException("Forbidden: $message")
    
    // HTTP 404 - Not Found
    data class NotFound(val message: String? = null) : 
        ApiException("Not found: $message")
    
    // HTTP 409 - Conflict (e.g., duplicate email)
    data class ConflictError(val message: String? = null) : 
        ApiException("Conflict: $message")
    
    // HTTP 429 - Rate Limited
    data class RateLimitError(val retryAfter: Long = 0) : 
        ApiException("Rate limited. Retry after: $retryAfter seconds")
    
    // HTTP 500 - Server Error
    data class ServerError(val code: Int = 500, val message: String? = null) : 
        ApiException("Server error ($code): ${message ?: "Unknown"}")
    
    // Token expiration (commonly 401 but specific handling)
    data class TokenExpired(val message: String? = null) : 
        ApiException("Token expired: $message")
    
    // Generic HTTP error for unmapped status codes
    data class HttpError(val code: Int, val message: String? = null) : 
        ApiException("HTTP error ($code): $message")
    
    // Timeout errors
    data class TimeoutError(val message: String? = null) : 
        ApiException("Request timeout: ${message ?: "Connection timed out"}")
    
    // Local/Parsing errors
    data class ParsingError(val message: String? = null) : 
        ApiException("Failed to parse response: $message")
    
    companion object {
        /**
         * Convert HTTP exceptions to typed ApiException
         * Handles various HTTP status codes with appropriate exception types
         */
        fun handleException(exception: HttpException): ApiException {
            return when (exception.code()) {
                400 -> ValidationError(message = exception.message())
                401 -> Unauthorized(message = exception.message())
                403 -> Forbidden(message = exception.message())
                404 -> NotFound(message = exception.message())
                409 -> ConflictError(message = exception.message())
                429 -> RateLimitError()
                in 500..599 -> ServerError(code = exception.code(), message = exception.message())
                else -> HttpError(code = exception.code(), message = exception.message())
            }
        }
    }
}
