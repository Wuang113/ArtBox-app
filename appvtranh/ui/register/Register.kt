package com.example.appvtranh.ui.register

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@Composable
fun RegisterScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModel: RegisterViewModel = viewModel()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var repassword by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .padding(16.dp)
                .size(70.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.Black,
                modifier = Modifier.size(24.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, end = 16.dp, top = 80.dp)
        ) {
            Text("Đăng ký", style = MaterialTheme.typography.headlineMedium)
            Text("Đăng ký tài khoản", color = Color.Gray)

            Spacer(modifier = Modifier.height(40.dp))

            Text("Họ và tên")
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = { Text("Nhập họ và tên") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Email")
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("abc@gmail.com") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Password")
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("Nhập mật khẩu") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Nhập lại mật khẩu")
            OutlinedTextField(
                value = repassword,
                onValueChange = { repassword = it },
                placeholder = { Text("Nhập lại mật khẩu") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    viewModel.register(
                        email = email,
                        password = password,
                        confirmPassword = repassword,
                        context = context,
                        onSuccess = {
                            Toast.makeText(context, "Đăng ký thành công", Toast.LENGTH_SHORT).show()
                            navController.navigate("gallery") {
                                popUpTo("register") { inclusive = true }
                            }
                        },
                        onError = {
                            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                        }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF74E8FF),
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Đăng ký")
            }
        }
    }
}
