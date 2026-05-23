package com.example.stockmeal.modelos

import androidx.compose.ui.graphics.vector.ImageVector

data class BottomItem(
    val titulo: Int,
    val ruta: String,
    val iconoSeleccionado: ImageVector,
    val iconoNoSeleccionado: ImageVector
)