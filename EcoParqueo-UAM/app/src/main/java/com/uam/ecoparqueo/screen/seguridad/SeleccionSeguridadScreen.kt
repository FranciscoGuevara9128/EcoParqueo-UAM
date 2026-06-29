package com.uam.ecoparqueo.screen.seguridad

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.uam.ecoparqueo.vmodel.SeleccionParqueoViewModel

@Composable
fun SeleccionSeguridadScreen(
    onParkingSelected: (String, Double, Double) -> Unit,
    viewModel: SeleccionParqueoViewModel = viewModel()
){
    val state by viewModel.state.collectAsState()
    val colorScheme = MaterialTheme.colorScheme

    val UAM = remember { LatLng(12.108503522103808, -86.25693253419533) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(UAM, 16f)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(colorScheme.primary, colorScheme.tertiary)
                )
            )
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            "Seleccione Punto de Control",
            fontSize   = 24.sp,
            fontWeight = FontWeight.Bold,
            color      = colorScheme.onPrimary
        )
        Text(
            text  = "${state.parqueos.size} parqueo(s) disponible(s)",
            color = colorScheme.onPrimary.copy(alpha = 0.8f),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Estado de carga
        if (state.loading) {
            CircularProgressIndicator(color = colorScheme.onPrimary)
            return@Column
        }

        // Mapa con los parqueos
        Card(
            modifier  = Modifier
                .fillMaxWidth()
                .height(220.dp),
            shape     = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            GoogleMap(
                modifier            = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                uiSettings          = MapUiSettings(
                    zoomControlsEnabled = true,
                    mapToolbarEnabled   = false
                )
            ) {
                state.parqueos.forEach { parqueo ->
                    if (parqueo.latitud != null && parqueo.longitud != null) {
                        val markerState = remember(parqueo.id) {
                            MarkerState(LatLng(parqueo.latitud, parqueo.longitud))
                        }
                        Marker(
                            state   = markerState,
                            title   = parqueo.nombre,
                            snippet = "${parqueo.disponibles}/${parqueo.capacidadTotal} espacios",
                            icon    = BitmapDescriptorFactory.defaultMarker(
                                BitmapDescriptorFactory.HUE_GREEN
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Error de red
        if (state.errorMessage.isNotBlank()) {
            Card(
                shape  = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = colorScheme.errorContainer
                )
            ) {
                Text(
                    text     = "⚠ Sin conexión: ${state.errorMessage}",
                    color    = colorScheme.onErrorContainer,
                    modifier = Modifier.padding(12.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Lista de parqueos
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(state.parqueos) { parqueo ->
                Button(
                    onClick  = { onParkingSelected(
                        parqueo.nombre,
                        parqueo.latitud ?: 12.108503522103808,
                        parqueo.longitud ?: -86.25693253419533
                    ) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .height(54.dp),
                    shape  = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorScheme.primary
                    )
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text       = parqueo.nombre,
                            color      = colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text  = "${parqueo.disponibles}/${parqueo.capacidadTotal} espacios",
                            color = colorScheme.onPrimary.copy(alpha = 0.8f),
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick  = { viewModel.actualizarDisponibilidad() },
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp),
            shape  = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorScheme.secondary
            )
        ) {
            if (state.loading) {
                CircularProgressIndicator(
                    modifier    = Modifier.size(18.dp),
                    strokeWidth = 2.dp,
                    color       = colorScheme.onSecondary
                )
            } else {
                Text("Actualizar lista", color = colorScheme.onSecondary)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}