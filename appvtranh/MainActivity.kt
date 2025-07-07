// MainActivity.kt
package com.example.appvtranh

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
//            val navController = rememberNavController()
//            NavHost(navController = navController, startDestination = "splash") {
//                composable("splash") { SplashScreenHome(navController) }
//                composable("home") { HomeScreen(navController) }
//                composable("login") { LoginScreen(navController) }
//                composable("register") { RegisterScreen(navController) }
//                composable("draw") { DrawingScreen() }
//            }
            DrawingScreen()
        }
    }
}

