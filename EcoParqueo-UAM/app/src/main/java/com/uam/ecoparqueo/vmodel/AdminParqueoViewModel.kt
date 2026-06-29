package com.uam.ecoparqueo.vmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uam.ecoparqueo.Graph
import com.uam.ecoparqueo.model.Parqueo
import com.uam.ecoparqueo.service.ApiResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdminParqueoState(
    val nombreInput: String          = "",
    val direccionInput: String       = "",
    val capacidadInput: String       = "",
    val latitudInput: String         = "",
    val longitudInput: String        = "",
    val isLoading: Boolean           = false,
    val mensajeExito: String         = "",
    val mensajeError: String         = "",
    val ultimoParqueoGuardado: Parqueo? = null
) {
    val nombreError: Boolean
        get() = nombreInput.isBlank()

    val direccionError: Boolean
        get() = direccionInput.isBlank()

    val capacidadError: Boolean
        get() = capacidadInput.toIntOrNull()?.let { it <= 0 } ?: true

    val latitudError: Boolean
        get() = latitudInput.toDoubleOrNull() == null

    val longitudError: Boolean
        get() = longitudInput.toDoubleOrNull() == null

    val formularioValido: Boolean
        get() = !nombreError && !direccionError &&
                !capacidadError && !latitudError && !longitudError
}

class AdminParqueoViewModel : ViewModel() {

    private val repository = Graph.parqueoRepository

    private val _state = MutableStateFlow(AdminParqueoState())
    val state: StateFlow<AdminParqueoState> = _state.asStateFlow()

    // Flow de parqueos locales para mostrar en el mapa
    val parqueos = repository.getAllParqueosFlow()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun onNombreChange(value: String)    = _state.update { it.copy(nombreInput    = value) }
    fun onDireccionChange(value: String) = _state.update { it.copy(direccionInput = value) }
    fun onCapacidadChange(value: String) = _state.update { it.copy(capacidadInput = value) }
    fun onLatitudChange(value: String)   = _state.update { it.copy(latitudInput   = value) }
    fun onLongitudChange(value: String)  = _state.update { it.copy(longitudInput  = value) }

    fun guardarParqueo() {
        val current = _state.value
        if (!current.formularioValido) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, mensajeError = "", mensajeExito = "") }
            try {
                val capacidad = current.capacidadInput.toInt()
                val nuevoParqueo = Parqueo(
                    name           = current.nombreInput.trim(),
                    capacidadTotal = capacidad,
                    disponibles    = capacidad,
                    direccion      = current.direccionInput.trim(),
                    latitud        = current.latitudInput.toDouble(),
                    longitud       = current.longitudInput.toDouble()
                )

                when (val result = repository.save(nuevoParqueo)) {
                    is ApiResult.Success -> {
                        _state.update {
                            AdminParqueoState(
                                mensajeExito          = "Parqueo \"${nuevoParqueo.name}\" guardado correctamente",
                                ultimoParqueoGuardado = result.data
                            )
                        }
                    }
                    is ApiResult.Error -> {
                        _state.update {
                            it.copy(
                                isLoading    = false,
                                mensajeError = result.message
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading    = false,
                        mensajeError = e.message ?: "Error de conexión"
                    )
                }
            }
        }
    }

    fun onMensajeHandled() {
        _state.update { it.copy(mensajeExito = "", mensajeError = "") }
    }
}
