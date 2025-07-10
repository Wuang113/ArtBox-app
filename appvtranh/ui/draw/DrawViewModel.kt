package com.example.appvtranh.ui.draw

import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.example.appvtranh.DrawTool
import com.example.appvtranh.controller.DrawController
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

class DrawViewModel : ViewModel() {
    val drawController = DrawController()

    var selectedTool by mutableStateOf(DrawTool.Brush)
        private set

    var shapeType by mutableStateOf("Circle")
        private set

    init {
        // ✅ Màu nền canvas và màu Ring giống nhau
        val ringColor = Color(0xFFF5F5F5) // ví dụ: xám nhạt
        drawController.changeColor(ringColor)       // màu cho Ring
        drawController.changeBgColor(ringColor)     // màu nền canvas
    }

    fun changeTool(tool: DrawTool) {
        selectedTool = tool
    }

    fun changeShape(shape: String) {
        shapeType = shape
    }

    fun changeColor(color: Color) {
        drawController.changeColor(color)
    }

    fun changeStrokeWidth(width: Float) {
        drawController.changeStrokeWidth(width)
    }

    fun changeOpacity(opacity: Float) {
        drawController.opacity = opacity
    }

    fun changeEraserSize(size: Float) {
        drawController.changeEraserSize(size)
    }

    fun undo() {
        drawController.unDo()
    }

    fun redo() {
        drawController.reDo()
    }

    fun loadBitmap(bitmap: Bitmap) {
        drawController.applyFilledBitmap(bitmap)
    }

    fun getBitmap(): Bitmap {
        return drawController.getCurrentBitmap()
    }

    fun clearCanvas() {
        drawController.pathList.clear()
        drawController.filledBitmap = null
    }
}
