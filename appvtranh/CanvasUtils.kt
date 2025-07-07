package com.example.appvtranh

import android.content.Context
import android.graphics.Bitmap
import android.view.View

fun View.drawBitmapFromCanvas(context: Context, config: Bitmap.Config): Bitmap? {
    val bitmap = Bitmap.createBitmap(this.width, this.height, config)
    val canvas = android.graphics.Canvas(bitmap)
    this.draw(canvas)  // chỉ vẽ nội dung View (canvas area)
    return bitmap
}
