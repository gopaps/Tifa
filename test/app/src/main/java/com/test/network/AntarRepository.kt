package com.test.network

import com.test.model.AntarData
import com.test.model.Table
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.http.Body
import retrofit2.http.POST
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.concurrent.atomic.AtomicInteger

class AntarRepository(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    private val idCounter = AtomicInteger(8)
    private val tables = mutableListOf<Table>().apply {
        addAll((1..8).map { Table(it, "Meja $it", "0.0;0.0;0.0;") }) // Default coordinates
    }

    // Base URL untuk API PHP
        private val baseUrl = "http://192.168.56.1" // Ganti dengan URL server PHP Anda

    /**
     * Mengambil daftar meja dari database lokal (simulasi)
     */
    suspend fun getTables(): List<Table> = withContext(ioDispatcher) {
        try {
            // Simulasi network call
            delay(500)
            tables.toList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Mengambil koordinat dari server PHP berdasarkan nomor meja
     */
    suspend fun getTableCoordinates(tableName: String): String? = withContext(ioDispatcher) {
        try {
            // Encode nomor meja untuk URL
            val encodedTableName = URLEncoder.encode(tableName, "UTF-8")
            val url = URL("http://192.168.56.1/tes_app/get_coordinates.php?table_number=$encodedTableName")

            // Buka koneksi HTTP
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 5000 // Timeout 5 detik
            connection.readTimeout = 5000

            // Cek response code
            if (connection.responseCode == 200) {
                // Baca response
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                val json = JSONObject(response)

                // Jika ada koordinat, kembalikan
                if (json.has("coordinates")) {
                    json.getString("coordinates")
                } else {
                    throw Exception(json.optString("error", "Koordinat tidak ditemukan"))
                }
            } else {
                throw Exception("HTTP error: ${connection.responseCode}")
            }
        } catch (e: Exception) {
            throw Exception("Gagal mengambil koordinat: ${e.message}")
        }
    }

    /**
     * Mengirim data pesanan ke server PHP
     */
    suspend fun sendOrderData(data: List<AntarData>): Result<Unit> = withContext(ioDispatcher) {
        try {
            // Kirim setiap pesanan satu per satu
            data.forEach { item ->
                val url = URL("http://192.168.56.1/tes_app/insert_order.php")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.doOutput = true
                connection.setRequestProperty("Content-Type", "application/json")
                connection.connectTimeout = 5000
                connection.readTimeout = 5000

                // Buat payload JSON
                val jsonInput = JSONObject().apply {
                    put("order", item.order)
                    put("tray", item.trayPosition)
                    put("table", item.tableNumber)
                    put("coordinates", item.coordinates)
                }

                // Kirim data
                connection.outputStream.write(jsonInput.toString().toByteArray())

                // Cek response
                if (connection.responseCode != 200) {
                    throw Exception("Gagal menyimpan pesanan ${item.order}")
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Menambahkan meja baru ke database lokal (simulasi)
     */
    suspend fun addTableToKoor(tableName: String): Result<Unit> = withContext(ioDispatcher) {
        try {
            if (tableName.isBlank()) throw IllegalArgumentException("Nama meja tidak boleh kosong")

            // Tambahkan meja baru dengan koordinat default
            val newTable = Table(
                id = idCounter.incrementAndGet(),
                name = tableName.trim(),
                coordinates = "0.0;0.0;0.0;" // Default coordinates
            )
            tables.add(newTable)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Fungsi bantuan untuk validasi format koordinat
     */
    private fun isCoordinateValid(coordinates: String): Boolean {
        val pattern = Regex("""^\d+\.\d+;\d+\.\d+;\d+\.\d+;$""")
        return pattern.matches(coordinates)
    }
}

