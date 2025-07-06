package com.example.appvetranh

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = "splash") {
                composable("splash") { SplashScreenHome(navController) }
                composable("home") { HomeScreen(navController) }
                composable("login") { LoginScreen(navController) }
                composable("register") { RegisterScreen(navController) }
                composable("forgot_password") { ForgotPasswordScreen(navController) }
                composable("draw") { DrawingScreen(navController) }
                composable(
                    "gallery?path={path}",
                    arguments = listOf(navArgument("path") {
                        nullable = true
                        defaultValue = null
                    })
                ) {
                    val path = it.arguments?.getString("path")
                    GalleryScreen(navController = navController)
                }
           }
        }
    }
}
