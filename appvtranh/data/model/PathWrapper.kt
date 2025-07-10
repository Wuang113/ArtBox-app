package com.example.appvtranh.data.model

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path

data class PathWrapper(
    val points: SnapshotStateList<Offset> = mutableStateListOf(),
    val strokeColor: Color,
    val alpha: Float,
    val strokeWidth: Float,
    val shape: Path? = null,
    val text: String? = null,
    val textStart: Offset? = null
)