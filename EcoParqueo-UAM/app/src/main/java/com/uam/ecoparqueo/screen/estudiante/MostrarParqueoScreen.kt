package com.uam.ecoparqueo.screen.estudiante

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.*
import com.uam.ecoparqueo.ui.components.DrawerDestination
import com.uam.ecoparqueo.ui.components.EcoParqueoDrawerScaffold
import com.uam.ecoparqueo.util.LocationHelper
import com.uam.ecoparqueo.vmodel.MostrarParqueoViewModel
import kotlinx.coroutines.launch

@Composable
fun MostrarParqueoScreen(
    nombreParqueo: String,
    direccion: String,
    disponibles: Int,
    latitud: Double,
    longitud: Double,
    nombreUsuario: String,
    tipoUsuario: String,
    onDrawerNavigate: (DrawerDestination) -> Unit,
    onIrAlPanel: () -> Unit,
    onCerrarSesion: () -> Unit,
    viewModel: MostrarParqueoViewModel = viewModel()
) {
    val colorScheme = MaterialTheme.colorScheme
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val parqueoLatLng = remember { LatLng(latitud, longitud) }
    val state by viewModel.state.collectAsState()

    var permisoGranted by remember { mutableStateOf(false) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(parqueoLatLng, 17f)
    }

    val parqueoMarkerState = remember { MarkerState(position = parqueoLatLng) }

    val onRouteCalculated: (List<LatLng>) -> Unit = { points ->
        if (points.isNotEmpty()) {
            val boundsBuilder = LatLngBounds.builder()
            points.forEach { boundsBuilder.include(it) }
            scope.launch {
                cameraPositionState.animate(
                    CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 120),
                    durationMs = 1000
                )
            }
        }
    }

    // Launcher para pedir permiso
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        permisoGranted = isGranted
        if (isGranted) {
            scope.launch {
                val loc = LocationHelper.getCurrentLocation(context)
                if (loc != null) {
                    viewModel.calcularRutaYDistancia(loc, parqueoLatLng, onRouteCalculated)
                } else {
                    viewModel.onLocationError()
                }
            }
        } else {
            viewModel.onLocationError()
        }
    }

    // Al entrar a la pantalla verificar si ya tiene permiso o pedirlo
    LaunchedEffect(Unit) {
        val tienePermiso = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (tienePermiso) {
            permisoGranted = true
            val loc = LocationHelper.getCurrentLocation(context)
            if (loc != null) {
                viewModel.calcularRutaYDistancia(loc, parqueoLatLng, onRouteCalculated)
            } else {
                viewModel.onLocationError()
            }
        } else {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    EcoParqueoDrawerScaffold(
        nombreUsuario = nombreUsuario,
        tipoUsuario = tipoUsuario,
        pantallaActual = DrawerDestination.BUSCAR_PARQUEO,
        title = nombreParqueo,
        onNavigate = onDrawerNavigate,
        onIrAlPanel = onIrAlPanel,
        onCerrarSesion = onCerrarSesion
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(colorScheme.primary, colorScheme.tertiary)
                    )
                )
        ) {
            // Mapa — 70% de la pantalla
            Box(modifier = Modifier.weight(0.7f)) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(
                        isMyLocationEnabled = permisoGranted
                    ),
                    uiSettings = MapUiSettings(
                        zoomControlsEnabled = true,
                        compassEnabled = true,
                        mapToolbarEnabled = true,
                        myLocationButtonEnabled = true
                    )
                ) {
                    // Marcador del parqueo destino
                    Marker(
                        state = parqueoMarkerState,
                        title = nombreParqueo,
                        snippet = "Disponibles: $disponibles"
                    )

                    // Marcador de ubicación del usuario
                    state.userLocation?.let { loc ->
                        val userMarkerState = remember(loc) { MarkerState(position = loc) }
                        Marker(
                            state = userMarkerState,
                            title = "Tu ubicación"
                        )
                    }

                    // Línea de la ruta
                    if (state.routePoints.isNotEmpty()) {
                        Polyline(
                            points = state.routePoints,
                            color = colorScheme.primary,
                            width = 14f
                        )
                    }
                }
            }

            // Info del parqueo — 30% restante
            Card(
                modifier = Modifier
                    .weight(0.3f)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text(
                        nombreParqueo,
                        style = MaterialTheme.typography.titleLarge,
                        color = colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Distancia
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("📍", fontSize = 22.sp)
                            if (state.isRouteLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp,
                                    color = colorScheme.primary
                                )
                            } else {
                                Text(
                                    state.distanciaTexto,
                                    fontWeight = FontWeight.Bold,
                                    color = colorScheme.primary,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            Text(
                                "Distancia",
                                style = MaterialTheme.typography.bodySmall,
                                color = colorScheme.onSurfaceVariant
                            )
                        }

                        // Tiempo estimado
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🕐", fontSize = 22.sp)
                            if (state.isRouteLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp,
                                    color = colorScheme.primary
                                )
                            } else {
                                Text(
                                    state.tiempoTexto,
                                    fontWeight = FontWeight.Bold,
                                    color = colorScheme.primary,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            Text(
                                "Tiempo est.",
                                style = MaterialTheme.typography.bodySmall,
                                color = colorScheme.onSurfaceVariant
                            )
                        }

                        // Espacios disponibles
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🅿️", fontSize = 22.sp)
                            Text(
                                "$disponibles",
                                fontWeight = FontWeight.Bold,
                                color = colorScheme.secondary,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                "Disponibles",
                                style = MaterialTheme.typography.bodySmall,
                                color = colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Text(
                        "📌 $direccion",
                        color = colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall
                    )

                    if (state.errorUbicacion) {
                        Text(
                            "⚠ No se pudo obtener tu ubicación. Activa el GPS.",
                            color = colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}