package com.example.stockmeal.ui.pantallas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stockmeal.ui.state.AppUIState
import com.example.stockmeal.ui.viewmodel.IngredienteDetalleUI
import com.example.stockmeal.ui.viewmodel.RecetaDetalleUI
import com.example.stockmeal.ui.viewmodel.RecetasViewModel

@Composable
fun PantallaRecetaDetalle(
    idReceta: Int,
    viewModel: RecetasViewModel = viewModel(factory = RecetasViewModel.Factory)
) {
    val recetaDetalleState = viewModel.recetaDetalleState

    LaunchedEffect(idReceta) {
        viewModel.obtenerRecetaDetalle(idReceta)
    }

    when (recetaDetalleState) {
        is AppUIState.Cargando -> PantallaCargando()
        is AppUIState.Error -> PantallaError()
        is AppUIState.Exito -> PantallaRecetaDetalleExito(
            recetaDetalle = recetaDetalleState.datos
        )
    }
}

@Composable
fun PantallaRecetaDetalleExito(
    recetaDetalle: RecetaDetalleUI
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        HeaderReceta(recetaDetalle)

        ListaIngredientesTicket(recetaDetalle.ingredientes)
    }
}

@Composable
fun HeaderReceta(
    receta: RecetaDetalleUI
) {
    val estado = estadoCocinado(receta.unidadesPosibles)

    TarjetaBase(
        leading = {
            Icon(
                imageVector = Icons.Default.Book,
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
                text = receta.ultimaProduccion,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        contentRight = {
            IndicadorCocinado(
                texto = estado.texto,
                color = estado.color
            )
        }
    )
}

@Composable
fun ListaIngredientesTicket(
    ingredientes: List<IngredienteDetalleUI>
) {
    Column(modifier = Modifier.padding(horizontal = 12.dp)) {
        Text(
            text = "Ingredientes",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        LazyColumn {
            items(ingredientes) { ingrediente ->
                ItemIngredienteTicket(ingrediente)
            }
        }
    }
}

@Composable
fun ItemIngredienteTicket(
    ingrediente: IngredienteDetalleUI
) {
    val colorStock = if (ingrediente.stockInsuficiente) {
        Color(0xFFC62828)
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Column {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = ingrediente.nombre,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = "Stock actual: ${ingrediente.stockActual ?: "-"} ${ingrediente.unidad}",
                    style = MaterialTheme.typography.bodySmall,
                    color = colorStock
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${formatCantidad(ingrediente.cantidadNecesaria)} ${ingrediente.unidad}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = colorStock
                )
            }
        }

        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant,
            thickness = 0.5.dp
        )
    }
}

private fun formatCantidad(cantidad: Double): String {
    return if (cantidad % 1.0 == 0.0) {
        cantidad.toInt().toString()
    } else {
        cantidad.toString()
    }
}

