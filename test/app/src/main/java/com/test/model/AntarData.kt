package com.test.model

/**
 * Data class untuk menyimpan informasi tentang tray dan nomor meja yang dipilih.
 * @property trayPosition Posisi tray: "Atas", "Tengah", atau "Bawah".
 * @property tableNumber Nomor meja yang dipilih, misalnya "Meja 1", "Meja 2", dst.
 */
data class AntarData(
    val order: Int,
    val trayPosition: String,
    val tableNumber: String,
    val coordinates: String
)

/**
 * Data class untuk menyimpan informasi tentang setiap meja.
 * @property id ID unik untuk setiap meja (bisa berupa angka atau string).
 * @property name Nama meja, misalnya "Meja 1", "Meja 2", dst.
 */
data class Table(
    val id: Int,
    val name: String,
    val coordinates: String
)