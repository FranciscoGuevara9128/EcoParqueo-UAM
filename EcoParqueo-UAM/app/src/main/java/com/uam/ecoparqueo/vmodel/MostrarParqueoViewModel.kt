package com.uam.ecoparqueo.vmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.uam.ecoparqueo.service.DirectionsService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MostrarParqueoState(
    val userLocation: LatLng? = null,
    val routePoints: List<LatLng> = emptyList(),
    val distanciaTexto: String = "Calculando...",
    val tiempoTexto: String = "Calculando...",
    val errorUbicacion: Boolean = false,
    val isRouteLoading: Boolean = false
)

class MostrarParqueoViewModel : ViewModel() {

    private val _state = MutableStateFlow(MostrarParqueoState())
    val state = _state.asStateFlow()

    fun onLocationError() {
        _state.update { it.copy(errorUbicacion = true, distanciaTexto = "N/A", tiempoTexto = "N/A") }
    }

    fun calcularRutaYDistancia(loc: LatLng, parqueoLatLng: LatLng, onRouteCalculated: (List<LatLng>) -> Unit) {
        _state.update { it.copy(userLocation = loc, isRouteLoading = true, errorUbicacion = false) }

        viewModelScope.launch {
            try {
                val points = DirectionsService.getRoute(loc, parqueoLatLng)
                
                val distMetros = calcularDistancia(loc, parqueoLatLng)
                val distText = if (distMetros < 1000) {
                    "${distMetros.toInt()} m"
                } else {
                    "${"%.1f".format(distMetros / 1000)} km"
                }
                val tiempoText = "${(distMetros / 400).toInt() + 1} min en carro"

                _state.update {
                    it.copy(
                        routePoints = points,
                        distanciaTexto = distText,
                        tiempoTexto = tiempoText,
                        isRouteLoading = false
                    )
                }

                if (points.isNotEmpty()) {
                    onRouteCalculated(points)
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        distanciaTexto = "Error",
                        tiempoTexto = "Error",
                        isRouteLoading = false
                    )
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
}
