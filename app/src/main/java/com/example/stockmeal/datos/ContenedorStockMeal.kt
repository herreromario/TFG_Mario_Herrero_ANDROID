package com.example.stockmeal.datos

import ProduccionAPI
import android.content.Context
import com.example.stockmeal.conexion.ProductoAPI
import com.example.stockmeal.conexion.RecetaAPI
import com.example.stockmeal.datos.repositorios.ConexionProduccionRepository
import com.example.stockmeal.datos.repositorios.ConexionProductoRepository
import com.example.stockmeal.datos.repositorios.ConexionRecetaRepository
import com.example.stockmeal.datos.repositorios.ProduccionRepository
import com.example.stockmeal.datos.repositorios.ProductoRepository
import com.example.stockmeal.datos.repositorios.RecetaRepository
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

interface ContenedorStockMeal {
    val produccionRepository: ProduccionRepository
    val productoRepository: ProductoRepository
    val recetaRepository: RecetaRepository
}

class DefaultContenedorStockMeal(context: Context): ContenedorStockMeal {

    // RETROFIT
    private val baseUrl = "http://10.0.2.2:8080/"
    private val json = Json { ignoreUnknownKeys = true }

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .baseUrl(baseUrl)
        .build()


    // SERVICIOS
    private val servicioProduccionAPI: ProduccionAPI by lazy {
        retrofit.create(ProduccionAPI::class.java)
    }
    private val servicioProductoAPI: ProductoAPI by lazy {
        retrofit.create(ProductoAPI::class.java)
    }
    private val servicioRecetaAPI: RecetaAPI by lazy {
        retrofit.create(RecetaAPI::class.java)
    }

    // REPOSITORIOS

    override val produccionRepository: ProduccionRepository by lazy {
        ConexionProduccionRepository(servicioProduccionAPI)
    }

    override val productoRepository: ProductoRepository by lazy {
        ConexionProductoRepository(servicioProductoAPI)
    }

    override val recetaRepository: RecetaRepository by lazy {
        ConexionRecetaRepository(servicioRecetaAPI)
    }
}