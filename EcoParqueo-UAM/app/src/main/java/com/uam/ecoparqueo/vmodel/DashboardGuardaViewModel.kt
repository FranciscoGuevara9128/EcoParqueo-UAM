package com.uam.ecoparqueo.vmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uam.ecoparqueo.Graph
import com.uam.ecoparqueo.model.RegistroAcceso
import com.uam.ecoparqueo.service.ApiResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DashboardGuardaState(
    val ultimosRegistros: List<RegistroAcceso> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String = ""
)

class DashboardGuardaViewModel : ViewModel() {
    private val registroRepository = Graph.registroAccesoRepository

    private val _state = MutableStateFlow(DashboardGuardaState())
    val state = _state.asStateFlow()

    init {
        cargarUltimosRegistros()
    }

    fun cargarUltimosRegistros() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = "") }
            when (val result = registroRepository.getTodosLosRegistros()) {
                is ApiResult.Success -> {
                    val ordenados = result.data.sortedByDescending { it.fechaHora }.take(5)
                    _state.update { it.copy(ultimosRegistros = ordenados, isLoading = false) }
                }
                is ApiResult.Error -> {
                    _state.update { it.copy(errorMessage = result.message, isLoading = false) }
                }
            }
        }
    }
}
