package com.example.appvtranh

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appvtranh.DrawTool.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawingScreen() {
    val drawController = rememberDrawController()
    var selectedTool by remember { mutableStateOf(Brush) }
    var shapeType by remember { mutableStateOf("Circle") }
    var showColorDialog by remember { mutableStateOf(false) }

    val showOpacitySlider = selectedTool == Brush || selectedTool == Circle
    val showStrokeWidthSlider = selectedTool == Brush || selectedTool == Circle || selectedTool == Ring
    val showShapeSelector = selectedTool == Circle

    Column(modifier = Modifier.fillMaxSize()) {

        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFEFEFEF))
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { drawController.unDo() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Undo")
                }
                IconButton(onClick = { drawController.reDo() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Redo")
                }
            }

            Text("100%", fontSize = 16.sp)

            IconButton(onClick = { /* menu */ }) {
                Icon(Icons.Default.MoreVert, contentDescription = "Menu")
            }
        }

        // Tool Settings (top right)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showOpacitySlider) {
                Column(horizontalAlignment = Alignment.End) {
                    Text("Opacity", fontSize = 12.sp)
                    Slider(
                        value = drawController.opacity,
                        onValueChange = { drawController.opacity = it },
                        valueRange = 0f..1f,
                        steps = 9,
                        modifier = Modifier.width(120.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }

            if (showStrokeWidthSlider) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = if (selectedTool == Ring) "Eraser Size" else "Width",
                        fontSize = 12.sp
                    )
                    Slider(
                        value = if (selectedTool == Ring) drawController.eraserSize else drawController.strokeWidth,
                        onValueChange = {
                            if (selectedTool == Ring) drawController.changeEraserSize(it)
                            else drawController.changeStrokeWidth(it)
                        },
                        valueRange = 1f..100f,
                        steps = 9,
                        modifier = Modifier.width(120.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }

            if (showShapeSelector) {
                Column(horizontalAlignment = Alignment.End) {
                    Text("Shape", fontSize = 12.sp)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        listOf("Circle", "Square", "Diamond").forEach { shape ->
                            val selected = shapeType == shape
                            Text(
                                text = shape,
                                modifier = Modifier
                                    .padding(4.dp)
                                    .clickable { shapeType = shape }
                                    .background(if (selected) Color.LightGray else Color.Transparent)
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        // Main Content
        Row(modifier = Modifier.weight(1f)) {

            // Sidebar Tools: Left Centered
            Box(
                modifier = Modifier
                    .width(80.dp)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .wrapContentHeight()
                        .background(Color(0xFFF0F0F0))
                        .padding(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val tools = listOf(
                        Brush to Icons.Default.Brush,
                        Circle to Icons.Default.Circle,
                        Pen to Icons.Default.Palette,
                        Ring to Icons.Default.RadioButtonUnchecked,
                        Palette to Icons.Default.FormatPaint

                    )


                    tools.forEach { (tool, icon) ->
                        val isSelected = tool == selectedTool
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) Color.Blue else Color.Gray,
                                    shape = CircleShape
                                )
                                .padding(6.dp)
                                .clickable {
                                    selectedTool = tool
                                    if (tool == Pen) {
                                        showColorDialog = true
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(icon, contentDescription = tool.name, modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }

            // Drawing Canvas
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
                    .background(Color.White)
            ) {
                DrawBox(
                    drawController = drawController,
                    bitmapCallback = { _, _ -> },
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
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Image, contentDescription = "Gallery")
            Image(
                painter = painterResource(id = R.drawable.logo_2),
                contentDescription = "Logo",
                modifier = Modifier.size(40.dp)
            )
            Icon(Icons.Default.Download, contentDescription = "Download")
        }
    }

    // Color Picker Dialog
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
                                            drawController.changeColor(color)
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
