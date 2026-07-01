package com.uam.ecoparqueo.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
import com.uam.ecoparqueo.screen.admin.AdminParqueoScreen
import com.uam.ecoparqueo.ui.components.DrawerDestination
import kotlinx.serialization.Serializable
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

    // ── Navegación centralizada del menú hamburguesa ────────────────
    // El estudiante solo puede saltar entre Buscar Parqueo <-> Mis Vehículos.
    // El guarda puede saltar entre Control de Acceso, Estadísticas y Registrar Parqueo.
    val onDrawerNavigate: (DrawerDestination) -> Unit = { destino ->
        when (destino) {
            DrawerDestination.BUSCAR_PARQUEO -> navController.navigate(SeleccionParqueoRoute) {
                launchSingleTop = true
            }
            DrawerDestination.MIS_VEHICULOS -> navController.navigate(GestionVehiculosRoute) {
                launchSingleTop = true
            }
            DrawerDestination.CONTROL_ACCESO -> navController.navigate(SeleccionSeguridadRoute) {
                launchSingleTop = true
            }
            DrawerDestination.ESTADISTICAS -> navController.navigate(EstadisticasRoute) {
                launchSingleTop = true
            }
            DrawerDestination.REGISTRAR_PARQUEO -> navController.navigate(AdminParqueoRoute) {
                launchSingleTop = true
            }
        }
    }

    val onIrAlPanel: () -> Unit = {
        val destino = if (userSession?.tipoUsuario == "Estudiante") DashboardEstudianteRoute else DashboardGuardaRoute
        navController.navigate(destino) {
            popUpTo(destino) { inclusive = true }
            launchSingleTop = true
        }
    }

    val onCerrarSesionDrawer: () -> Unit = {
        coroutineScope.launch { Graph.sessionManager.clearSession() }
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
                tipoUsuario = userSession?.tipoUsuario ?: "Estudiante",
                onDrawerNavigate = onDrawerNavigate,
                onIrAlPanel = onIrAlPanel,
                onCerrarSesion = onCerrarSesionDrawer,
                onNavigateToGestionVehiculos = { navController.navigate(GestionVehiculosRoute) },
                onNavigateToSeleccionParqueo = { navController.navigate(SeleccionParqueoRoute) }
            )
        }

        composable<DashboardGuardaRoute> {
            DashboardGuardaScreen(
                nombreUsuario = userSession?.nombre ?: "Guarda",
                tipoUsuario = userSession?.tipoUsuario ?: "Guarda",
                onDrawerNavigate = onDrawerNavigate,
                onIrAlPanel = onIrAlPanel,
                onCerrarSesion = onCerrarSesionDrawer,
                onNavigateToControlAcceso = { navController.navigate(SeleccionSeguridadRoute) },
                onNavigateToEstadisticas = { navController.navigate(EstadisticasRoute) },
                onNavigateToAdminParqueo = { navController.navigate(AdminParqueoRoute) }
            )
        }

        // ── A partir de aquí, las pantallas incluyen el menú hamburguesa ──

        composable<GestionVehiculosRoute> {
            GestionVehiculosScreen(
                nombreUsuario = userSession?.nombre ?: "Estudiante",
                tipoUsuario = userSession?.tipoUsuario ?: "Estudiante",
                onDrawerNavigate = onDrawerNavigate,
                onIrAlPanel = onIrAlPanel,
                onCerrarSesion = onCerrarSesionDrawer
            )
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
                nombreUsuario = userSession?.nombre ?: "Estudiante",
                tipoUsuario = userSession?.tipoUsuario ?: "Estudiante",
                onDrawerNavigate = onDrawerNavigate,
                onIrAlPanel = onIrAlPanel,
                onCerrarSesion = onCerrarSesionDrawer,
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
                nombreUsuario = userSession?.nombre ?: "Estudiante",
                tipoUsuario   = userSession?.tipoUsuario ?: "Estudiante",
                onDrawerNavigate = onDrawerNavigate,
                onIrAlPanel   = onIrAlPanel,
                onCerrarSesion = onCerrarSesionDrawer
            )
        }

        composable<SeleccionSeguridadRoute> {
            SeleccionSeguridadScreen(
                nombreUsuario = userSession?.nombre ?: "Guarda",
                tipoUsuario = userSession?.tipoUsuario ?: "Guarda",
                onDrawerNavigate = onDrawerNavigate,
                onIrAlPanel = onIrAlPanel,
                onCerrarSesion = onCerrarSesionDrawer,
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
                nombreUsuario = userSession?.nombre ?: "Guarda",
                tipoUsuario   = userSession?.tipoUsuario ?: "Guarda",
                onDrawerNavigate = onDrawerNavigate,
                onIrAlPanel   = onIrAlPanel,
                onCerrarSesion = onCerrarSesionDrawer
            )
        }

        composable<EstadisticasRoute> {
            EstadisticasScreen(
                nombreUsuario = userSession?.nombre ?: "Guarda",
                tipoUsuario = userSession?.tipoUsuario ?: "Guarda",
                onDrawerNavigate = onDrawerNavigate,
                onIrAlPanel = onIrAlPanel,
                onCerrarSesion = onCerrarSesionDrawer
            )
        }

        composable<AdminParqueoRoute> {
            AdminParqueoScreen(
                nombreUsuario = userSession?.nombre ?: "Guarda",
                tipoUsuario = userSession?.tipoUsuario ?: "Guarda",
                onDrawerNavigate = onDrawerNavigate,
                onIrAlPanel = onIrAlPanel,
                onCerrarSesion = onCerrarSesionDrawer
            )
        }
    }
}