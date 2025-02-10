package com.test.network

class DoorRepository(private val doorApiService: DoorApiService) {
    suspend fun sendDoorCommand(position: Int): Result<Unit> {
        return try {
            val response = doorApiService.updateDoor(DoorCommandRequest(position))
            if (response.isSuccessful && response.body()?.error == null) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.body()?.error ?: "Server error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}