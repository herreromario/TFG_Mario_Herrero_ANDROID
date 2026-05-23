package com.example.stockmeal.ui.state

sealed class AppUIState<out T> {
    object Cargando : AppUIState<Nothing>()
    data class Exito<T>(val datos: T) : AppUIState<T>()
    data class Error(val mensaje: String) : AppUIState<Nothing>()
}