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
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.*
import com.uam.ecoparqueo.service.DirectionsService
import com.uam.ecoparqueo.util.LocationHelper
import kotlinx.coroutines.launch

@Composable
fun MostrarParqueoScreen(
    nombreParqueo: String,
    direccion: String,
    disponibles: Int,
    latitud: Double,
    longitud: Double,
    onVolver: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val parqueoLatLng = remember { LatLng(latitud, longitud) }

    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    var routePoints by remember { mutableStateOf<List<LatLng>>(emptyList()) }
    var distanciaTexto by remember { mutableStateOf("Calculando...") }
    var tiempoTexto by remember { mutableStateOf("Calculando...") }
    var permisoGranted by remember { mutableStateOf(false) }
    var errorUbicacion by remember { mutableStateOf(false) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(parqueoLatLng, 17f)
    }

    val parqueoMarkerState = remember { MarkerState(position = parqueoLatLng) }

    // Función para calcular ruta una vez que se tiene la ubicación
    fun calcularRuta(loc: LatLng) {
        scope.launch {
            userLocation = loc

            val points = DirectionsService.getRoute(loc, parqueoLatLng)
            routePoints = points

            if (points.isNotEmpty()) {
                val boundsBuilder = LatLngBounds.builder()
                points.forEach { boundsBuilder.include(it) }
                cameraPositionState.animate(
                    CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 120),
                    durationMs = 1000
                )
            }

            val distMetros = calcularDistancia(loc, parqueoLatLng)
            distanciaTexto = if (distMetros < 1000) {
                "${distMetros.toInt()} m"
            } else {
                "${"%.1f".format(distMetros / 1000)} km"
            }
            tiempoTexto = "${(distMetros / 400).toInt() + 1} min en carro"
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
                if (loc != null) calcularRuta(loc) else errorUbicacion = true
            }
        } else {
            errorUbicacion = true
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
            if (loc != null) calcularRuta(loc) else errorUbicacion = true
        } else {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
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
                userLocation?.let { loc ->
                    val userMarkerState = remember(loc) { MarkerState(position = loc) }
                    Marker(
                        state = userMarkerState,
                        title = "Tu ubicación"
                    )
                }

                // Línea de la ruta
                if (routePoints.isNotEmpty()) {
                    Polyline(
                        points = routePoints,
                        color = colorScheme.primary,
                        width = 14f
                    )
                }
            }

            // Botón volver flotante sobre el mapa
            Button(
                onClick = onVolver,
                modifier = Modifier
                    .padding(12.dp)
                    .align(Alignment.TopStart),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorScheme.surface
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("← Volver", color = colorScheme.primary, fontWeight = FontWeight.Bold)
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
                        Text(
                            distanciaTexto,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.primary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            "Distancia",
                            style = MaterialTheme.typography.bodySmall,
                            color = colorScheme.onSurfaceVariant
                        )
                    }

                    // Tiempo estimado
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🕐", fontSize = 22.sp)
                        Text(
                            tiempoTexto,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.primary,
                            style = MaterialTheme.typography.bodyMedium
                        )
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

                if (errorUbicacion) {
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

// Fórmula Haversine para calcular distancia en metros
private fun calcularDistancia(from: LatLng, to: LatLng): Double {
    val r = 6371000.0
    val lat1 = Math.toRadians(from.latitude)
    val lat2 = Math.toRadians(to.latitude)
    val dLat = Math.toRadians(to.latitude - from.latitude)
    val dLng = Math.toRadians(to.longitude - from.longitude)
    val a = Math.sin(dLat / 2).let { it * it } +
            Math.cos(lat1) * Math.cos(lat2) *
            Math.sin(dLng / 2).let { it * it }
    return r * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
}