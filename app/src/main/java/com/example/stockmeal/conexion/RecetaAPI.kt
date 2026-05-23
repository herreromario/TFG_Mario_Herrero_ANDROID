package com.example.stockmeal.conexion

import com.example.stockmeal.modelos.CapacidadProduccion
import com.example.stockmeal.modelos.Receta
import com.example.stockmeal.modelos.RecetaDetalle
import retrofit2.http.GET
import retrofit2.http.Path

interface RecetaAPI {
    @GET("recetas")
    suspend fun obtenerRecetas(): List<Receta>

    @GET("recetas/{idReceta}")
    suspend fun obtenerRecetaPorId(
        @Path("idReceta") idReceta: Int
    ): RecetaDetalle

    @GET("recetas/capacidad")
    suspend fun obtenerCapacidadProduccion(): List<CapacidadProduccion>
}
