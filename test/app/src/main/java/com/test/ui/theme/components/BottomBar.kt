package com.test.ui.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import android.content.Context
import android.media.AudioManager
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.filled.Undo
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow

@Composable
fun BottomBar(navController: NavController) {
    // Wrapper untuk membuat efek melayang
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp) // Memberikan margin di sekitar bottom bar
            .background(Color.Transparent) // Latar belakang transparan agar terlihat melayang
    ) {
        // Bottom Bar dengan shadow
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .align(Alignment.BottomCenter)
                .background(
                    color = Color.Black,
                    shape = CircleShape.copy(all = CornerSize(40.dp)) // Membuat sudut melengkung
                )
                .shadow(
                    elevation = 10.dp, // Tinggi shadow
                    shape = CircleShape, // Bentuk shadow (lingkaran)
                    clip = true // Memastikan elemen tetap berbentuk lingkaran
                ) // Memberikan efek bayangan
        ) {
            // Tiga tombol di tengah
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
                    .padding(horizontal = 32.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tombol Kembali
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .size(60.dp)
                        .background(Color.Black, CircleShape)
                        .clip(CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Undo,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                // Tombol Home
                IconButton(
                    onClick = { navController.navigate("home") },
                    modifier = Modifier
                        .size(60.dp)
                        .background(Color.Black, CircleShape)
                        .clip(CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Home",
                        tint = Color.White
                    )
                }

                // Tombol Volume
                val context = LocalContext.current
                IconButton(
                    onClick = {
                        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
                        audioManager.adjustStreamVolume(
                            AudioManager.STREAM_MUSIC,
                            AudioManager.ADJUST_RAISE,
                            AudioManager.FLAG_SHOW_UI
                        )
                    },
                    modifier = Modifier
                        .size(60.dp)
                        .background(Color.Black, CircleShape)
                        .clip(CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "Volume",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
fun PreviewBottomBar() {
    BottomBar(navController = rememberNavController())
}