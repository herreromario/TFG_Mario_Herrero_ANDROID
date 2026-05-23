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
import com.example.stockmeal.modelos.Produccion
import com.example.stockmeal.ui.state.AppUIState
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
class HistoricoProduccionViewModel(
    private val produccionRepository: ProduccionRepository
) : ViewModel() {

    var state by mutableStateOf<AppUIState<Map<String, List<Produccion>>>>(AppUIState.Cargando)
        private set

    init {
        obtenerHistorico()
    }

    fun obtenerHistorico() {
        viewModelScope.launch {
            state = AppUIState.Cargando
            try {
                val producciones = produccionRepository.obtenerProduccion()
                val agrupadas = producciones.groupBy { it.fecha }.toSortedMap(compareByDescending { it })
                state = AppUIState.Exito(agrupadas)
            } catch (e: Exception) {
                e.printStackTrace()
                state = AppUIState.Error("Error cargando el histórico")
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val aplicacion = (this[APPLICATION_KEY] as StockMealAplicacion)
                val produccionRepository = aplicacion.contenedorStockMeal.produccionRepository
                HistoricoProduccionViewModel(produccionRepository = produccionRepository)
            }
        }
    }
}
