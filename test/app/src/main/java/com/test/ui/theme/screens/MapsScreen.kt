package com.test.ui.theme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.test.model.ImageModel
import com.test.network.RetrofitInstance
import com.test.ui.theme.components.BatteryIndicator
import com.test.ui.theme.components.BottomBar
import com.test.ui.theme.viewmodels.BatteryViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Base64

@Composable
fun MapsScreen(navController: NavController) {
    val batteryViewModel: BatteryViewModel = viewModel()  // Inisialisasi ViewModel
    val batteryLevel by batteryViewModel.batteryLevel.collectAsState()  // Ambil data baterai
    Scaffold(
        topBar = { BatteryIndicator(batteryLevel = batteryLevel) },
        bottomBar = { BottomBar(navController) }, // Tambahkan BottomBar di sini
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Pilin Maps",
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                ImageList() // Tampilkan daftar gambar dari MySQL
            }
        }
    )
}

@Composable
fun ImageList(modifier: Modifier = Modifier) {
    val images = remember { mutableStateOf<List<ImageModel>>(emptyList()) }

    LaunchedEffect(Unit) {
        RetrofitInstance.api.getImages().enqueue(object : Callback<List<ImageModel>> {
            override fun onResponse(call: Call<List<ImageModel>>, response: Response<List<ImageModel>>) {
                if (response.isSuccessful) {
                    images.value = response.body() ?: emptyList()
                }
            }

            override fun onFailure(call: Call<List<ImageModel>>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    LazyColumn(modifier = modifier) {
        items(images.value) { image ->
            val imageBytes = Base64.getDecoder().decode(image.image_data)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                AsyncImage(
                    model = imageBytes,
                    contentDescription = image.image_name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = image.image_name, // Tampilkan nama gambar
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { /* Handle button click */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Tambah Maps ")
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}