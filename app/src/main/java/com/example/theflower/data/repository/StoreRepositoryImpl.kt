package com.example.theflower.data.repository

import com.example.theflower.data.exceptions.ApiException
import com.example.theflower.data.remote.api.TheFlowerApiService
import com.example.theflower.data.remote.dtos.StoreLocationDto
import com.example.theflower.domain.repositories.IStoreRepository
import retrofit2.HttpException

class StoreRepositoryImpl(
    private val apiService: TheFlowerApiService
) : IStoreRepository {

    override suspend fun getStores(): Result<List<StoreLocationDto>> {
        return try {
            val response = apiService.getStores()
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

    override suspend fun getStoreDetail(locationId: String): Result<StoreLocationDto> {
        return try {
            val response = apiService.getStoreDetail(locationId)
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
