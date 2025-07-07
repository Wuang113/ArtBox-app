package com.example.appvtranh

import android.graphics.Bitmap
import android.graphics.Color
import java.util.*
import androidx.core.graphics.set
import androidx.core.graphics.get

fun floodFill(bitmap: Bitmap, x: Int, y: Int, targetColor: Int, replacementColor: Int) {
    if (targetColor == replacementColor) return
    val width = bitmap.width
    val height = bitmap.height

    val queue: Queue<Pair<Int, Int>> = LinkedList()
    queue.add(Pair(x, y))

    while (queue.isNotEmpty()) {
        val (cx, cy) = queue.remove()
        if (cx !in 0 until width || cy !in 0 until height) continue
        if (bitmap[cx, cy] != targetColor) continue

        bitmap[cx, cy] = replacementColor

        queue.add(Pair(cx + 1, cy))
        queue.add(Pair(cx - 1, cy))
        queue.add(Pair(cx, cy + 1))
        queue.add(Pair(cx, cy - 1))
    }
}
