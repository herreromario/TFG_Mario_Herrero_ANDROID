package com.example.stockmeal.ui.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cookie
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.stockmeal.R
import com.example.stockmeal.ui.state.AppUIState
import com.example.stockmeal.ui.viewmodel.IngredienteStockUI
import com.example.stockmeal.ui.viewmodel.StockViewModel

@Composable
fun Stock(
    navController: NavController,
    filtrarAlertasInicialmente: Boolean = false,
    viewModel: StockViewModel = viewModel(factory = StockViewModel.Factory)
) {
    val stockState = viewModel.stockState

    when (stockState) {
        is AppUIState.Error -> PantallaError()
        is AppUIState.Cargando -> PantallaCargando()
        is AppUIState.Exito -> PantallaStockExito(
            listaIngredientes = stockState.datos,
            filtrarAlertasInicialmente = filtrarAlertasInicialmente
        )
    }
}

@Composable
fun PantallaStockExito(
    listaIngredientes: List<IngredienteStockUI>,
    filtrarAlertasInicialmente: Boolean = false
) {
    var mostrarSoloAlertas by remember(filtrarAlertasInicialmente) {
        mutableStateOf(filtrarAlertasInicialmente)
    }
    val ingredientesFiltrados = if (mostrarSoloAlertas) {
        listaIngredientes.filter { it.enAlerta }
    } else {
        listaIngredientes
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.stock),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
            )

            FilterChip(
                selected = mostrarSoloAlertas,
                onClick = { mostrarSoloAlertas = !mostrarSoloAlertas },
                label = { Text("Alertas") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Warning,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            )
        }

        if (ingredientesFiltrados.isEmpty()) {
            Text(
                text = if (mostrarSoloAlertas) {
                    "No hay ingredientes en alerta"
                } else {
                    stringResource(R.string.no_se_han_encontrado_registros)
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(12.dp)
            )
        } else {
            LazyColumn {
                items(ingredientesFiltrados) { ingredienteStock ->
                    TarjetaIngredienteStock(ingredienteStock)
                }
            }
        }
    }
}

@Composable
fun TarjetaIngredienteStock(
    ingredienteStock: IngredienteStockUI
) {
    val producto = ingredienteStock.producto
    val colorStock = colorStockPorProgreso(ingredienteStock.progresoExistencias)
    val colorFondo = if (ingredienteStock.enAlerta) {
        Color(0xFFFFEBEE)
    } else {
        MaterialTheme.colorScheme.surface
    }

    TarjetaBase(
        containerColor = colorFondo,
        leading = {
            Icon(
                imageVector = Icons.Filled.Cookie,
                contentDescription = null,
                tint = colorStock,
                modifier = Modifier.size(30.dp)
            )
        },
        contentLeft = {
            Text(
                text = producto.nombre,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = if (ingredienteStock.enAlerta) colorStock else MaterialTheme.colorScheme.onSurface
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, end = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${producto.stockMinimo} ${producto.unidad}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = colorStock,
                    modifier = Modifier.width(72.dp)
                )

                Spacer(modifier = Modifier.width(10.dp))

                BarraStock(
                    progreso = ingredienteStock.progresoExistencias,
                    color = colorStock
                )
            }
        },
        contentRight = {
            Column(
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = producto.stockActual.toString(),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = colorStock
                )
            }
        }
    )
}

@Composable
fun BarraStock(
    progreso: Float,
    color: Color
) {
    val shape = RoundedCornerShape(3.dp)

    Box(
        modifier = Modifier
            .width(150.dp)
            .height(12.dp)
            .clip(shape)
            .background(color.copy(alpha = 0.14f))
            .border(
                width = 1.dp,
                color = color.copy(alpha = 0.55f),
                shape = shape
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(progreso.coerceIn(0f, 1f))
                .background(color)
        )
    }
}

private fun colorStockPorProgreso(progreso: Float): Color {
    val rojo = Color(0xFFC62828)
    val naranja = Color(0xFFEF6C00)
    val amarillo = Color(0xFFF9A825)
    val verde = Color(0xFF2E7D32)

    return when {
        progreso >= 0.75f -> verde
        progreso >= 0.50f -> lerp(amarillo, verde, (progreso - 0.50f) / 0.25f)
        progreso >= 0.25f -> lerp(naranja, amarillo, (progreso - 0.25f) / 0.25f)
        else -> lerp(rojo, naranja, progreso / 0.25f)
    }
}
