package com.example.appvtranh

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View

fun View.drawBitmapFromView(context: Context, config: Bitmap.Config = Bitmap.Config.ARGB_8888): Bitmap? {
    return try {
        val bitmap = Bitmap.createBitmap(width, height, config)
        val canvas = Canvas(bitmap)
        draw(canvas)
        bitmap
    } catch (e: Exception) {
        null
    }
}
