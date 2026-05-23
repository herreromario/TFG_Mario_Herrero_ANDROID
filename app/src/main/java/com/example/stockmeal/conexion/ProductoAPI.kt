package com.example.stockmeal.conexion

import com.example.stockmeal.modelos.Producto
import retrofit2.http.GET

interface ProductoAPI {
    @GET("productos")
    suspend fun obtenerProductos(): List<Producto>

    @GET("productos/platos")
    suspend fun obtenerPlatos(): List<Producto>

    @GET("productos/ingredientes")
    suspend fun obtenerIngredientes(): List<Producto>


}