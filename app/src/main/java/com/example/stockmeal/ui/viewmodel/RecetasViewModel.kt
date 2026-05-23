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
import com.example.stockmeal.datos.repositorios.ProduccionRepository
import com.example.stockmeal.datos.repositorios.ProductoRepository
import com.example.stockmeal.datos.repositorios.RecetaRepository
import com.example.stockmeal.modelos.Ingrediente
import com.example.stockmeal.modelos.Producto
import com.example.stockmeal.modelos.Receta
import com.example.stockmeal.modelos.RecetaDetalle
import com.example.stockmeal.ui.state.AppUIState
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

data class RecetaResumenUI(
    val receta: Receta,
    val ultimaProduccion: String,
    val unidadesPosibles: Int
)

data class RecetaDetalleUI(
    val idReceta: Int,
    val nombre: String,
    val ultimaProduccion: String,
    val unidadesPosibles: Int,
    val ingredientes: List<IngredienteDetalleUI>
)

data class IngredienteDetalleUI(
    val nombre: String,
    val cantidadNecesaria: Double,
    val stockActual: Int?,
    val unidad: String
) {
    val stockInsuficiente: Boolean
        get() = stockActual != null && stockActual < cantidadNecesaria
}

class RecetasViewModel(
    private val recetaRepository: RecetaRepository,
    private val produccionRepository: ProduccionRepository,
    private val productoRepository: ProductoRepository
) : ViewModel() {

    var recetasState by mutableStateOf<AppUIState<List<RecetaResumenUI>>>(AppUIState.Cargando)
        private set

    var recetaDetalleState by mutableStateOf<AppUIState<RecetaDetalleUI>>(AppUIState.Cargando)
        private set

    init {
        obtenerRecetas()
    }

    fun obtenerRecetas() {
        viewModelScope.launch {
            recetasState = AppUIState.Cargando
            try {
                val listaRecetas = recetaRepository.obtenerRecetas()
                val capacidades = recetaRepository.obtenerCapacidadProduccion()

                val recetasResumen = listaRecetas.map { receta ->
                    async {
                        val producciones = produccionRepository.obtenerProduccionPorPlato(receta.idReceta)
                        val fechaUltimaProduccion = producciones.maxByOrNull { it.fecha }?.fecha
                        val unidadesPosibles = capacidades
                            .firstOrNull { it.idProducto == receta.idReceta }
                            ?.unidadesPosibles
                            ?: 0

                        RecetaResumenUI(
                            receta = receta,
                            ultimaProduccion = textoUltimaProduccion(fechaUltimaProduccion),
                            unidadesPosibles = unidadesPosibles
                        )
                    }
                }.awaitAll()

                recetasState = AppUIState.Exito(recetasResumen)
            } catch (e: Exception) {
                e.printStackTrace()
                recetasState = AppUIState.Error("Error cargando las recetas")
            }
        }
    }

    fun obtenerRecetaDetalle(
        idReceta: Int
    ) {
        viewModelScope.launch {
            recetaDetalleState = AppUIState.Cargando
            try {
                val recetaDetalle = recetaRepository.obtenerRecetaPorId(idReceta)
                val capacidades = recetaRepository.obtenerCapacidadProduccion()
                val producciones = produccionRepository.obtenerProduccionPorPlato(idReceta)
                val productosIngredientes = productoRepository.obtenerIngredientes()

                recetaDetalleState = AppUIState.Exito(
                    recetaDetalle.toDetalleUI(
                        ultimaProduccion = textoUltimaProduccion(
                            producciones.maxByOrNull { it.fecha }?.fecha
                        ),
                        unidadesPosibles = capacidades
                            .firstOrNull { it.idProducto == idReceta }
                            ?.unidadesPosibles
                            ?: 0,
                        productosIngredientes = productosIngredientes
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
                recetaDetalleState = AppUIState.Error("Error cargando la receta seleccionada")
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val aplicacion = (this[APPLICATION_KEY] as StockMealAplicacion)
                val recetaRepository = aplicacion.contenedorStockMeal.recetaRepository
                val produccionRepository = aplicacion.contenedorStockMeal.produccionRepository
                val productoRepository = aplicacion.contenedorStockMeal.productoRepository
                RecetasViewModel(
                    recetaRepository = recetaRepository,
                    produccionRepository = produccionRepository,
                    productoRepository = productoRepository
                )
            }
        }
    }
}

private fun RecetaDetalle.toDetalleUI(
    ultimaProduccion: String,
    unidadesPosibles: Int,
    productosIngredientes: List<Producto>
): RecetaDetalleUI {
    return RecetaDetalleUI(
        idReceta = idReceta,
        nombre = nombre,
        ultimaProduccion = ultimaProduccion,
        unidadesPosibles = unidadesPosibles,
        ingredientes = ingredientes.map { ingrediente ->
            ingrediente.toDetalleUI(productosIngredientes)
        }
    )
}

private fun Ingrediente.toDetalleUI(
    productosIngredientes: List<Producto>
): IngredienteDetalleUI {
    val producto = productosIngredientes.firstOrNull {
        it.nombre.equals(nombre, ignoreCase = true)
    }

    return IngredienteDetalleUI(
        nombre = nombre,
        cantidadNecesaria = cantidad,
        stockActual = producto?.stockActual,
        unidad = unidad
    )
}

private fun textoUltimaProduccion(fecha: String?): String {
    if (fecha.isNullOrBlank()) {
        return "Sin producciones"
    }

    val formatoApi = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val fechaProduccion = formatoApi.parse(fecha) ?: return "Ultima vez el $fecha"
    val dias = diasEntre(fechaProduccion, Date())

    return when (dias) {
        0L -> "Ultima vez hoy"
        1L -> "Ultima vez ayer"
        in 2L..6L -> "Ultima vez hace $dias dias"
        else -> {
            val formatoPantalla = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            "Ultima vez el ${formatoPantalla.format(fechaProduccion)}"
        }
    }
}

private fun diasEntre(desde: Date, hasta: Date): Long {
    val formatoDia = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val inicioDiaDesde = formatoDia.parse(formatoDia.format(desde)) ?: desde
    val inicioDiaHasta = formatoDia.parse(formatoDia.format(hasta)) ?: hasta
    val diferencia = inicioDiaHasta.time - inicioDiaDesde.time
    return TimeUnit.MILLISECONDS.toDays(diferencia).coerceAtLeast(0)
}
