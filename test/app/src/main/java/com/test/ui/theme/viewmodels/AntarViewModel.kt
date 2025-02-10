package com.test.ui.theme.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.test.model.AntarData
import com.test.model.Table
import com.test.network.AntarRepository
import com.test.network.DoorRepository
import com.test.network.DoorApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AntarViewModel : ViewModel() {
    private val repository = AntarRepository()
    private val doorApiService: DoorApiService = Retrofit.Builder()
        .baseUrl("http://192.168.56.1/tes_app/door_control.php") // Ganti dengan URL server yang benar
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(DoorApiService::class.java)
    private val doorRepository = DoorRepository(doorApiService)

    // State management
    private var _selectedTray by mutableStateOf<String?>(null)
    private var _selectedTable by mutableStateOf<String?>(null)
    private val _tables = MutableStateFlow<List<Table>>(emptyList())

    // Exposed properties
    val tables: StateFlow<List<Table>> = _tables.asStateFlow()
    val selectedTray: String? get() = _selectedTray
    val selectedTable: String? get() = _selectedTable
    val selectedItems = mutableStateListOf<AntarData>()

    // UI state
    var showAddTableDialog by mutableStateOf(false)
    var showConfirmationDialog by mutableStateOf(false)
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    companion object {
        const val MAX_ORDERS = 3
    }

    init {
        loadTables()
    }

    fun getOrderForTable(tableName: String): Int? {
        return selectedItems.find { it.tableNumber == tableName }?.order
    }

    fun setSelectedTray(tray: String?) {
        _selectedTray = tray
        _selectedTable = null // Reset table selection when tray changes
    }

    fun setSelectedTable(table: String?) {
        _selectedTable = table
    }

    fun addSelection() {
        viewModelScope.launch {
            try {
                _selectedTray?.let { tray ->
                    _selectedTable?.let { table ->
                        if (selectedItems.size >= MAX_ORDERS) {
                            errorMessage = "Maksimal $MAX_ORDERS pesanan"
                            return@launch
                        }

                        val coordinates = repository.getTableCoordinates(table)
                            ?: throw Exception("Koordinat tidak ditemukan untuk $table")

                        val order = selectedItems.size + 1

                        val existing = selectedItems.indexOfFirst { it.trayPosition == tray }
                        if (existing != -1) {
                            selectedItems[existing] = AntarData(order, tray, table, coordinates)
                        } else {
                            selectedItems.add(AntarData(order, tray, table, coordinates))
                        }

                        _selectedTray = null
                        _selectedTable = null
                    }
                }
            } catch (e: Exception) {
                errorMessage = e.message ?: "Gagal menambahkan pesanan"
            }
        }
    }

    private fun validateOrderSequence() {
        val orders = selectedItems.map { it.order }.sorted()
        val expected = (1..selectedItems.size).toList()
        if (orders != expected) {
            throw IllegalArgumentException("Urutan harus berkelanjutan 1,2,3")
        }
    }

    fun confirmSelection() {
        viewModelScope.launch {
            try {
                validateOrderSequence()
                isLoading = true
                val result = repository.sendOrderData(selectedItems.toList())
                result.onSuccess {
                    selectedItems.clear()
                    showConfirmationDialog = false
                }.onFailure { e ->
                    errorMessage = e.message ?: "Gagal mengirim data"
                }
            } catch (e: Exception) {
                errorMessage = when {
                    e.message?.contains("urut") == true -> "Urutan tidak valid! Pastikan urutan 1,2,3"
                    e.message?.contains("Koordinat") == true -> "Koordinat tidak ditemukan"
                    else -> "Error: ${e.message}"
                }
            } finally {
                isLoading = false
            }
        }
    }

    fun removeSelection(trayPosition: String) {
        selectedItems.removeAll { it.trayPosition == trayPosition }
    }

    fun showAddTableDialog() {
        showAddTableDialog = true
    }

    fun hideAddTableDialog() {
        showAddTableDialog = false
    }

    fun addTable(tableName: String) {
        viewModelScope.launch {
            isLoading = true
            val result = repository.addTableToKoor(tableName)
            isLoading = false

            result.onSuccess {
                loadTables()
                hideAddTableDialog()
            }.onFailure { e ->
                errorMessage = e.message ?: "Gagal menambahkan meja"
            }
        }
    }

    fun dismissError() {
        errorMessage = null
    }

    private fun loadTables() {
        viewModelScope.launch {
            isLoading = true
            _tables.value = repository.getTables()
            isLoading = false
        }
    }

    fun sendDoorCommand(position: Int) {
        viewModelScope.launch {
            try {
                val result = doorRepository.sendDoorCommand(position)
                if (!result.isSuccess) {
                    errorMessage = "Gagal mengirim perintah pintu"
                }
            } catch (e: Exception) {
                errorMessage = "Error: ${e.localizedMessage}"
            }
        }
    }
}
