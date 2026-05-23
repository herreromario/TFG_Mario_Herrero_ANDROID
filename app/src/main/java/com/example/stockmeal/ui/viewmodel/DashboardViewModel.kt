package com.example.stockmeal.ui.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
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
import com.example.stockmeal.datos.repositorios.ProduccionRepository
import com.example.stockmeal.datos.repositorios.ProductoRepository
import com.example.stockmeal.modelos.Produccion
import com.example.stockmeal.ui.state.AppUIState
import kotlinx.coroutines.launch
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
class DashboardViewModel(
    private val produccionRepository: ProduccionRepository,
    private val productoRepository: ProductoRepository
): ViewModel() {

    var state by mutableStateOf<AppUIState<List<Produccion>>>(AppUIState.Cargando)
        private set

    var numeroAlertas by mutableStateOf(0)
        private set

    init {
        obtenerDatosDashboard()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun obtenerDatosDashboard(){
        viewModelScope.launch {

            state = AppUIState.Cargando

            try {

                val fechaHoy = LocalDate.now().toString()

                val produccionHoy = produccionRepository.obtenerProduccionPorFecha(fechaHoy)
                val ingredientes = productoRepository.obtenerIngredientes()

                numeroAlertas = ingredientes.count() {
                    it.stockActual <= it.stockMinimo
                }

                state = AppUIState.Exito(produccionHoy)

            } catch (e: Exception) {
                e.printStackTrace()
                state = AppUIState.Error("Error cargando el dashboard")
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val aplicacion = (this[APPLICATION_KEY] as StockMealAplicacion)
                val produccionRepository = aplicacion.contenedorStockMeal.produccionRepository
                val productoRepository = aplicacion.contenedorStockMeal.productoRepository
                DashboardViewModel(
                    produccionRepository = produccionRepository,
                    productoRepository = productoRepository
                )
            }
        }
    }
}