package com.example.appvtranh.ui.draw

import android.Manifest
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.appvtranh.DrawTool.*
import com.example.appvtranh.R
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawingScreen(navController: NavController, path: String?) {
    val context = LocalContext.current
    val viewModel: DrawViewModel = viewModel()
    val drawController = viewModel.drawController

    val selectedTool = viewModel.selectedTool
    val shapeType = viewModel.shapeType

    var showColorDialog by remember { mutableStateOf(false) }
    var showSaveDialog by remember { mutableStateOf(false) }
    var showClearDialog by remember { mutableStateOf(false) }
    var showSidebar by remember { mutableStateOf(true) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (!isGranted) {
                Toast.makeText(context, "Bạn cần cấp quyền để lưu ảnh.", Toast.LENGTH_SHORT).show()
            }
        }
    )

    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    LaunchedEffect(path) {
        path?.let {
            val file = File(Uri.decode(it))
            if (file.exists()) {
                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                viewModel.loadBitmap(bitmap)
            }
        }
    }

    fun saveToGallery() {
        val bitmap = viewModel.getBitmap()
        val fileName = "drawing_${System.currentTimeMillis()}.png"
        val contentValues = android.content.ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/DrawingApp")
        }

        val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        uri?.let {
            context.contentResolver.openOutputStream(it)?.use { out ->
                bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, out)
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {

        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFEFEFEF))
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { viewModel.undo() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Undo")
                }
                IconButton(onClick = { viewModel.redo() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Redo")
                }
            }

            Text("100%", fontSize = 16.sp)

            IconButton(onClick = {}) {
                Icon(Icons.Default.MoreVert, contentDescription = "Menu")
            }
        }

        // Tool Settings
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.End
        ) {
            if (selectedTool == Brush || selectedTool == Circle) {
                Column(horizontalAlignment = Alignment.End) {
                    Text("Opacity", fontSize = 12.sp)
                    Slider(
                        value = drawController.opacity,
                        onValueChange = { viewModel.changeOpacity(it) },
                        valueRange = 0f..1f,
                        steps = 9,
                        modifier = Modifier.width(120.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }

            if (selectedTool == Brush || selectedTool == Circle || selectedTool == Ring) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = if (selectedTool == Ring) "Eraser Size" else "Width",
                        fontSize = 12.sp
                    )
                    Slider(
                        value = if (selectedTool == Ring) drawController.eraserSize else drawController.strokeWidth,
                        onValueChange = {
                            if (selectedTool == Ring) viewModel.changeEraserSize(it)
                            else viewModel.changeStrokeWidth(it)
                        },
                        valueRange = 1f..100f,
                        steps = 9,
                        modifier = Modifier.width(120.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }

            if (selectedTool == Circle) {
                Column(horizontalAlignment = Alignment.End) {
                    Text("Shape", fontSize = 12.sp)
                    Row {
                        listOf("Circle", "Square", "Diamond").forEach { shape ->
                            val selected = shape == shapeType
                            Text(
                                text = shape,
                                modifier = Modifier
                                    .padding(4.dp)
                                    .clickable { viewModel.changeShape(shape) }
                                    .background(if (selected) Color.LightGray else Color.Transparent)
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        // Main Area
        Row(modifier = Modifier.weight(1f)) {
            if (showSidebar) {
                Column(
                    modifier = Modifier
                        .width(80.dp)
                        .background(Color(0xFFF0F0F0))
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    IconButton(onClick = { showSidebar = false }) {
                        Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Hide")
                    }

                    val tools = listOf(
                        Brush to Icons.Default.Brush,
                        Circle to Icons.Default.Circle,
                        Pen to Icons.Default.Delete, // Pen là công cụ xoá toàn bộ
                        Ring to painterResource(id = R.drawable.eraser),
                        Palette to Icons.Default.Palette
                    )

                    tools.forEach { (tool, icon) ->
                        val selected = selectedTool == tool
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .border(
                                    width = if (selected) 2.dp else 1.dp,
                                    color = if (selected) Color.Blue else Color.Gray,
                                    shape = CircleShape
                                )
                                .padding(6.dp)
                                .clickable {
                                    when (tool) {
                                        Pen -> showClearDialog = true
                                        else -> {
                                            viewModel.changeTool(tool) // 🟢 Đảm bảo dòng này luôn được gọi cho Ring
                                            if (tool in listOf(Palette, Brush, Circle)) {
                                                showColorDialog = true
                                            }
                                        }
                                    }
                                }

                            ,
                            contentAlignment = Alignment.Center
                        ) {
                            when (icon) {
                                is ImageVector -> Icon(icon, contentDescription = tool.name, modifier = Modifier.size(20.dp))
                                is Painter -> Image(painter = icon, contentDescription = tool.name, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    IconButton(onClick = { showSidebar = true }) {
                        Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Show")
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .background(Color.White)
            ) {
                DrawBox(
                    drawController = drawController,
                    selectedTool = selectedTool,
                    shapeType = shapeType,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // Bottom bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF0F0F0))
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(onClick = { showSaveDialog = true }) {
                Icon(Icons.Default.Download, contentDescription = "Save")
            }

            Image(
                painter = painterResource(id = R.drawable.logo_2),
                contentDescription = "Logo",
                modifier = Modifier.size(40.dp)
            )

            IconButton(onClick = {
                val bitmap = viewModel.getBitmap()
                val uid = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: "unknown_user"
                val userFolder = File(context.filesDir, uid)
                if (!userFolder.exists()) userFolder.mkdirs()

                val file = if (path != null) File(Uri.decode(path))
                else File(userFolder, "drawing_${System.currentTimeMillis()}.png")

                FileOutputStream(file).use { out ->
                    bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, out)
                }

                navController.navigate("gallery?path=${Uri.encode(file.absolutePath)}")
            }) {
                Icon(Icons.Default.Image, contentDescription = "To Gallery")
            }
        }
    }

    // Save dialog
    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = { Text("Lưu ảnh") },
            text = { Text("Bạn có muốn lưu ảnh về thư viện không?") },
            confirmButton = {
                TextButton(onClick = {
                    saveToGallery()
                    showSaveDialog = false
                    Toast.makeText(context, "Đã lưu vào thư viện", Toast.LENGTH_SHORT).show()
                }) {
                    Text("Lưu")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveDialog = false }) {
                    Text("Hủy")
                }
            }
        )
    }

    // Clear dialog (Pen tool)
    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.clearCanvas()
                    viewModel.changeTool(Pen)
                    showClearDialog = false
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("No")
                }
            },
            title = { Text("Xác nhận") },
            text = { Text("Bạn có muốn xóa tất cả những gì đã vẽ không?") }
        )
    }

    // Color picker
    if (showColorDialog) {
        AlertDialog(
            onDismissRequest = { showColorDialog = false },
            confirmButton = {},
            title = { Text("Chọn màu") },
            text = {
                Column {
                    listOf(
                        Color.Red, Color.Green, Color.Blue,
                        Color.Yellow, Color.Magenta, Color.Cyan,
                        Color.Black, Color.Gray
                    ).chunked(4).forEach { row ->
                        Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                            row.forEach { color ->
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .padding(4.dp)
                                        .background(color, CircleShape)
                                        .clickable {
                                            viewModel.changeColor(color)
                                            showColorDialog = false
                                        }
                                )
                            }
                        }
                    }
                }
            }
        )
    }
}
