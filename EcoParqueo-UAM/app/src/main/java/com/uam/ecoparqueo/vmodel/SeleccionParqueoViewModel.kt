package com.uam.ecoparqueo.vmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uam.ecoparqueo.model.entity.ParqueoEntity
import com.uam.ecoparqueo.repository.ParqueoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SeleccionParqueoState(
    val parqueos: List<ParqueoEntity> = emptyList(),
    val selectedName: String? = null,
    val loading: Boolean = false,
    val tab: Int = 2
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

    fun onTabChange(tab: Int) {
        _internalState.update { it.copy(tab = tab) }
    }

    fun onParqueoSelected(name: String?) {
        _internalState.update {
            it.copy(selectedName = if (it.selectedName == name) null else name)
        }
    }

    fun actualizarDisponibilidad() {
        // En Room no necesitamos hacer nada especial porque Room nos notifica,
        // pero podemos simular el loading si se desea.
    }

    fun decrementarDisponibilidad() {
        val selected = state.value.parqueoSeleccionado ?: return
        viewModelScope.launch {
            repository.disminuirDisponibilidad(selected.id)
        }
    }
}
