package com.example.appvtranh.utils

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path

fun createPath(points: List<Offset>): Path {
    return Path().apply {
        if (points.isNotEmpty()) {
            moveTo(points.first().x, points.first().y)
            points.drop(1).forEach {
                lineTo(it.x, it.y)
            }
        }
    }
}

