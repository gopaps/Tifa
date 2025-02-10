package com.test.ui.theme.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.test.ui.theme.screens.*

@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "home") {
        composable("home") { HomeScreen(navController) }
        composable("antar") { AntarScreen(navController) }
        composable("jaga") { JagaScreen(navController) }
        composable("keliling") { KelilingScreen(navController) }
        composable("maps") { MapsScreen(navController) } // Rute untuk MapsScreen
        composable("kelilingMode") { KelilingModeScreen(navController) } // Rute untuk KelilingModeScreen
        composable("ai") { AIScreen(navController) }
    }
}