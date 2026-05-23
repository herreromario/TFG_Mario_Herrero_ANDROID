package com.example.stockmeal.ui.pantallas

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stockmeal.R
import com.example.stockmeal.modelos.Produccion
import com.example.stockmeal.ui.state.AppUIState
import com.example.stockmeal.ui.viewmodel.HistoricoProduccionViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PantallaHistoricoProduccion(
    viewModel: HistoricoProduccionViewModel = viewModel(factory = HistoricoProduccionViewModel.Factory)
) {
    val state = viewModel.state

    when (state) {
        is AppUIState.Error -> PantallaError()
        is AppUIState.Cargando -> PantallaCargando()
        is AppUIState.Exito -> PantallaHistoricoExito(produccionesPorDia = state.datos)
    }
}

@Composable
fun PantallaHistoricoExito(
    produccionesPorDia: Map<String, List<Produccion>>
) {
    if (produccionesPorDia.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(stringResource(R.string.no_se_han_encontrado_registros))
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            produccionesPorDia.forEach { (fecha, producciones) ->
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = formatearFechaHistorico(fecha),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }
                items(producciones) { produccion ->
                    TarjetaProduccion(produccion)
                }
            }
        }
    }
}

private fun formatearFechaHistorico(fecha: String): String {
    val formatoApi = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val fechaDate = formatoApi.parse(fecha) ?: return fecha
    val dias = diasEntreHistorico(fechaDate, Date())

    return when (dias) {
        0L -> "Hoy"
        1L -> "Ayer"
        in 2L..6L -> "Hace $dias días"
        else -> {
            val formatoPantalla = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            formatoPantalla.format(fechaDate)
        }
    }
}

private fun diasEntreHistorico(desde: Date, hasta: Date): Long {
    val formatoDia = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val inicioDiaDesde = formatoDia.parse(formatoDia.format(desde)) ?: desde
    val inicioDiaHasta = formatoDia.parse(formatoDia.format(hasta)) ?: hasta
    val diferencia = inicioDiaHasta.time - inicioDiaDesde.time
    return TimeUnit.MILLISECONDS.toDays(diferencia).coerceAtLeast(0)
}
