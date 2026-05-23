package com.example.stockmeal.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.stockmeal.StockMealAplicacion
import com.example.stockmeal.datos.repositorios.ProductoRepository
import com.example.stockmeal.modelos.Producto
import com.example.stockmeal.ui.state.AppUIState
import kotlinx.coroutines.launch

data class IngredienteStockUI(
    val producto: Producto,
    val progresoExistencias: Float,
    val enAlerta: Boolean
)

class StockViewModel(
    private val productoRepository: ProductoRepository
) : ViewModel() {

    var stockState by mutableStateOf<AppUIState<List<IngredienteStockUI>>>(AppUIState.Cargando)
        private set

    init {
        obtenerIngredientesStock()
    }

    fun obtenerIngredientesStock() {
        viewModelScope.launch {
            stockState = AppUIState.Cargando
            try {
                val ingredientesStock = productoRepository.obtenerIngredientes()
                    .map { producto ->
                        IngredienteStockUI(
                            producto = producto,
                            progresoExistencias = calcularProgresoExistencias(producto),
                            enAlerta = producto.stockActual <= producto.stockMinimo
                        )
                    }

                stockState = AppUIState.Exito(ingredientesStock)
            } catch (e: Exception) {
                e.printStackTrace()
                stockState = AppUIState.Error("Error cargando el stock")
            }
        }
    }

    private fun calcularProgresoExistencias(producto: Producto): Float {
        if (producto.stockMinimo <= 0) {
            return 1f
        }

        val stockMaximoEstimado = producto.stockMinimo * 2f
        val stockDisponibleSobreMinimo = producto.stockActual - producto.stockMinimo
        val rangoStockDisponible = stockMaximoEstimado - producto.stockMinimo

        return (stockDisponibleSobreMinimo / rangoStockDisponible).coerceIn(0f, 1f)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val aplicacion = (this[APPLICATION_KEY] as StockMealAplicacion)
                val productoRepository = aplicacion.contenedorStockMeal.productoRepository
                StockViewModel(
                    productoRepository = productoRepository
                )
            }
        }
    }
}
