package com.example.stockmeal

import android.app.Application
import com.example.stockmeal.datos.ContenedorStockMeal
import com.example.stockmeal.datos.DefaultContenedorStockMeal

class StockMealAplicacion : Application() {
    lateinit var contenedorStockMeal: ContenedorStockMeal
    override fun onCreate() {
        super.onCreate()
        contenedorStockMeal = DefaultContenedorStockMeal(this)
    }
}