package com.example.appvetranh


import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    // Google SignIn setup
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(context.getString(R.string.default_web_client_id))
        .requestEmail()
        .build()
    val googleSignInClient = GoogleSignIn.getClient(context, gso)

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            auth.signInWithCredential(credential).addOnCompleteListener { authTask ->
                if (authTask.isSuccessful) {
                    Toast.makeText(context, "Đăng nhập Google thành công", Toast.LENGTH_SHORT).show()

                    //Sao khi đăng nhập thành công nó sẽ chuyển sang Gallery
                    navController.navigate("Gallery") {
                        popUpTo("login") { inclusive = true }
                    }
                } else {
                    Toast.makeText(context, "Lỗi: ${authTask.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
        } catch (e: ApiException) {
            Toast.makeText(context, "Google Sign-In lỗi: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
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
            Text("Welcome back", style = MaterialTheme.typography.headlineMedium)
            Text("Đăng nhập tài khoản của bạn", color = Color.Gray)

            Spacer(modifier = Modifier.height(60.dp))

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

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = {
                    navController.navigate("forgot_password")
                }) {
                    Text("Quên mật khẩu?")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    loading = true
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            loading = false
                            if (task.isSuccessful) {
                                Toast.makeText(context, "Đăng nhập thành công", Toast.LENGTH_SHORT).show()
                                navController.navigate("draw") {
                                    popUpTo("login") { inclusive = true }
                                }
                            } else {
                                Toast.makeText(context, "Lỗi: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                enabled = !loading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF74E8FF),
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Đăng nhập")
            }

            Spacer(modifier = Modifier.height(30.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Chưa có tài khoản?")
                TextButton(
                    onClick = {
                        navController.navigate("register")
                    },
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("Đăng ký")
                }
            }

            Divider(
                color = Color.LightGray,
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 12.dp)
            )

            Text(
                "Hoặc",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(23.dp))

            Button(
                onClick = {
                    val signInIntent = googleSignInClient.signInIntent
                    launcher.launch(signInIntent)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE5E0E0),
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.googleicon),
                        contentDescription = "Google Logo",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Đăng nhập với Google", fontSize = 16.sp)
                }
            }
        }
    }
}
