package com.test.ui.theme.helpers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File
import java.io.FileOutputStream

fun loadImagesFromStorage(context: Context): List<String> {
    val directory = File(context.filesDir, "carousel_images")
    if (!directory.exists()) {
        directory.mkdirs()
    }
    return directory.listFiles()?.map { it.absolutePath } ?: emptyList()
}

fun addImageToStorage(context: Context): String? {
    val directory = File(context.filesDir, "carousel_images")
    if (!directory.exists()) {
        directory.mkdirs()
    }
    val file = File(directory, "image_${System.currentTimeMillis()}.jpg")
    try {
        // Simpan gambar dummy (ganti dengan logika unggah gambar sesuai kebutuhan)
        val bitmap = BitmapFactory.decodeResource(context.resources, android.R.drawable.ic_menu_gallery)
        val outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.close()
        return file.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }
}

fun deleteImageFromStorage(filePath: String): Boolean {
    val file = File(filePath)
    return file.exists() && file.delete()
}