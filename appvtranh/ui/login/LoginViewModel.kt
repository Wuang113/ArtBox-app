package com.example.appvtranh.ui.login

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class LoginViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun loginWithEmail(
        email: String,
        password: String,
        context: Context,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Đăng nhập thành công", Toast.LENGTH_SHORT).show()
                    onSuccess()
                } else {
                    onFailure(task.exception?.message ?: "Đăng nhập thất bại")
                }
            }
    }

    fun loginWithGoogleToken(
        token: String,
        context: Context,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val credential = GoogleAuthProvider.getCredential(token, null)
        auth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(context, "Đăng nhập Google thành công", Toast.LENGTH_SHORT).show()
                onSuccess()
            } else {
                onFailure(task.exception?.message ?: "Đăng nhập Google thất bại")
            }
        }
    }
}
