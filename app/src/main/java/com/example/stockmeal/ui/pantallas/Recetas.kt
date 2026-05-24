package com.example.stockmeal.ui.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stockmeal.R
import com.example.stockmeal.ui.state.AppUIState
import com.example.stockmeal.ui.viewmodel.RecetaResumenUI
import com.example.stockmeal.ui.viewmodel.RecetasViewModel

@Composable
fun PantallaRecetas(
    viewModel: RecetasViewModel = viewModel(factory = RecetasViewModel.Factory),
    onDetallesReceta: (Int) -> Unit
) {
    val recetasState = viewModel.recetasState

    when (recetasState) {
        is AppUIState.Error -> PantallaError()
        is AppUIState.Cargando -> PantallaCargando()
        is AppUIState.Exito -> PantallaRecetasExito(
            listaRecetas = recetasState.datos,
            onDetallesReceta = onDetallesReceta
        )
    }
}

@Composable
fun PantallaRecetasExito(
    listaRecetas: List<RecetaResumenUI>,
    onDetallesReceta: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        Text(
            text = stringResource(R.string.recetas),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(12.dp)
        )

        LazyColumn {
            items(listaRecetas) { recetaResumen ->
                TarjetaReceta(
                    recetaResumen = recetaResumen,
                    onDetallesReceta = onDetallesReceta
                )
            }
        }
    }
}

@Composable
fun TarjetaReceta(
    recetaResumen: RecetaResumenUI,
    onDetallesReceta: (Int) -> Unit
) {
    val receta = recetaResumen.receta
    val estadoCocinado = estadoCocinado(recetaResumen.unidadesPosibles)

    TarjetaBase(
        onClick = { onDetallesReceta(receta.idReceta) },
        leading = {
            Icon(
                imageVector = Icons.Filled.Book,
                contentDescription = null,
                tint = Color(0xFF1565C0),
                modifier = Modifier.size(30.dp)
            )
        },
        contentLeft = {
            Text(
                text = receta.nombre,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = recetaResumen.ultimaProduccion,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        contentRight = {
            IndicadorCocinado(
                texto = estadoCocinado.texto,
                color = estadoCocinado.color
            )
        }
    )
}

@Composable
fun IndicadorCocinado(
    texto: String,
    color: Color
) {
    Row(
        modifier = Modifier.width(108.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(
                    color = color,
                    shape = CircleShape
                )
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = texto,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = color
        )
    }
}

data class EstadoCocinado(
    val texto: String,
    val color: Color
)

fun estadoCocinado(unidadesPosibles: Int): EstadoCocinado {
    return when {
        unidadesPosibles <= 0 -> EstadoCocinado(
            texto = "Sin stock",
            color = Color(0xFFC62828)
        )
        unidadesPosibles <= 5 -> EstadoCocinado(
            texto = "$unidadesPosibles raciones",
            color = Color(0xFFEF6C00)
        )
        else -> EstadoCocinado(
            texto = "Disponible",
            color = Color(0xFF2E7D32)
        )
    }
}
