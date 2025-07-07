package com.example.appvtranh

import android.graphics.Bitmap
import android.view.View
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
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
    var bgColor by mutableStateOf(Color.White)
    var opacity by mutableFloatStateOf(1f)
    var eraserSize by mutableFloatStateOf(40f)

    var filledBitmap: Bitmap? by mutableStateOf(null)
    var onBitmapReady: ((Bitmap) -> Unit)? = null

    fun changeOpacity(value: Float) { opacity = value }
    fun changeStrokeWidth(value: Float) { strokeWidth = value }
    fun changeColor(value: Color) { color = value }
    fun changeBgColor(value: Color) { bgColor = value }
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
        onResult: (androidx.compose.ui.graphics.ImageBitmap?, Throwable?) -> Unit
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
}

@Composable
fun rememberDrawController() = remember { DrawController() }
