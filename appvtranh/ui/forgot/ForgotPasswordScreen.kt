package com.example.appvtranh.ui.forgot

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ForgotPasswordScreen(navController: NavController) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    var email by remember { mutableStateOf("") }
    var success by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Khôi phục mật khẩu", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (email.isNotBlank()) {
                    auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                success = "Đã gửi email đặt lại mật khẩu"
                                error = ""
                            } else {
                                success = ""
                                error = "Lỗi: ${task.exception?.message}"
                            }
                        }
                } else {
                    error = "Vui lòng nhập email"
                    success = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Gửi link khôi phục")
        }

        if (success.isNotEmpty()) {
            Text(success, color = Color.Green, modifier = Modifier.padding(top = 12.dp))
        }
        if (error.isNotEmpty()) {
            Text(error, color = Color.Red, modifier = Modifier.padding(top = 12.dp))
        }

        Spacer(modifier = Modifier.height(10.dp))

        TextButton(onClick = { navController.popBackStack() }) {
            Text("Quay lại đăng nhập")
        }
    }
}
