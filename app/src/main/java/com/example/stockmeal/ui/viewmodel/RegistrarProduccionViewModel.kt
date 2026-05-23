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
import com.example.stockmeal.modelos.CapacidadProduccion
import com.example.stockmeal.modelos.Produccion
import com.example.stockmeal.modelos.ProduccionRequest
import com.example.stockmeal.modelos.Producto
import com.example.stockmeal.ui.state.AppUIState
import kotlinx.serialization.SerializationException
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class RegistrarProduccionFormState(
    val idProductoSeleccionado: Int? = null,
    val cantidad: String = "1",
    val fecha: String = fechaHoy(),
    val mensajeError: String? = null
)

class RegistrarProduccionViewModel(
    private val produccionRepository: ProduccionRepository,
    private val productoRepository: ProductoRepository,
    private val recetaRepository: RecetaRepository
) : ViewModel() {

    var platosState by mutableStateOf<AppUIState<List<Producto>>>(AppUIState.Cargando)
        private set

    var capacidadState by mutableStateOf<AppUIState<List<CapacidadProduccion>>>(AppUIState.Cargando)
        private set

    var formState by mutableStateOf(RegistrarProduccionFormState())
        private set

    var registroState by mutableStateOf<AppUIState<Produccion>?>(null)
        private set

    init {
        obtenerPlatos()
        obtenerCapacidadProduccion()
    }

    fun obtenerPlatos() {
        viewModelScope.launch {
            platosState = AppUIState.Cargando
            try {
                val platos = productoRepository.obtenerPlatos()
                platosState = AppUIState.Exito(platos)
            } catch (e: Exception) {
                e.printStackTrace()
                platosState = AppUIState.Error("Error cargando los platos")
            }
        }
    }

    fun obtenerCapacidadProduccion() {
        viewModelScope.launch {
            capacidadState = AppUIState.Cargando
            try {
                capacidadState = AppUIState.Exito(recetaRepository.obtenerCapacidadProduccion())
                ajustarCantidadAlMaximo()
            } catch (e: Exception) {
                e.printStackTrace()
                capacidadState = AppUIState.Error("Error cargando la capacidad de produccion")
            }
        }
    }

    fun seleccionarProducto(idProducto: Int) {
        val maximo = maximoUnidades(idProducto)
        val cantidad = when {
            maximo == null -> formState.cantidad
            maximo <= 0 -> "0"
            else -> (formState.cantidad.toIntOrNull() ?: 1).coerceIn(1, maximo).toString()
        }

        formState = formState.copy(
            idProductoSeleccionado = idProducto,
            cantidad = cantidad,
            mensajeError = null
        )
    }

    fun actualizarCantidad(cantidad: String) {
        val cantidadNumerica = cantidad.filter { it.isDigit() }.toIntOrNull()
        val cantidadAjustada = ajustarCantidadPermitida(cantidadNumerica)

        formState = formState.copy(
            cantidad = cantidadAjustada,
            mensajeError = null
        )
    }

    fun incrementarCantidad() {
        val cantidadActual = formState.cantidad.toIntOrNull() ?: 1
        val cantidadAjustada = ajustarCantidadPermitida(cantidadActual + 1)

        formState = formState.copy(
            cantidad = cantidadAjustada,
            mensajeError = null
        )
    }

    fun decrementarCantidad() {
        val cantidadActual = formState.cantidad.toIntOrNull() ?: 1
        val cantidadAjustada = ajustarCantidadPermitida(cantidadActual - 1)

        formState = formState.copy(
            cantidad = cantidadAjustada,
            mensajeError = null
        )
    }

    fun registrarProduccion() {
        val idProducto = formState.idProductoSeleccionado
        val cantidad = formState.cantidad.toIntOrNull()

        if (idProducto == null) {
            formState = formState.copy(mensajeError = "Selecciona un plato")
            return
        }

        if (cantidad == null || cantidad <= 0) {
            formState = formState.copy(mensajeError = "Introduce una cantidad valida")
            return
        }

        val maximo = maximoUnidades(idProducto)
        if (maximo == null) {
            formState = formState.copy(mensajeError = "Espera a que se calcule la capacidad disponible")
            return
        }

        if (maximo <= 0) {
            formState = formState.copy(mensajeError = "No hay stock suficiente para producir este plato")
            return
        }

        if (cantidad > maximo) {
            formState = formState.copy(mensajeError = "La cantidad maxima para este plato es $maximo")
            return
        }

        if (formState.fecha.isBlank()) {
            formState = formState.copy(mensajeError = "Introduce una fecha")
            return
        }

        viewModelScope.launch {
            registroState = AppUIState.Cargando
            try {
                val produccion = produccionRepository.registrarProduccion(
                    ProduccionRequest(
                        idProducto = idProducto,
                        cantidad = cantidad,
                        fecha = formState.fecha
                    )
                )

                registroState = AppUIState.Exito(produccion)
                formState = RegistrarProduccionFormState(fecha = formState.fecha)
                obtenerCapacidadProduccion()
            } catch (e: Exception) {
                e.printStackTrace()
                registroState = AppUIState.Error(mensajeErrorRegistro(e))
            }
        }
    }

    fun limpiarResultadoRegistro() {
        registroState = null
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val aplicacion = (this[APPLICATION_KEY] as StockMealAplicacion)
                val produccionRepository = aplicacion.contenedorStockMeal.produccionRepository
                val productoRepository = aplicacion.contenedorStockMeal.productoRepository
                val recetaRepository = aplicacion.contenedorStockMeal.recetaRepository
                RegistrarProduccionViewModel(
                    produccionRepository = produccionRepository,
                    productoRepository = productoRepository,
                    recetaRepository = recetaRepository
                )
            }
        }
    }

    private fun maximoUnidades(idProducto: Int): Int? {
        val capacidades = (capacidadState as? AppUIState.Exito)?.datos ?: return null
        return capacidades.firstOrNull { it.idProducto == idProducto }?.unidadesPosibles ?: 0
    }

    private fun ajustarCantidadPermitida(cantidad: Int?): String {
        val maximo = formState.idProductoSeleccionado?.let { maximoUnidades(it) }
        val valor = cantidad ?: 0

        return when {
            maximo == null -> valor.coerceAtLeast(1).toString()
            maximo <= 0 -> "0"
            else -> valor.coerceIn(1, maximo).toString()
        }
    }

    private fun ajustarCantidadAlMaximo() {
        val idProducto = formState.idProductoSeleccionado ?: return
        formState = formState.copy(
            cantidad = ajustarCantidadPermitida(formState.cantidad.toIntOrNull()),
            mensajeError = null
        )
    }
}

private fun mensajeErrorRegistro(e: Exception): String {
    return when (e) {
        is HttpException -> {
            val errorBackend = e.response()?.errorBody()?.string()
            if (errorBackend.isNullOrBlank()) {
                "Error registrando la produccion: HTTP ${e.code()}"
            } else {
                "Error registrando la produccion: HTTP ${e.code()} - $errorBackend"
            }
        }

        is IOException -> "Error registrando la produccion: no se pudo conectar con el servidor"
        is SerializationException -> "Error registrando la produccion: respuesta o request JSON no valido"
        else -> "Error registrando la produccion: ${e.message ?: e::class.simpleName}"
    }
}

private fun fechaHoy(): String {
    return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
}
