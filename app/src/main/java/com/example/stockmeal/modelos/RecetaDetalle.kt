package com.example.stockmeal.modelos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RecetaDetalle (
    @SerialName("idReceta")
    val idReceta: Int,
    @SerialName("nombre")
    val nombre: String,
    val ingredientes: List<Ingrediente>
)