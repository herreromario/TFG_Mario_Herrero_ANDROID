package com.example.stockmeal.modelos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Producto (
    @SerialName("idProducto")
    val idProducto: Int,
    @SerialName("nombre")
    val nombre: String,
    @SerialName("descripcion")
    val descripcion: String,
    @SerialName("unidad")
    val unidad: String,
    @SerialName("stockActual")
    val stockActual: Int,
    @SerialName("stockMinimo")
    val stockMinimo: Int
)