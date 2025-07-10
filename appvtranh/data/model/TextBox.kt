package com.example.appvtranh.data.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntSize

data class TextBox(
    var rect: Rect,
    var color: Color,
    var text: String = "",
    var fontSize: Float = 16f,
    var isFocused: Boolean = false
) {
    fun contains(point: Offset): Boolean {
        return rect.contains(point)
    }

    fun updatePosition(newStart: Offset, newEnd: Offset) {
        rect = Rect(
            left = minOf(newStart.x, newEnd.x),
            top = minOf(newStart.y, newEnd.y),
            right = maxOf(newStart.x, newEnd.x),
            bottom = maxOf(newStart.y, newEnd.y)
        )
    }

    fun size(): IntSize {
        return IntSize(rect.width.toInt(), rect.height.toInt())
    }
}