package com.test.ui.theme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.test.ui.theme.components.BatteryIndicator
import com.test.ui.theme.components.BottomBar
import com.test.ui.theme.viewmodels.BatteryViewModel


@Composable
fun KelilingModeScreen(navController: NavController) {
    val batteryViewModel: BatteryViewModel = viewModel()  // Inisialisasi ViewModel
    val batteryLevel by batteryViewModel.batteryLevel.collectAsState()  // Ambil data baterai
    Scaffold(
        topBar = { BatteryIndicator(batteryLevel = batteryLevel) },
        bottomBar = { BottomBar(navController) },
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
                    text = "Ini adalah Halaman Mode Keliling",
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    )
}