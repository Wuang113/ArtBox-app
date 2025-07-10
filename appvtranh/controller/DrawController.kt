package com.example.appvtranh.controller

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.view.View
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import com.example.appvtranh.data.model.PathWrapper
import com.example.appvtranh.utils.drawBitmapFromView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*

class DrawController {

    private val _undo = mutableStateListOf<PathWrapper>()
    private val _redo = mutableStateListOf<PathWrapper>()
    val pathList: SnapshotStateList<PathWrapper> = _undo

    private val _hist = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    fun trackHistory(scope: CoroutineScope, callback: (undo: Int, redo: Int) -> Unit) {
        _hist.onEach { callback(_undo.size, _redo.size) }.launchIn(scope)
    }

    private val _bmGen = MutableSharedFlow<Bitmap.Config>(1)
    fun saveBitmap(config: Bitmap.Config = Bitmap.Config.ARGB_8888) = _bmGen.tryEmit(config)

    var strokeWidth by mutableFloatStateOf(8f)
    var color by mutableStateOf(Color.Black)
    var bgColor by mutableStateOf(Color(0xFFF5F5F5)) // hoặc bất kỳ màu nào bạn muốn Ring xóa
    var opacity by mutableFloatStateOf(1f)
    var eraserSize by mutableFloatStateOf(40f)

    var filledBitmap: Bitmap? by mutableStateOf(null)
    var onBitmapReady: ((Bitmap) -> Unit)? = null

    fun changeStrokeWidth(value: Float) { strokeWidth = value }
    fun changeColor(value: Color) { color = value }
    fun changeBgColor(value: Color) { bgColor = value }
    fun changeOpacity(value: Float) { opacity = value }
    fun changeEraserSize(value: Float) { eraserSize = value }

    fun unDo() {
        if (_undo.isNotEmpty()) {
            _redo.add(_undo.removeAt(_undo.lastIndex))
            _hist.tryEmit(Unit)
        }
    }

    fun reDo() {
        if (_redo.isNotEmpty()) {
            _undo.add(_redo.removeAt(_redo.lastIndex))
            _hist.tryEmit(Unit)
        }
    }

    fun insertNewPath(start: Offset, isErase: Boolean = false) {
        _undo.add(
            PathWrapper(
                points = mutableStateListOf(start),
                strokeColor = if (isErase) bgColor else color,
                alpha = if (isErase) 1f else opacity,
                strokeWidth = if (isErase) eraserSize else strokeWidth
            )
        )
        _redo.clear()
        _hist.tryEmit(Unit)
    }

    fun updateLatestPath(point: Offset) {
        _undo.lastOrNull()?.points?.add(point)
    }

    fun insertCustomPath(start: Offset, end: Offset, shapeType: String) {
        val left = minOf(start.x, end.x)
        val top = minOf(start.y, end.y)
        val right = maxOf(start.x, end.x)
        val bottom = maxOf(start.y, end.y)
        val width = right - left
        val height = bottom - top

        val path = Path()
        when (shapeType) {
            "Circle" -> {
                val radius = minOf(width, height) / 2f
                val center = Offset((left + right) / 2f, (top + bottom) / 2f)
                path.addOval(Rect(center - Offset(radius, radius), Size(radius * 2, radius * 2)))
            }
            "Square" -> {
                val size = minOf(width, height)
                path.addRect(Rect(Offset(left, top), Size(size, size)))
            }
            "Diamond" -> {
                val cx = (left + right) / 2f
                val cy = (top + bottom) / 2f
                path.moveTo(cx, top)
                path.lineTo(right, cy)
                path.lineTo(cx, bottom)
                path.lineTo(left, cy)
                path.close()
            }
        }

        _undo.add(
            PathWrapper(
                points = mutableStateListOf(),
                strokeColor = color,
                alpha = opacity,
                strokeWidth = strokeWidth,
                shape = path
            )
        )
        _redo.clear()
        _hist.tryEmit(Unit)
    }

    fun trackBitmaps(
        view: View,
        scope: CoroutineScope,
        onResult: (ImageBitmap?, Throwable?) -> Unit
    ) {
        _bmGen
            .mapNotNull { config -> view.drawBitmapFromView(view.context, config) }
            .onEach { bitmap ->
                onBitmapReady?.invoke(bitmap)
                onResult(bitmap.asImageBitmap(), null)
            }
            .catch { error -> onResult(null, error) }
            .launchIn(scope)
    }

    fun getCurrentBitmap(width: Int = 1080, height: Int = 1920): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        filledBitmap?.let {
            canvas.drawBitmap(it, 0f, 0f, null)
        } ?: canvas.drawColor(bgColor.toArgb())

        for (wrapper in pathList) {
            val paint = Paint().apply {
                color = wrapper.strokeColor.toArgb()
                style = Paint.Style.STROKE
                strokeWidth = wrapper.strokeWidth
                alpha = (wrapper.alpha * 255).toInt()
                isAntiAlias = true
            }

            if (wrapper.shape != null) {
                canvas.drawPath(wrapper.shape.asAndroidPath(), paint)
            } else if (wrapper.points.size >= 2) {
                val path = android.graphics.Path().apply {
                    moveTo(wrapper.points[0].x, wrapper.points[0].y)
                    for (i in 1 until wrapper.points.size) {
                        lineTo(wrapper.points[i].x, wrapper.points[i].y)
                    }
                }
                canvas.drawPath(path, paint)
            }
        }

        return bitmap
    }

    fun applyFilledBitmap(bitmap: Bitmap) {
        filledBitmap = bitmap
        _hist.tryEmit(Unit)
    }

    // ✅ Dùng bgColor để tẩy thay vì thao tác trực tiếp bitmap
    private var isErasing = false
    fun beginErase() {
        isErasing = true
        insertNewPath(Offset.Zero, isErase = true) // Dummy start, sẽ update lại ngay
    }

    fun applyEraseAt(offset: Offset) {
        if (isErasing) {
            if (_undo.lastOrNull()?.strokeColor != bgColor) {
                insertNewPath(offset, isErase = true)
            } else {
                updateLatestPath(offset)
            }
        }
    }

    fun endErase() {
        isErasing = false
    }
}
