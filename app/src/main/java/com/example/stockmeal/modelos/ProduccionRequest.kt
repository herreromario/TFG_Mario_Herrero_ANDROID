package com.example.stockmeal.modelos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProduccionRequest (
    @SerialName("idProducto")
    val idProducto: Int,
    @SerialName("cantidad")
    val cantidad: Int,
    @SerialName("fecha")
    val fecha: String
)
