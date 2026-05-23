package com.example.stockmeal.modelos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CapacidadProduccion(
    @SerialName("idProducto")
    val idProducto: Int,
    @SerialName("nombre")
    val nombre: String,
    @SerialName("unidadesPosibles")
    val unidadesPosibles: Int
)
