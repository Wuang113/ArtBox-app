package com.example.appvtranh.ui.gallery

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class GalleryViewModel : ViewModel() {

    val imageList = mutableStateListOf<File>()

    fun loadImages(context: Context, uid: String, pathFromNav: String? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            val userFolder = File(context.filesDir, uid)
            val saved = userFolder.listFiles()?.filter { it.extension == "png" } ?: emptyList()
            imageList.clear()
            imageList.addAll(saved)

            pathFromNav?.let {
                val file = File(Uri.decode(it))
                if (file.exists() && !imageList.contains(file)) {
                    imageList.add(file)
                }
            }
        }
    }

    fun deleteImage(file: File) {
        if (file.exists()) {
            file.delete()
            imageList.remove(file)
        }
    }
}
