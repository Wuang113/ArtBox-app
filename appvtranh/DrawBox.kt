package com.example.appvtranh

import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import kotlin.math.roundToInt
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.asImageBitmap

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

    val canvasModifier = modifier
        .fillMaxSize()
        .background(drawController.bgColor)
        .pointerInput(selectedTool, shapeType) {
            detectDragGestures(
                onDragStart = { offset ->
                    dragStart = offset

                    if (selectedTool == DrawTool.Palette) {
                        val x = offset.x.roundToInt()
                        val y = offset.y.roundToInt()
                        drawController.onBitmapReady = { bmp ->
                            if (x in 0 until bmp.width && y in 0 until bmp.height) {
                                val target = bmp.getPixel(x, y)
                                val fillColor = drawController.color.toArgb()
                                floodFill(bmp, x, y, target, fillColor)
                                drawController.filledBitmap = bmp
                            }
                        }
                        drawController.saveBitmap()
                        return@detectDragGestures
                    }

                    if (selectedTool in listOf(DrawTool.Brush, DrawTool.Pen, DrawTool.Ring)) {
                        drawController.insertNewPath(offset, isErase = selectedTool == DrawTool.Ring)
                    }
                },
                onDrag = { change, _ ->
                    dragEnd = change.position
                    if (selectedTool in listOf(DrawTool.Brush, DrawTool.Pen, DrawTool.Ring)) {
                        drawController.updateLatestPath(change.position)
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
                    dragStart = null
                    dragEnd = null
                }
            )
        }

    Canvas(canvasModifier) {
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

        if (selectedTool == DrawTool.Ring && dragEnd != null) {
            drawCircle(
                color = Color.Gray.copy(alpha = 0.5f),
                radius = drawController.eraserSize / 2f,
                center = dragEnd!!
            )
        }
    }
}
