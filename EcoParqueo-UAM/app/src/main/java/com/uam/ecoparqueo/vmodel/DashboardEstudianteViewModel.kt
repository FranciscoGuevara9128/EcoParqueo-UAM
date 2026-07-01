package com.uam.ecoparqueo.vmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uam.ecoparqueo.Graph
import com.uam.ecoparqueo.model.RegistroAcceso
import com.uam.ecoparqueo.service.ApiResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DashboardEstudianteState(
    val misRegistros: List<RegistroAcceso> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String = ""
)

class DashboardEstudianteViewModel : ViewModel() {
    private val registroRepository = Graph.registroAccesoRepository
    private val vehiculoRepository = Graph.vehiculoRepository

    private val _state = MutableStateFlow(DashboardEstudianteState())
    val state = _state.asStateFlow()

    init {
        cargarMisRegistros()
    }

    fun cargarMisRegistros() {
        viewModelScope.launch {
            val usuario = Graph.sessionManager.userSession.first() ?: return@launch
            _state.update { it.copy(isLoading = true, errorMessage = "") }

            // 1. Obtener las placas de los vehículos locales del estudiante
            val misVehiculos = vehiculoRepository.getVehiculosDeUsuarioFlow(usuario.id ?: "").first()
            val misPlacas = misVehiculos.map { it.numeroPlaca.trim().uppercase() }.toSet()

            if (misPlacas.isEmpty()) {
                _state.update { it.copy(misRegistros = emptyList(), isLoading = false) }
                return@launch
            }

            // 2. Obtener todos los registros y filtrar por las placas del estudiante
            when (val result = registroRepository.getTodosLosRegistros()) {
                is ApiResult.Success -> {
                    val filtrados = result.data
                        .filter { it.placa.trim().uppercase() in misPlacas }
                        .sortedByDescending { it.fechaHora }
                        .take(5)
                    _state.update { it.copy(misRegistros = filtrados, isLoading = false) }
                }
                is ApiResult.Error -> {
                    _state.update { it.copy(errorMessage = result.message, isLoading = false) }
                }
            }
        }
    }
}
