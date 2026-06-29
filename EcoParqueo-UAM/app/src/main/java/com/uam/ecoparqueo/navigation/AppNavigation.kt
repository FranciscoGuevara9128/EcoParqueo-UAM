package com.uam.ecoparqueo.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import com.uam.ecoparqueo.Graph
import com.uam.ecoparqueo.FALLBACK_USER_ID
import com.uam.ecoparqueo.screen.estudiante.DashboardEstudianteScreen
import com.uam.ecoparqueo.screen.estudiante.GestionVehiculosScreen
import com.uam.ecoparqueo.screen.estudiante.MostrarParqueoScreen
import com.uam.ecoparqueo.screen.estudiante.RegistroVehicularScreen
import com.uam.ecoparqueo.screen.estudiante.SeleccionParqueoScreen
import com.uam.ecoparqueo.screen.login.LoginScreen
import com.uam.ecoparqueo.screen.login.RegistroUsuarioScreen
import com.uam.ecoparqueo.screen.seguridad.DashboardGuardaScreen
import com.uam.ecoparqueo.screen.seguridad.ControlAccesoVehicularScreen
import com.uam.ecoparqueo.screen.seguridad.EstadisticasScreen
import com.uam.ecoparqueo.screen.seguridad.SeleccionSeguridadScreen
import kotlinx.serialization.Serializable
import com.uam.ecoparqueo.screen.admin.AdminParqueoScreen
import kotlinx.coroutines.launch

@Serializable object LoginRoute
@Serializable object RegistroUsuarioRoute
@Serializable object DashboardEstudianteRoute
@Serializable object DashboardGuardaRoute
@Serializable object RegistroVehicularRoute
@Serializable object SeleccionParqueoRoute
@Serializable
data class MostrarParqueoRoute(
    val nombreParqueo: String,
    val direccion: String,
    val disponibles: Int,
    val latitud: Double = 12.108503522103808,
    val longitud: Double = -86.25693253419533
)
@Serializable object SeleccionSeguridadRoute
@Serializable
data class ControlAccesoVehicularRoute(
    val nombreParqueo: String,
    val latitud: Double = 12.108503522103808,
    val longitud: Double = -86.25693253419533
)
@Serializable object GestionVehiculosRoute
@Serializable object EstadisticasRoute
@Serializable object AdminParqueoRoute


@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val userSession by Graph.sessionManager.userSession.collectAsState(initial = null)
    val coroutineScope = rememberCoroutineScope()

    // Manejo de auto-login al abrir la app o deslogueo reactivo
    LaunchedEffect(userSession) {
        val user = userSession
        if (user != null) {
            val destination = if (user.tipoUsuario == "Estudiante") DashboardEstudianteRoute else DashboardGuardaRoute
            // Evitamos navegar si ya estamos en un destino secundario (ej: gestion de vehiculos, etc)
            // Solo redireccionamos si el destino actual es el Login
            val currentRoute = navController.currentBackStackEntry?.destination?.route
            if (currentRoute == null || currentRoute == LoginRoute::class.qualifiedName) {
                navController.navigate(destination) {
                    popUpTo(LoginRoute) { inclusive = true }
                }
            }
        } else {
            // Si la sesión es null (logout) y no estamos en Login, forzamos regresar al Login
            val currentRoute = navController.currentBackStackEntry?.destination?.route
            if (currentRoute != null && currentRoute != LoginRoute::class.qualifiedName && currentRoute != RegistroUsuarioRoute::class.qualifiedName) {
                navController.navigate(LoginRoute) {
                    popUpTo(0) { inclusive = true }
                }
            }
        }
    }

    NavHost(navController = navController, startDestination = LoginRoute, modifier = modifier) {

        composable<LoginRoute> {
            LoginScreen(
                onLoginSuccess = { tipo ->
                    // Al iniciar sesión de manera exitosa, el LoginViewModel ya guarda la sesión.
                    // El LaunchedEffect de arriba reaccionará y hará la navegación.
                },
                onRegisterClick = {
                    navController.navigate(RegistroUsuarioRoute)
                }
            )
        }

        composable<RegistroUsuarioRoute> {
            RegistroUsuarioScreen(
                onVolverAlLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable<DashboardEstudianteRoute> {
            DashboardEstudianteScreen(
                nombreUsuario = userSession?.nombre ?: "Estudiante",
                onNavigateToGestionVehiculos = { navController.navigate(GestionVehiculosRoute) },
                onNavigateToSeleccionParqueo = { navController.navigate(SeleccionParqueoRoute) },
                onCerrarSesion = {
                    coroutineScope.launch {
                        Graph.sessionManager.clearSession()
                    }
                }
            )
        }

        composable<DashboardGuardaRoute> {
            DashboardGuardaScreen(
                nombreUsuario = userSession?.nombre ?: "Guarda",
                onNavigateToControlAcceso = { navController.navigate(SeleccionSeguridadRoute) },
                onNavigateToEstadisticas = { navController.navigate(EstadisticasRoute) },
                onNavigateToAdminParqueo = { navController.navigate(AdminParqueoRoute) },
                onCerrarSesion = {
                    coroutineScope.launch {
                        Graph.sessionManager.clearSession()
                    }
                }
            )
        }

        composable<GestionVehiculosRoute> {
            GestionVehiculosScreen(onVolver = { navController.popBackStack() })
        }

        composable<RegistroVehicularRoute> {
            val usuarioIdSesion = userSession?.id ?: FALLBACK_USER_ID
            RegistroVehicularScreen(
                usuarioId = usuarioIdSesion,
                onRegistroExitoso = { navController.navigate(SeleccionParqueoRoute) }
            )
        }

        composable<SeleccionParqueoRoute> {
            SeleccionParqueoScreen(
                tab = 2,
                onIrAlParqueo = { nombre: String, direccion: String, disponibles: Int, latitud: Double, longitud: Double ->
                    navController.navigate(
                        MostrarParqueoRoute(nombre, direccion, disponibles, latitud, longitud)
                    )
                }
            )
        }

        composable<MostrarParqueoRoute> { backStackEntry ->
            val args = backStackEntry.toRoute<MostrarParqueoRoute>()
            MostrarParqueoScreen(
                nombreParqueo = args.nombreParqueo,
                direccion     = args.direccion,
                disponibles   = args.disponibles,
                latitud       = args.latitud,
                longitud      = args.longitud,
                onVolver      = { navController.popBackStack() }
            )
        }

        composable<SeleccionSeguridadRoute> {
            SeleccionSeguridadScreen(
                onParkingSelected = { nombre, lat, lng ->
                    navController.navigate(ControlAccesoVehicularRoute(nombre, lat, lng))
                }
            )
        }

        composable<ControlAccesoVehicularRoute> { backStackEntry ->
            val args = backStackEntry.toRoute<ControlAccesoVehicularRoute>()
            ControlAccesoVehicularScreen(
                nombreParqueo = args.nombreParqueo,
                latitud       = args.latitud,
                longitud      = args.longitud,
                onBack        = { navController.popBackStack() }
            )
        }

        composable<EstadisticasRoute> {
            EstadisticasScreen(onVolver = { navController.popBackStack() })
        }

        composable<AdminParqueoRoute> {
            AdminParqueoScreen(onVolver = { navController.popBackStack() })
        }
    }
}