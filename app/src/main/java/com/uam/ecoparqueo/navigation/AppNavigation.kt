package com.uam.ecoparqueo.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.uam.ecoparqueo.ui.screens.estudiante.RegistroVehicular
import com.uam.ecoparqueo.ui.screens.login.LoginScreen
import com.uam.ecoparqueo.ui.screens.seguridad.ControlAccesoVehicular
import com.uam.ecoparqueo.ui.screens.seguridad.SeleccionSeguridad
import kotlinx.serialization.Serializable

@Serializable
object LoginScreen // Primera pantalla: Login


@Serializable
object RegistroVehicularScreen // Pantalla para Estudiante

@Serializable
object SeleccionSeguridadScreen // Pantalla 1 del Guarda

@Serializable
data class ControlAccesoVehicularScreen(
    val nombreParqueo: String // Pasa el nombre del parqueo seleccionado
)

@Composable
fun AppNavigation(modifier: Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = LoginScreen, // Primera pantalla: Login
        modifier = modifier
    ) {
        // Pantalla de Login
        composable<LoginScreen> {
            LoginScreen(
                onLoginSuccess = { tipo ->
                    if (tipo == "Estudiante") {
                        navController.navigate(RegistroVehicularScreen)
                    } else {
                        navController.navigate(SeleccionSeguridadScreen)
                    }
                }
            )
        }

        // Pantalla para Estudiante: Registro Vehicular
        composable<RegistroVehicularScreen> {
            RegistroVehicular()
        }

        // Pantalla 1: Selección de Parqueo para Guarda
        composable<SeleccionSeguridadScreen> {
            SeleccionSeguridad(
                onParkingSelected = { nombre ->
                    // Navega pasando el nombre del parqueo seleccionado como argumento
                    navController.navigate(ControlAccesoVehicularScreen(nombre))
                }
            )
        }

        // Pantalla 2: Registro de Placas (Control de Acceso)
        composable<ControlAccesoVehicularScreen> { backStackEntry ->
            // Extrae el argumento usando toRoute() como en tu ejemplo de clase
            val args = backStackEntry.toRoute<ControlAccesoVehicularScreen>()

            ControlAccesoVehicular(
                nombreParqueo = args.nombreParqueo,
                onBack = { navController.popBackStack() }
            )
        }
    }
}