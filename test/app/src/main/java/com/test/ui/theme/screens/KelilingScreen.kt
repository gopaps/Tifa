package com.test.ui.theme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.test.ui.theme.components.BatteryIndicator
import com.test.ui.theme.components.BottomBar
import com.test.ui.theme.viewmodels.BatteryViewModel


@Composable
fun KelilingScreen(navController: NavController) {
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
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Pilih Mode:",
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(modifier = Modifier.height(32.dp))

                // Row untuk menempatkan tombol sejajar horizontal di tengah
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center, // Tombol di tengah
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Tombol Mode Maps dengan Ikon (Tombol Kiri)
                    Button(
                        onClick = { navController.navigate("maps") },
                        modifier = Modifier
                            .width(220.dp) // Lebar tombol
                            .height(80.dp) // Tinggi tombol
                            .padding(1.dp), // Padding kecil
                        shape = RoundedCornerShape(19.dp), // Sudut melengkung
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Black, // Warna latar belakang tombol hitam
                            contentColor = Color.White // Warna teks dan ikon putih
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Map, // Ikon untuk Mode Maps
                                contentDescription = "Maps",
                                modifier = Modifier.size(24.dp) // Ukuran ikon
                            )
                            Spacer(modifier = Modifier.width(8.dp)) // Jarak antara ikon dan teks
                            Text("Mode Maps")
                        }
                    }

                    Spacer(modifier = Modifier.width(36.dp)) // Jarak antara dua tombol

                    // Tombol Mode Keliling dengan Ikon (Tombol Kanan)
                    Button(
                        onClick = { navController.navigate("kelilingMode") },
                        modifier = Modifier
                            .width(220.dp) // Lebar tombol
                            .height(80.dp) // Tinggi tombol
                            .padding(1.dp), // Padding kecil
                        shape = RoundedCornerShape(19.dp), // Sudut melengkung
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Black, // Warna latar belakang tombol hitam
                            contentColor = Color.White // Warna teks dan ikon putih
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.DirectionsWalk, // Ikon untuk Mode Keliling
                                contentDescription = "Keliling",
                                modifier = Modifier.size(24.dp) // Ukuran ikon
                            )
                            Spacer(modifier = Modifier.width(8.dp)) // Jarak antara ikon dan teks
                            Text("Mode Keliling")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp)) // Jarak tambahan di bawah tombol
            }

        }
    )
}

@Preview(showBackground = true, device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
fun KelilingTabletPreview() {
    val navController = rememberNavController()
    KelilingScreen(navController = navController)
}