package com.test.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface DoorApiService {
    @POST("door_control.php") // Sesuaikan dengan endpoint di server
    suspend fun updateDoor(@Body request: DoorCommandRequest): Response<ApiResponse>
}

// Model untuk request dan response API
data class DoorCommandRequest(val command: Int)
data class ApiResponse(val message: String?, val error: String?)
