package com.example.appvtranh.ui.register

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import androidx.lifecycle.ViewModel

class RegisterViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    fun register(
        email: String,
        password: String,
        confirmPassword: String,
        context: Context,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            onError("Vui lòng nhập đầy đủ thông tin")
            return
        }

        if (password != confirmPassword) {
            onError("Mật khẩu không khớp")
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    onError(task.exception?.message ?: "Đăng ký thất bại")
                }
            }
    }
}
