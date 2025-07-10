package com.example.appvtranh.ui.draw

import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import com.example.appvtranh.controller.DrawController
import com.example.appvtranh.DrawTool
import com.example.appvtranh.utils.createPath
import com.example.appvtranh.utils.floodFill
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

@Composable
fun DrawBox(
    drawController: DrawController,
    selectedTool: DrawTool,
    shapeType: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.White,
    bitmapCallback: (ImageBitmap?, Throwable?) -> Unit = { _, _ -> },
    trackHistory: (undoCount: Int, redoCount: Int) -> Unit = { _, _ -> }
) {
    LaunchedEffect(drawController) {
        drawController.changeBgColor(backgroundColor)
    }

    val view = LocalView.current
    LaunchedEffect(view) {
        drawController.trackHistory(this, trackHistory)
        drawController.trackBitmaps(view, this, bitmapCallback)
    }

    var dragStart by remember { mutableStateOf<Offset?>(null) }
    var dragEnd by remember { mutableStateOf<Offset?>(null) }
    var showClearDialog by remember { mutableStateOf(false) }
    var allowPen by remember { mutableStateOf(false) }
    var canvasSize by remember { mutableStateOf(Size.Zero) }

    val canvasModifier = modifier
        .background(drawController.bgColor)
        .pointerInput(selectedTool, shapeType) {
            if (selectedTool == DrawTool.Delete && !allowPen) {
                if (drawController.pathList.isNotEmpty() || drawController.filledBitmap != null) {
                    showClearDialog = true
                    return@pointerInput
                } else {
                    allowPen = true
                }
            }

            if (selectedTool == DrawTool.Palette) {
                detectTapGestures { offset ->
                    val x = offset.x.roundToInt()
                    val y = offset.y.roundToInt()
                    val fullBitmap = drawController.getCurrentBitmap()

                    if (x in 0 until fullBitmap.width && y in 0 until fullBitmap.height) {
                        val targetColor = fullBitmap.getPixel(x, y)
                        val fillColor = drawController.color.toArgb()
                        floodFill(fullBitmap, x, y, targetColor, fillColor, tolerance = 15)
                        drawController.applyFilledBitmap(fullBitmap.copy(Bitmap.Config.ARGB_8888, true))
                    }
                }
            } else {
                detectDragGestures(
                    onDragStart = { offset ->
                        dragStart = offset
                        if (offset.x in 0f..canvasSize.width && offset.y in 0f..canvasSize.height) {
                            when (selectedTool) {
                                DrawTool.Brush -> drawController.insertNewPath(offset)
                                DrawTool.Eraser -> drawController.beginErase()
                                DrawTool.Circle, DrawTool.Delete, DrawTool.Palette -> {} // Không làm gì cả
                            }
                        }
                    },
                    onDrag = { change, _ ->
                        dragEnd = change.position
                        val point = change.position
                        if (point.x in 0f..canvasSize.width && point.y in 0f..canvasSize.height) {
                            when (selectedTool) {
                                DrawTool.Brush -> drawController.updateLatestPath(point)
                                DrawTool.Eraser -> drawController.applyEraseAt(point)
                                DrawTool.Circle, DrawTool.Delete, DrawTool.Palette -> {}
                            }

                        }
                    },
                    onDragEnd = {
                        if (selectedTool == DrawTool.Circle) {
                            dragStart?.let { start ->
                                dragEnd?.let { end ->
                                    drawController.insertCustomPath(start, end, shapeType)
                                }
                            }
                        }
                        if (selectedTool == DrawTool.Eraser) {
                            drawController.endErase()
                        }
                        dragStart = null
                        dragEnd = null
                    }
                )
            }
        }

    Canvas(canvasModifier) {
        canvasSize = size

        drawController.filledBitmap?.let {
            drawImage(it.asImageBitmap())
        }

        drawController.pathList.forEach { pathWrapper ->
            when {
                pathWrapper.shape != null -> drawPath(
                    pathWrapper.shape,
                    pathWrapper.strokeColor,
                    pathWrapper.alpha,
                    style = Stroke(pathWrapper.strokeWidth)
                )
                pathWrapper.points.size <= 3 && pathWrapper.strokeWidth >= 20f -> {
                    pathWrapper.points.forEach { point ->
                        drawCircle(
                            color = pathWrapper.strokeColor,
                            radius = pathWrapper.strokeWidth / 2f,
                            center = point,
                            alpha = pathWrapper.alpha
                        )
                    }
                }
                else -> {
                    val path = createPath(pathWrapper.points)
                    drawPath(
                        path = path,
                        color = pathWrapper.strokeColor,
                        alpha = pathWrapper.alpha,
                        style = Stroke(width = pathWrapper.strokeWidth)
                    )
                }
            }
        }

        if (selectedTool == DrawTool.Circle && dragStart != null && dragEnd != null) {
            val start = dragStart!!
            val end = dragEnd!!

            val left = min(start.x, end.x)
            val top = min(start.y, end.y)
            val right = max(start.x, end.x)
            val bottom = max(start.y, end.y)
            val width = right - left
            val height = bottom - top

            val previewPath = Path().apply {
                when (shapeType) {
                    "Circle" -> {
                        val radius = min(width, height) / 2f
                        val center = Offset((left + right) / 2f, (top + bottom) / 2f)
                        addOval(Rect(center - Offset(radius, radius), Size(radius * 2, radius * 2)))
                    }
                    "Square" -> {
                        val size = min(width, height)
                        addRect(Rect(Offset(left, top), Size(size, size)))
                    }
                    "Diamond" -> {
                        val cx = (left + right) / 2f
                        val cy = (top + bottom) / 2f
                        moveTo(cx, top)
                        lineTo(right, cy)
                        lineTo(cx, bottom)
                        lineTo(left, cy)
                        close()
                    }
                }
            }

            drawPath(
                path = previewPath,
                color = drawController.color,
                style = Stroke(width = drawController.strokeWidth),
                alpha = drawController.opacity
            )
        }

        if (selectedTool == DrawTool.Eraser && dragEnd != null) {
            drawCircle(
                color = Color.Gray.copy(alpha = 0.3f),
                radius = drawController.eraserSize / 2f,
                center = dragEnd!!
            )
        }
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    drawController.pathList.clear()
                    drawController.filledBitmap = null
                    showClearDialog = false
                    allowPen = true
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showClearDialog = false
                }) {
                    Text("No")
                }
            },
            title = { Text("Xác nhận") },
            text = { Text("Bạn có muốn xóa tất cả những gì đã vẽ không?") }
        )
    }
}
