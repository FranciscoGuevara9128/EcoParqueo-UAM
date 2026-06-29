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
import com.uam.ecoparqueo.vmodel.ControlAccesoViewModel

@Composable
fun ControlAccesoVehicularScreen(
    nombreParqueo: String,
    latitud: Double,
    longitud: Double,
    onBack: () -> Unit,
    viewModel: ControlAccesoViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val colorScheme = MaterialTheme.colorScheme

    val parqueoLatLng = remember { LatLng(latitud, longitud) }
    val markerState   = remember { MarkerState(position = parqueoLatLng) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(parqueoLatLng, 19f)
    }

    val textFieldColors = TextFieldDefaults.colors(
        focusedTextColor          = colorScheme.onPrimary,
        unfocusedTextColor        = colorScheme.onPrimary,
        focusedContainerColor     = colorScheme.primary.copy(alpha = 0.3f),
        unfocusedContainerColor   = colorScheme.primary.copy(alpha = 0.2f),
        cursorColor               = colorScheme.onPrimary,
        focusedIndicatorColor     = colorScheme.onPrimary,
        unfocusedIndicatorColor   = colorScheme.onPrimary.copy(alpha = 0.6f),
        focusedLabelColor         = colorScheme.onPrimary,
        unfocusedLabelColor       = colorScheme.onPrimary.copy(alpha = 0.8f)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(colorScheme.primary, colorScheme.tertiary)
                )
            )
    ) {
        // Mapa — 40% superior
        Box(modifier = Modifier
            .fillMaxWidth()
            .weight(0.4f)
        ) {
            GoogleMap(
                modifier            = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                uiSettings          = MapUiSettings(
                    zoomControlsEnabled = true,
                    mapToolbarEnabled   = true,
                    compassEnabled      = true
                )
            ) {
                Marker(
                    state   = markerState,
                    title   = nombreParqueo,
                    icon    = BitmapDescriptorFactory.defaultMarker(
                        BitmapDescriptorFactory.HUE_GREEN
                    )
                )
            }

            // Nombre del parqueo flotante sobre el mapa
            Card(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp),
                shape  = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = colorScheme.surface.copy(alpha = 0.9f)
                )
            ) {
                Text(
                    text     = "📍 $nombreParqueo",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    color    = colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }

        // Controles — 60% inferior
        Column(
            modifier = Modifier
                .weight(0.6f)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value         = state.placaText,
                onValueChange = { viewModel.onPlacaTextChange(it) },
                label         = { Text("Número de Placa", color = colorScheme.onPrimary) },
                isError       = state.placaText.isNotBlank() && state.placaError,
                modifier      = Modifier.fillMaxWidth(),
                colors        = textFieldColors,
                supportingText = {
                    if (state.placaText.isNotBlank() && state.placaError) {
                        Text(
                            "Ingrese una placa válida (6-10 caracteres)",
                            color = colorScheme.onPrimary
                        )
                    }
                }
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick  = { viewModel.registrarPlaca(nombreParqueo) },
                    enabled  = state.placaText.isNotBlank() && !state.placaError && !state.isLoading,
                    modifier = Modifier.weight(1f),
                    shape    = RoundedCornerShape(16.dp),
                    colors   = ButtonDefaults.buttonColors(
                        containerColor         = colorScheme.surface,
                        contentColor           = colorScheme.primary,
                        disabledContainerColor = colorScheme.outline,
                        disabledContentColor   = colorScheme.onPrimary
                    )
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier    = Modifier.size(20.dp),
                            color       = colorScheme.primary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Registrar")
                    }
                }

                Button(
                    onClick  = { viewModel.registrarSalidaRapida(nombreParqueo) },
                    enabled  = !state.isLoading,
                    modifier = Modifier.weight(1f),
                    shape    = RoundedCornerShape(16.dp),
                    colors   = ButtonDefaults.buttonColors(
                        containerColor = colorScheme.secondaryContainer,
                        contentColor   = colorScheme.onSecondaryContainer
                    )
                ) {
                    Text("⚡ Salida Rápida")
                }
            }

            // Mensaje error
            if (state.errorMessage.isNotBlank()) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    colors   = CardDefaults.cardColors(
                        containerColor = colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text       = state.errorMessage,
                        color      = colorScheme.onErrorContainer,
                        modifier   = Modifier.padding(12.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Mensaje info
            if (state.infoMessage.isNotBlank()) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    colors   = CardDefaults.cardColors(
                        containerColor = colorScheme.primaryContainer
                    )
                ) {
                    Text(
                        text       = state.infoMessage,
                        color      = colorScheme.onPrimaryContainer,
                        modifier   = Modifier.padding(12.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Lista de registros
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(state.placaList) { placa ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors   = CardDefaults.cardColors(
                            containerColor = colorScheme.primaryContainer
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(modifier = Modifier.padding(16.dp)) {
                            Text("🚗 ", color = colorScheme.onSurfaceVariant)
                            Text(
                                placa,
                                fontWeight = FontWeight.Bold,
                                color      = colorScheme.primary
                            )
                        }
                    }
                }
            }

            Button(
                onClick  = onBack,
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(16.dp),
                colors   = ButtonDefaults.buttonColors(
                    containerColor = colorScheme.surface,
                    contentColor   = colorScheme.primary
                )
            ) {
                Text("Volver")
            }
        }
    }
}