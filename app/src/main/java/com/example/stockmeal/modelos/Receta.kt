package com.example.stockmeal.modelos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Receta (
    @SerialName("idReceta")
    val idReceta: Int,
    @SerialName("nombre")
    val nombre: String,
    @SerialName("numIngredientes")
    val numIngredientes: Int
)