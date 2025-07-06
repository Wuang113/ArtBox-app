package com.example.appvetranh

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color as AwtColor
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.ui.res.painterResource
import java.io.File
import java.io.FileOutputStream
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.core.net.toUri

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawingScreen(navController: NavController) {
    val context = LocalContext.current

    // 🔧 Tạo bitmap trắng
    fun generateFakeDrawing(): Bitmap {
        val width = 800
        val height = 800
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(AwtColor.WHITE) // vẽ nền trắng
        return bitmap
    }

    // 💾 Lưu ảnh và điều hướng
    fun saveAndNavigate(bitmap: Bitmap) {
        val fileName = "drawing_${System.currentTimeMillis()}.png"
        val file = File(context.filesDir, fileName)
        val outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        outputStream.flush()
        outputStream.close()

        navController.navigate("gallery?path=${Uri.encode(file.absolutePath)}")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.statusBars.asPaddingValues()) // ✅ Tránh status bar / notch
    ) {
        // ----- Top Bar (Setting) -----
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFEFEFEF))
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { /* Back */ }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
                IconButton(onClick = { /* Forward */ }) {
                    Icon(Icons.Default.ArrowForward, contentDescription = "Forward")
                }
            }

            Text("100%", fontSize = 16.sp)

            IconButton(onClick = { /* Menu */ }) {
                Icon(Icons.Default.MoreVert, contentDescription = "Menu")
            }
        }

        // ----- Main Content -----
        Row(modifier = Modifier.weight(1f)) {
            // Tools + Layer Panel
            Column(
                modifier = Modifier
                    .width(110.dp)
                    .fillMaxHeight()
            ) {
                Spacer(modifier = Modifier.height(16.dp)) // ✅ đẩy khung xuống một chút

                // ----- Layers -----
                Column(
                    modifier = Modifier
                        .padding(6.dp)
                        .background(Color.White)
                ) {
                    Text("Layers", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    repeat(3) { index ->
                        var checked by remember { mutableStateOf(index == 0) }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(2.dp)
                                .border(1.dp, Color.Gray)
                                .padding(4.dp)
                                .toggleable(
                                    value = checked,
                                    onValueChange = { checked = it }
                                )
                        ) {
                            Checkbox(checked = checked, onCheckedChange = null)
                            Text("Layer ${index + 1}", fontSize = 15.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // ----- Tools -----
                Box(
                    modifier = Modifier
                        .background(Color(0xFFF0F0F0))
                        .padding(vertical = 8.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val toolIcons = listOf(
                            Icons.Default.Brush to "Brush",
                            Icons.Default.Circle to "Circle",
                            Icons.Default.ChangeHistory to "Diamond",
                            Icons.Default.TextFields to "Text",
                            Icons.Default.Create to "Pen",
                            Icons.Default.RadioButtonUnchecked to "Ring",
                            Icons.Default.Palette to "Palette"
                        )

                        toolIcons.forEach { (icon, desc) ->
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .border(1.dp, Color.Gray, shape = CircleShape)
                                    .padding(6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(icon, contentDescription = desc, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }
            }

            // Drawing Area (giữ nguyên)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
                    .background(Color.White)
            ) {
                // Canvas placeholder
            }
        }

        // ----- Bottom bar -----
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF0F0F0))
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Image, contentDescription = "Gallery")

            Image(
                painter = painterResource(id = R.drawable.logo_2),
                contentDescription = "Logo",
                modifier = Modifier.size(40.dp)
            )

            IconButton(onClick = {
                val bitmap = generateFakeDrawing()
                saveAndNavigate(bitmap)
            }) {
                Icon(Icons.Default.Download, contentDescription = "Download")
            }
        }
    }
}
