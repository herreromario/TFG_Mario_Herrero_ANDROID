package com.example.stockmeal.datos.repositorios

import com.example.stockmeal.conexion.RecetaAPI
import com.example.stockmeal.modelos.CapacidadProduccion
import com.example.stockmeal.modelos.Receta
import com.example.stockmeal.modelos.RecetaDetalle

interface RecetaRepository {
    suspend fun obtenerRecetas(): List<Receta>
    suspend fun obtenerRecetaPorId(idReceta: Int): RecetaDetalle
    suspend fun obtenerCapacidadProduccion(): List<CapacidadProduccion>
}

class ConexionRecetaRepository(
    private val recetaAPI: RecetaAPI
): RecetaRepository {
    override suspend fun obtenerRecetas(): List<Receta> = recetaAPI.obtenerRecetas()
    override suspend fun obtenerRecetaPorId(idReceta: Int): RecetaDetalle = recetaAPI.obtenerRecetaPorId(idReceta)
    override suspend fun obtenerCapacidadProduccion(): List<CapacidadProduccion> = recetaAPI.obtenerCapacidadProduccion()
}
