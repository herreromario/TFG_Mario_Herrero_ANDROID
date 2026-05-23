package com.example.stockmeal.datos.repositorios

import ProduccionAPI
import com.example.stockmeal.modelos.Produccion
import com.example.stockmeal.modelos.ProduccionRequest

interface ProduccionRepository {
    suspend fun obtenerProduccion(): List<Produccion>
    suspend fun obtenerProduccionPorFecha(fecha: String): List<Produccion>
    suspend fun obtenerProduccionPorRango(desde: String, hasta: String): List<Produccion>
    suspend fun obtenerProduccionPorPlato(idProducto: Int): List<Produccion>
    suspend fun registrarProduccion(produccionRequest: ProduccionRequest): Produccion
}

class ConexionProduccionRepository(
    private val produccionAPI: ProduccionAPI
): ProduccionRepository {
    override suspend fun obtenerProduccion(): List<Produccion> = produccionAPI.obtenerProduccion()
    override suspend fun obtenerProduccionPorFecha(fecha: String): List<Produccion> = produccionAPI.obtenerProduccionPorFecha(fecha)
    override suspend fun obtenerProduccionPorRango(desde: String, hasta: String): List<Produccion> = produccionAPI.obtenerProduccionPorRango(desde, hasta)
    override suspend fun obtenerProduccionPorPlato(idProducto: Int): List<Produccion> = produccionAPI.obtenerProduccionPorPlato(idProducto)
    override suspend fun registrarProduccion(produccionRequest: ProduccionRequest): Produccion = produccionAPI.registrarProduccion(produccionRequest)
}
