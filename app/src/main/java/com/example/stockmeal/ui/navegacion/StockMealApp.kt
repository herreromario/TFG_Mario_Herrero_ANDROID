package com.example.stockmeal.ui.navegacion

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Warehouse
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Warehouse
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.stockmeal.R
import com.example.stockmeal.modelos.BottomItem
import com.example.stockmeal.ui.pantallas.PantallaDashboard
import com.example.stockmeal.ui.pantallas.PantallaHistoricoProduccion
import com.example.stockmeal.ui.pantallas.PantallaRecetaDetalle
import com.example.stockmeal.ui.pantallas.PantallaRecetas
import com.example.stockmeal.ui.pantallas.PantallaRegistrarProduccion
import com.example.stockmeal.ui.pantallas.PantallaSeleccionarPlatoProduccion
import com.example.stockmeal.ui.pantallas.PRODUCCION_REGISTRADA
import com.example.stockmeal.ui.pantallas.Stock

enum class Pantallas(@StringRes val titulo: Int) {
    Dashboard(R.string.dashboard),
    RegistrarProduccion(R.string.registrar_produccion),
    SeleccionarPlatoProduccion(R.string.seleccionar_plato),
    HistoricoProduccion(R.string.historico_produccion),
    Recetas(R.string.recetas),
    RecetaDetalle(R.string.detalles_de_la_receta),
    Stock(R.string.stock)
}

val bottomItems = listOf(
    BottomItem(
        titulo = Pantallas.Dashboard.titulo,
        ruta = Pantallas.Dashboard.name,
        iconoSeleccionado = Icons.Filled.Dashboard,
        iconoNoSeleccionado = Icons.Outlined.Dashboard
    ),
    BottomItem(
        titulo = Pantallas.Recetas.titulo,
        ruta = Pantallas.Recetas.name,
        iconoSeleccionado = Icons.AutoMirrored.Filled.MenuBook,
        iconoNoSeleccionado = Icons.AutoMirrored.Outlined.MenuBook
    ),
    BottomItem(
        titulo = Pantallas.Stock.titulo,
        ruta = Pantallas.Stock.name,
        iconoSeleccionado = Icons.Filled.Warehouse,
        iconoNoSeleccionado = Icons.Outlined.Warehouse
    )
)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StockMealApp() {

    val navController = rememberNavController()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val rutaActual = backStackEntry?.destination?.route ?: Pantallas.Dashboard.name
    val rutaBase = rutaActual.substringBefore("/")
    val pantallaActual = Pantallas.valueOf(rutaBase)
    val esPantallaPrincipal = bottomItems.any { it.ruta == rutaBase }
    val puedeNavegarAtras = !esPantallaPrincipal && navController.previousBackStackEntry != null

    Scaffold(

        topBar = {
            AppTopBar(
                pantallaActual = pantallaActual,
                puedeNavegarAtras = puedeNavegarAtras,
                onNavegarAtras = {
                    navController.popBackStack()
                }
            )
        },

        bottomBar = {
            NavigationBar {
                bottomItems.forEach { item ->

                    val selected = rutaBase == item.ruta

                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(item.ruta) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector =
                                    if (selected) item.iconoSeleccionado
                                    else item.iconoNoSeleccionado,
                                contentDescription = null
                            )
                        },
                        label = {
                            Text(stringResource(item.titulo))
                        }
                    )
                }
            }
        }

    ) { padding ->

        NavHost(
            navController = navController,
            startDestination = Pantallas.Dashboard.name,
            modifier = Modifier.padding(padding)
        ) {

            composable(Pantallas.Dashboard.name) { dashboardBackStackEntry ->
                val refrescarProduccion by dashboardBackStackEntry.savedStateHandle
                    .getStateFlow(PRODUCCION_REGISTRADA, false)
                    .collectAsState()

                PantallaDashboard(
                    refrescarProduccion = refrescarProduccion,
                    onRefrescoProduccionConsumido = {
                        dashboardBackStackEntry.savedStateHandle[PRODUCCION_REGISTRADA] = false
                    },
                    onVerAlertas = {
                        navController.navigate("${Pantallas.Stock.name}/true") {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onRegistrarProduccion = {
                        navController.navigate(Pantallas.RegistrarProduccion.name)
                    },
                    onVerHistorico = {
                        navController.navigate(Pantallas.HistoricoProduccion.name)
                    }
                )
            }


            composable(Pantallas.RegistrarProduccion.name) {
                PantallaRegistrarProduccion(navController)
            }

            composable(Pantallas.SeleccionarPlatoProduccion.name) {
                PantallaSeleccionarPlatoProduccion(navController)
            }

            composable(Pantallas.HistoricoProduccion.name) {
                PantallaHistoricoProduccion()
            }

            composable(Pantallas.Recetas.name) {
                PantallaRecetas(
                    onDetallesReceta = { id ->
                        navController.navigate("${Pantallas.RecetaDetalle.name}/$id")
                    }
                )
            }

            composable("${Pantallas.RecetaDetalle.name}/{id}") { backStackEntry ->

                val id = backStackEntry.arguments
                    ?.getString("id")
                    ?.toIntOrNull()

                if (id != null) {
                    PantallaRecetaDetalle(idReceta = id)
                }
            }

            composable(Pantallas.Stock.name) {
                Stock(
                    navController = navController,
                    filtrarAlertasInicialmente = false
                )
            }

            composable("${Pantallas.Stock.name}/{filtrarAlertas}") { backStackEntry ->
                val filtrarAlertas = backStackEntry.arguments
                    ?.getString("filtrarAlertas")
                    ?.toBooleanStrictOrNull()
                    ?: false

                Stock(
                    navController = navController,
                    filtrarAlertasInicialmente = filtrarAlertas
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    pantallaActual: Pantallas,
    puedeNavegarAtras: Boolean,
    onNavegarAtras: () -> Unit
) {
    CenterAlignedTopAppBar(
        navigationIcon = {
            if (puedeNavegarAtras) {
                IconButton(onClick = onNavegarAtras) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null
                    )
                }
            }
        },
        title = {
            Text(stringResource(pantallaActual.titulo))
        }
    )
}



