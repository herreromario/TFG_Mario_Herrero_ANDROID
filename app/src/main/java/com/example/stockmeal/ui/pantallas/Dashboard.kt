package com.example.stockmeal.ui.pantallas

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Dining
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.stockmeal.R
import com.example.stockmeal.modelos.Produccion
import com.example.stockmeal.ui.navegacion.Pantallas
import com.example.stockmeal.ui.state.AppUIState
import com.example.stockmeal.ui.viewmodel.DashboardViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PantallaDashboard(
    viewModel: DashboardViewModel = viewModel(factory = DashboardViewModel.Factory),
    refrescarProduccion: Boolean = false,
    onRefrescoProduccionConsumido: () -> Unit = {},
    onVerAlertas: () -> Unit,
    onRegistrarProduccion: () -> Unit,
    onVerHistorico: () -> Unit
) {
    val state = viewModel.state

    LaunchedEffect(refrescarProduccion) {
        if (refrescarProduccion) {
            viewModel.obtenerDatosDashboard()
            onRefrescoProduccionConsumido()
        }
    }

    when (state) {

        is AppUIState.Error -> PantallaError()
        is AppUIState.Cargando -> PantallaCargando()
        is AppUIState.Exito -> PantallaDashboardExito(
            listaProduccion = state.datos,
            alertas = viewModel.numeroAlertas,
            onVerAlertas = onVerAlertas,
            onRegistrarProduccion = onRegistrarProduccion,
            onVerHistorico = onVerHistorico
        )
    }
}

@Composable
fun PantallaDashboardExito(
    listaProduccion: List<Produccion>,
    alertas: Int,
    onVerAlertas: () -> Unit,
    onRegistrarProduccion: () -> Unit,
    onVerHistorico: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {

        // PRODUCCIÓN
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.produccion_de_hoy),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Button(onClick = onVerHistorico) {
                    Icon(
                        imageVector = Icons.Filled.History,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = stringResource(R.string.ver_historico))
                }
            }

            if (listaProduccion.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(stringResource(R.string.no_se_han_encontrado_registros))
                }
            } else {
                LazyColumn {
                    items(listaProduccion) { produccion ->
                        TarjetaProduccion(produccion)
                    }
                }
            }
        }

        // ALERTAS
        if (alertas > 0) {
            Spacer(modifier = Modifier.height(8.dp))

            TarjetaAlertaStock(
                alertas = alertas,
                onClick = { onVerAlertas() }
            )
        }
        }

        FloatingActionButton(
            onClick = onRegistrarProduccion,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(
                    end = 24.dp,
                    bottom = if (alertas > 0) 124.dp else 24.dp
                )
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = null
            )
        }
    }
}

@Composable
fun TarjetaBase(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,

    // SLOT IZQUIERDA
    contentLeft: @Composable ColumnScope.() -> Unit,

    // SLOT DERECHA
    contentRight: @Composable ColumnScope.() -> Unit,

    // SLOT OPCIONAL (icono a la izquierda)
    leading: (@Composable () -> Unit)? = null,

    // COLOR DE FONDO
    containerColor: Color = MaterialTheme.colorScheme.surface
) {

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp,
            pressedElevation = 6.dp,

        ),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        onClick = onClick ?: {}
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // ICONO (opcional)
            if (leading != null) {
                leading()
                Spacer(modifier = Modifier.width(12.dp))
            }

            // IZQUIERDA
            Column(
                modifier = Modifier.weight(1f)
            ) {
                contentLeft()
            }

            // DERECHA
            Column(
                horizontalAlignment = Alignment.End
            ) {
                contentRight()
            }
        }
    }
}

@Composable
fun TarjetaProduccion(
    produccion: Produccion
) {

    TarjetaBase(

        // ICONO
        leading = {
            Icon(
                imageVector = Icons.Filled.Dining,
                contentDescription = null,
                tint = Color(0xFF2E7D32),
                modifier = Modifier.size(30.dp)
            )
        },

        // TEXTO IZQUIERDA
        contentLeft = {

            // TÍTULO
            Text(
                text = produccion.plato,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            // ETIQUETA
            Text(
                text = "Platos preparados",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },


        // TEXTO DERECHA
        contentRight = {

            // TÍTULO
            Text(
                text = produccion.cantidad.toString(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E7D32)
            )

            // ETIQUETA
            Text(
                text = "PLATOS",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    )
}

@Composable
fun TarjetaAlertaStock(
    alertas: Int,
    onClick: () -> Unit
) {

    TarjetaBase(
        onClick = onClick,

        containerColor = Color(0xFFFFEBEE),

        // ICONO
        leading = {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = Color(0xFFC62828),
                modifier = Modifier.size(30.dp)
            )
        },

        // TEXTO IZQUIERDA
        contentLeft = {

            // TÍTULO
            Text(
                text = "Ingredientes en alerta",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFFC62828)
            )

            // ETIQUETA
            Text(
                text = "Requieren reposición",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFFC62828)
            )
        },

        // TEXTO DERECHA
        contentRight = {

            // TÍTULO
            Text(
                text = alertas.toString(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFC62828)
            )

            // ETIQUETA
            Text(
                text = "ITEMS",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFFC62828)
            )
        }
    )
}
