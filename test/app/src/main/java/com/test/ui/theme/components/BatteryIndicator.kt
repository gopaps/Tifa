package com.test.ui.theme.components

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun BatteryIndicator(batteryLevel: Int) {
    Log.d("BatteryIndicator", "Rendering battery level: $batteryLevel")  // Log untuk debugging
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        // Ikon baterai
        Icon(
            imageVector = Icons.Default.BatteryFull,
            contentDescription = "Battery Icon",
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        // Teks level baterai
        Text(
            text = "$batteryLevel%",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}