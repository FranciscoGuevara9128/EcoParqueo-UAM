package com.uam.ecoparqueo.vmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uam.ecoparqueo.model.entity.ParqueoEntity
import com.uam.ecoparqueo.repository.ParqueoRepository
import com.uam.ecoparqueo.service.ApiResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SeleccionParqueoState(
    val parqueos: List<ParqueoEntity> = emptyList(),
    val selectedName: String? = null,
    val loading: Boolean = false,
    val errorMessage: String = "",
    val tab: Int = 2,
    // Coordenadas del parqueo seleccionado para el zoom del mapa
    val zoomLatitud: Double? = null,
    val zoomLongitud: Double? = null
) {
    val parqueosFiltrados: List<ParqueoEntity>
        get() = when (tab) {
            0 -> parqueos.filter { it.disponibles > 0 }
            1 -> parqueos.filter { it.disponibles == 0 }
            else -> parqueos
        }

    val totalDisponibles: Int
        get() = parqueos.count { it.disponibles > 0 }

    val parqueoSeleccionado: ParqueoEntity?
        get() = parqueos.find { it.nombre == selectedName }
}

class SeleccionParqueoViewModel : ViewModel() {

    private val repository = ParqueoRepository()
    private val _internalState = MutableStateFlow(SeleccionParqueoState())

    val state: StateFlow<SeleccionParqueoState> = combine(
        _internalState,
        repository.getAllParqueosFlow()
    ) { internal, parqueosBD ->
        internal.copy(parqueos = parqueosBD)
    }.stateIn(viewModelScope, SharingStarted.Lazily, SeleccionParqueoState())

    init {
        cargarParqueos()
    }

    fun onTabChange(tab: Int) {
        _internalState.update { it.copy(tab = tab) }
    }

    // Al seleccionar un parqueo también actualizamos las coordenadas de zoom
    fun onParqueoSelected(name: String?) {
        val parqueo = _internalState.value.parqueos.find { it.nombre == name }
        _internalState.update {
            it.copy(
                selectedName = if (it.selectedName == name) null else name,
                zoomLatitud  = if (it.selectedName == name) null else parqueo?.latitud,
                zoomLongitud = if (it.selectedName == name) null else parqueo?.longitud
            )
        }
    }

    fun actualizarDisponibilidad() {
        cargarParqueos()
    }


    private fun cargarParqueos() {
        viewModelScope.launch {
            _internalState.update { it.copy(loading = true, errorMessage = "") }
            when (val result = repository.refresh()) {
                is ApiResult.Error -> _internalState.update {
                    it.copy(errorMessage = result.message)
                }
                else -> Unit
            }
            _internalState.update { it.copy(loading = false) }
        }
    }
}