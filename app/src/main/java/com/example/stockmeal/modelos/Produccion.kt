package com.example.stockmeal.modelos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Produccion(
    @SerialName("idProduccion")
    val idProduccion: Int,
    @SerialName("plato")
    val plato: String,
    @SerialName("cantidad")
    val cantidad: Int,
    @SerialName("fecha")
    val fecha: String
)