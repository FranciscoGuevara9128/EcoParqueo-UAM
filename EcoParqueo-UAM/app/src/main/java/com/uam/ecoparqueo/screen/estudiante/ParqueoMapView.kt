package com.uam.ecoparqueo.screen.estudiante

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.uam.ecoparqueo.model.entity.ParqueoEntity

private val UAM_LOCATION = LatLng(12.1328, -86.2734)
private const val ZOOM_CAMPUS  = 16f
private const val ZOOM_PARQUEO = 19f

@Composable
fun ParqueoMapView(
    parqueos: List<ParqueoEntity>,
    selectedName: String?,
    zoomLatitud: Double?,
    zoomLongitud: Double?,
    modifier: Modifier = Modifier
) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(UAM_LOCATION, ZOOM_CAMPUS)
    }

    // Estado del marcador del campus, recordado para no recrearse
    val uamMarkerState = remember {
        MarkerState(position = UAM_LOCATION)
    }

    // Estados de los marcadores de parqueo, recordados por nombre de parqueo
    val markerStates = remember(parqueos) {
        parqueos
            .filter { it.latitud != null && it.longitud != null }
            .associate { parqueo ->
                parqueo.nombre to MarkerState(
                    position = LatLng(parqueo.latitud!!, parqueo.longitud!!)
                )
            }
    }

    // Zoom animado al seleccionar o deseleccionar un parqueo
    LaunchedEffect(zoomLatitud, zoomLongitud) {
        if (zoomLatitud != null && zoomLongitud != null) {
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(
                    LatLng(zoomLatitud, zoomLongitud),
                    ZOOM_PARQUEO
                ),
                durationMs = 800
            )
        } else {
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(
                    UAM_LOCATION,
                    ZOOM_CAMPUS
                ),
                durationMs = 600
            )
        }
    }

    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        properties = MapProperties(isMyLocationEnabled = false),
        uiSettings = MapUiSettings(
            zoomControlsEnabled = true,
            compassEnabled      = true,
            mapToolbarEnabled   = false
        )
    ) {
        // Marcador fijo del campus UAM
        Marker(
            state   = uamMarkerState,
            title   = "UAM - Universidad Americana",
            snippet = "Campus principal"
        )

        // Marcadores de parqueos con coordenadas registradas
        parqueos.forEach { parqueo ->
            val markerState = markerStates[parqueo.nombre] ?: return@forEach
            val esSeleccionado = parqueo.nombre == selectedName

            Marker(
                state   = markerState,
                title   = parqueo.nombre,
                snippet = "Disponibles: ${parqueo.disponibles}/${parqueo.capacidadTotal}",
                icon    = BitmapDescriptorFactory.defaultMarker(
                    if (esSeleccionado)
                        BitmapDescriptorFactory.HUE_AZURE
                    else
                        BitmapDescriptorFactory.HUE_RED
                )
            )
        }
    }
}