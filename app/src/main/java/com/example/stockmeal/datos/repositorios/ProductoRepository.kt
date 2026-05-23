package com.example.stockmeal.datos.repositorios

import com.example.stockmeal.conexion.ProductoAPI
import com.example.stockmeal.modelos.Producto

interface ProductoRepository {
    suspend fun obtenerProductos(): List<Producto>
    suspend fun obtenerPlatos(): List<Producto>
    suspend fun obtenerIngredientes(): List<Producto>
}

class ConexionProductoRepository(
    private val productoAPI: ProductoAPI
): ProductoRepository {
    override suspend fun obtenerProductos(): List<Producto> = productoAPI.obtenerProductos()
    override suspend fun obtenerPlatos(): List<Producto> = productoAPI.obtenerPlatos()
    override suspend fun obtenerIngredientes(): List<Producto> = productoAPI.obtenerIngredientes()
}
