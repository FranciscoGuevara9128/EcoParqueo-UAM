package com.uam.ecoparqueo.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.uam.ecoparqueo.screen.estudiante.GestionVehiculosScreen
import com.uam.ecoparqueo.screen.estudiante.MostrarParqueoScreen
import com.uam.ecoparqueo.screen.estudiante.RegistroVehicularScreen
import com.uam.ecoparqueo.screen.estudiante.SeleccionParqueoScreen
import com.uam.ecoparqueo.screen.login.LoginScreen
import com.uam.ecoparqueo.screen.seguridad.ControlAccesoVehicularScreen
import com.uam.ecoparqueo.screen.seguridad.EstadisticasScreen
import com.uam.ecoparqueo.screen.seguridad.SeleccionSeguridadScreen
import kotlinx.serialization.Serializable
import com.uam.ecoparqueo.screen.admin.AdminParqueoScreen

@Serializable object LoginRoute
@Serializable object DashboardEstudianteRoute
@Serializable object DashboardGuardaRoute
@Serializable object RegistroVehicularRoute
@Serializable object SeleccionParqueoRoute
@Serializable data class MostrarParqueoRoute(val nombreParqueo: String, val direccion: String, val disponibles: Int)
@Serializable object SeleccionSeguridadRoute
@Serializable data class ControlAccesoVehicularRoute(val nombreParqueo: String)
@Serializable object GestionVehiculosRoute
@Serializable object EstadisticasRoute
@Serializable object AdminParqueoRoute
@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = LoginRoute, modifier = modifier) {

        composable<LoginRoute> {
            LoginScreen(
                onLoginSuccess = { tipo ->
                    if (tipo == "Estudiante") {
                        navController.navigate(DashboardEstudianteRoute) { popUpTo(LoginRoute) { inclusive = true } }
                    } else {
                        navController.navigate(DashboardGuardaRoute) { popUpTo(LoginRoute) { inclusive = true } }
                    }
                }
            )
        }

        composable<DashboardEstudianteRoute> {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Text("Panel de Estudiante", style = MaterialTheme.typography.headlineMedium)
                Button(onClick = { navController.navigate(GestionVehiculosRoute) }, modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
                    Text("Mis Vehículos")
                }
                Button(onClick = { navController.navigate(SeleccionParqueoRoute) }, modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
                    Text("Buscar Parqueo")
                }
            }
        }

        composable<DashboardGuardaRoute> {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Panel de Guarda", style = MaterialTheme.typography.headlineMedium)
                Button(
                    onClick = { navController.navigate(SeleccionSeguridadRoute) },
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                ) {
                    Text("Control de Acceso")
                }
                Button(
                    onClick = { navController.navigate(EstadisticasRoute) },
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                ) {
                    Text("Estadísticas del Día")
                }
                // Nuevo botón para registrar parqueos
                Button(
                    onClick = { navController.navigate(AdminParqueoRoute) },
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                ) {
                    Text("Registrar Parqueo")
                }
            }
        }

        composable<GestionVehiculosRoute> {
            GestionVehiculosScreen(onVolver = { navController.popBackStack() })
        }

        composable<RegistroVehicularRoute> {
            RegistroVehicularScreen(onRegistroExitoso = { navController.navigate(SeleccionParqueoRoute) })
        }

        composable<SeleccionParqueoRoute> {
            SeleccionParqueoScreen(
                tab = 2,
                onIrAlParqueo = { nombre, direccion, disponibles ->
                    navController.navigate(MostrarParqueoRoute(nombre, direccion, disponibles))
                }
            )
        }

        composable<MostrarParqueoRoute> { backStackEntry ->
            val args = backStackEntry.toRoute<MostrarParqueoRoute>()
            MostrarParqueoScreen(args.nombreParqueo, args.direccion, args.disponibles, onVolver = { navController.popBackStack() })
        }

        composable<SeleccionSeguridadRoute> {
            SeleccionSeguridadScreen(onParkingSelected = { nombre -> navController.navigate(ControlAccesoVehicularRoute(nombre)) })
        }

        composable<ControlAccesoVehicularRoute> { backStackEntry ->
            val args = backStackEntry.toRoute<ControlAccesoVehicularRoute>()
            ControlAccesoVehicularScreen(args.nombreParqueo, onBack = { navController.popBackStack() })
        }

        composable<EstadisticasRoute> {
            EstadisticasScreen(onVolver = { navController.popBackStack() })
        }

        composable<AdminParqueoRoute> {
            AdminParqueoScreen(onVolver = { navController.popBackStack() })
        }
    }
}