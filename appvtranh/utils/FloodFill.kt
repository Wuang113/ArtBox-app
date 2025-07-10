package com.example.appvtranh.utils

import android.graphics.Bitmap
import java.util.*
import kotlin.math.abs

fun floodFill(bitmap: Bitmap, x: Int, y: Int, targetColor: Int, replacementColor: Int, tolerance: Int = 10) {
    if (targetColor == replacementColor) return

    val width = bitmap.width
    val height = bitmap.height
    val pixels = IntArray(width * height)
    bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

    fun isSimilarColor(c1: Int, c2: Int): Boolean {
        val r1 = (c1 shr 16) and 0xFF
        val g1 = (c1 shr 8) and 0xFF
        val b1 = c1 and 0xFF
        val r2 = (c2 shr 16) and 0xFF
        val g2 = (c2 shr 8) and 0xFF
        val b2 = c2 and 0xFF
        return (abs(r1 - r2) <= tolerance &&
                abs(g1 - g2) <= tolerance &&
                abs(b1 - b2) <= tolerance)
    }

    val queue: ArrayDeque<Pair<Int, Int>> = ArrayDeque()
    queue.add(Pair(x, y))

    while (queue.isNotEmpty()) {
        val (cx, cy) = queue.removeFirst()
        val index = cy * width + cx
        if (cx in 0 until width && cy in 0 until height && isSimilarColor(pixels[index], targetColor)) {
            pixels[index] = replacementColor
            queue.add(Pair(cx + 1, cy))
            queue.add(Pair(cx - 1, cy))
            queue.add(Pair(cx, cy + 1))
            queue.add(Pair(cx, cy - 1))
        }
    }

    bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
}

