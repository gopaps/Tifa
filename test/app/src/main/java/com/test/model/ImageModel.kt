package com.test.model

data class ImageModel(
    val id: Int,          // ID gambar
    val image_name: String, // Nama gambar
    val image_data: String  // Data gambar (base64 encoded)
)