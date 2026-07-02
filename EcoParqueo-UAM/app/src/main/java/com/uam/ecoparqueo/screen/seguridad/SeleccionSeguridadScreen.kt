package com.uam.ecoparqueo.screen.seguridad

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.uam.ecoparqueo.ui.components.DrawerDestination
import com.uam.ecoparqueo.ui.components.EcoParqueoDrawerScaffold
import com.uam.ecoparqueo.vmodel.SeleccionParqueoViewModel

@Composable
fun SeleccionSeguridadScreen(
    nombreUsuario: String,
    tipoUsuario: String,
    onDrawerNavigate: (DrawerDestination) -> Unit,
    onIrAlPanel: () -> Unit,
    onCerrarSesion: () -> Unit,
    onParkingSelected: (String, Double, Double) -> Unit,
    viewModel: SeleccionParqueoViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val colorScheme = MaterialTheme.colorScheme

    val UAM = remember { LatLng(12.108503522103808, -86.25693253419533) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(UAM, 16f)
    }

    val parqueosList = state.parqueos

    // Auto-seleccionar el primero al cargar los parqueos
    var selectedIndex by remember(parqueosList) {
        mutableIntStateOf(0)
    }

    val selectedParqueo = remember(selectedIndex, parqueosList) {
        if (parqueosList.isNotEmpty() && selectedIndex in parqueosList.indices) {
            parqueosList[selectedIndex]
        } else null
    }

    // Animar cámara del mapa hacia el parqueo seleccionado
    LaunchedEffect(selectedParqueo) {
        selectedParqueo?.let { parqueo ->
            if (parqueo.latitud != null && parqueo.longitud != null) {
                cameraPositionState.animate(
                    update = CameraUpdateFactory.newLatLngZoom(
                        LatLng(parqueo.latitud, parqueo.longitud),
                        18f
                    ),
                    durationMs = 800
                )
            }
        }
    }

    EcoParqueoDrawerScaffold(
        nombreUsuario = nombreUsuario,
        tipoUsuario = tipoUsuario,
        pantallaActual = DrawerDestination.CONTROL_ACCESO,
        title = "Punto de Control",
        onNavigate = onDrawerNavigate,
        onIrAlPanel = onIrAlPanel,
        onCerrarSesion = onCerrarSesion,
        topBarActions = {
            IconButton(onClick = { viewModel.actualizarDisponibilidad() }) {
                if (state.loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Actualizar",
                        tint = colorScheme.onPrimary
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(colorScheme.background)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Mensaje de error de red
            if (state.errorMessage.isNotBlank()) {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = "⚠ Error de conexión: ${state.errorMessage}",
                        color = colorScheme.onErrorContainer,
                        modifier = Modifier.padding(12.dp),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Mapa expansivo
            Card(
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f) // Mapa ocupa el espacio central
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                border = BorderStroke(1.dp, colorScheme.outline.copy(alpha = 0.15f))
            ) {
                GoogleMap(
                    modifier            = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties          = MapProperties(isMyLocationEnabled = false),
                    uiSettings          = MapUiSettings(
                        zoomControlsEnabled = true,
                        mapToolbarEnabled   = false
                    )
                ) {
                    // Marcadores de todos los parqueos
                    parqueosList.forEach { parqueo ->
                        if (parqueo.latitud != null && parqueo.longitud != null) {
                            val isSelected = selectedParqueo?.id == parqueo.id
                            val markerState = remember(parqueo.id) {
                                MarkerState(LatLng(parqueo.latitud, parqueo.longitud))
                            }
                            Marker(
                                state   = markerState,
                                title   = parqueo.nombre,
                                snippet = "Libres: ${parqueo.disponibles}/${parqueo.capacidadTotal}",
                                onClick = {
                                    val idx = parqueosList.indexOfFirst { it.id == parqueo.id }
                                    if (idx != -1) {
                                        selectedIndex = idx
                                    }
                                    false
                                },
                                icon    = BitmapDescriptorFactory.defaultMarker(
                                    if (isSelected) BitmapDescriptorFactory.HUE_AZURE
                                    else BitmapDescriptorFactory.HUE_GREEN
                                )
                            )
                        }
                    }
                }
            }

            // Tarjeta de Navegación del Guarda (Selección y Confirmación)
            Card(
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
                border = BorderStroke(1.dp, colorScheme.outline.copy(alpha = 0.12f))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Flecha Izquierda
                        IconButton(
                            onClick = {
                                if (parqueosList.isNotEmpty()) {
                                    selectedIndex = if (selectedIndex - 1 < 0) parqueosList.size - 1 else selectedIndex - 1
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                contentDescription = "Anterior",
                                modifier = Modifier.size(32.dp),
                                tint = colorScheme.primary
                            )
                        }

                        // Detalle Central del Parqueo
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            selectedParqueo?.let { parqueo ->
                                Text(
                                    text = parqueo.nombre,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = colorScheme.primary,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(colorScheme.secondary)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Capacidad: ${parqueo.disponibles} / ${parqueo.capacidadTotal} espacios",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = colorScheme.onSurfaceVariant
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = parqueo.direccion,
                                    fontSize = 11.sp,
                                    color = colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                                    textAlign = TextAlign.Center,
                                    maxLines = 2
                                )
                            } ?: Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(32.dp),
                                    color = colorScheme.primary,
                                    strokeWidth = 3.dp
                                )
                            }
                        }

                        // Flecha Derecha
                        IconButton(
                            onClick = {
                                if (parqueosList.isNotEmpty()) {
                                    selectedIndex = (selectedIndex + 1) % parqueosList.size
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = "Siguiente",
                                modifier = Modifier.size(32.dp),
                                tint = colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Botón de Confirmación
                    Button(
                        onClick = {
                            selectedParqueo?.let { parqueo ->
                                onParkingSelected(
                                    parqueo.nombre,
                                    parqueo.latitud ?: 12.108503522103808,
                                    parqueo.longitud ?: -86.25693253419533
                                )
                            }
                        },
                        enabled = selectedParqueo != null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorScheme.primary,
                            disabledContainerColor = colorScheme.outline.copy(alpha = 0.5f)
                        )
                    ) {
                        Text(
                            text = "Iniciar Control de Acceso",
                            color = colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    }
}