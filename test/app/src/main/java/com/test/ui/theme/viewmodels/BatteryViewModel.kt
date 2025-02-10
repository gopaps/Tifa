package com.test.ui.theme.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import android.util.Log

class BatteryViewModel : ViewModel() {
    private val _batteryLevel = MutableStateFlow(0)  // State untuk level baterai
    val batteryLevel: StateFlow<Int> get() = _batteryLevel

    // Interface untuk API
    interface ApiService {
        @GET("get_battery_level.php")  // Sesuaikan dengan endpoint API Anda
        suspend fun getBatteryLevel(): BatteryData
    }

    // Data class untuk response API
    data class BatteryData(
        val battery_level: Int
    )

    // Inisialisasi Retrofit
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.56.1/tes_app/")  // Ganti dengan URL server Anda
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService = retrofit.create(ApiService::class.java)

    init {
        // Mulai polling data dari API setiap 5 detik
        viewModelScope.launch {
            while (true) {
                fetchBatteryLevel()
                delay(5000)
            }
        }
    }

    // Fungsi untuk mengambil data dari API
    private suspend fun fetchBatteryLevel() {
        try {
            Log.d("BatteryViewModel", "Fetching battery level from: http://192.168.56.1/tes_app/get_battery_level.php")
            val batteryData = withContext(Dispatchers.IO) {
                apiService.getBatteryLevel()
            }
            Log.d("BatteryViewModel", "Battery level received: ${batteryData.battery_level}")
            _batteryLevel.value = batteryData.battery_level
        } catch (e: Exception) {
            Log.e("BatteryViewModel", "Error fetching battery level", e)
        }
    }
}