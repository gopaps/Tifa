package com.test.network

import com.test.model.ImageModel
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("get_images.php") // Endpoint untuk mengambil data gambar
    fun getImages(): Call<List<ImageModel>>
}
