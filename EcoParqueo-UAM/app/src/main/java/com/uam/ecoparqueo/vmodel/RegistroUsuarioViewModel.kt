package com.uam.ecoparqueo.vmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uam.ecoparqueo.Graph
import com.uam.ecoparqueo.service.ApiResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RegistroUsuarioState(
    val nombre: String = "",
    val contrasena: String = "",
    val contrasenaVisible: Boolean = false,
    val loading: Boolean = false,
    val registroExitoso: Boolean = false,
    val mensajeError: String = ""
) {
    val isFormValid: Boolean get() = nombre.isNotBlank() && contrasena.length >= 4
}

class RegistroUsuarioViewModel : ViewModel() {
    private val authRepository = Graph.authRepository

    private val _state = MutableStateFlow(RegistroUsuarioState())
    val state = _state.asStateFlow()

    fun onNombreChange(value: String) {
        _state.update { it.copy(nombre = value, mensajeError = "") }
    }

    fun onContrasenaChange(value: String) {
        _state.update { it.copy(contrasena = value, mensajeError = "") }
    }

    fun onToggleContrasenaVisible() {
        _state.update { it.copy(contrasenaVisible = !it.contrasenaVisible) }
    }

    fun onRegistrar() {
        val current = _state.value
        if (!current.isFormValid) {
            _state.update { it.copy(mensajeError = "Nombre no puede estar vacío y contraseña mínimo 4 caracteres") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(loading = true, mensajeError = "") }
            when (val result = authRepository.register(current.nombre, current.contrasena)) {
                is ApiResult.Success -> {
                    _state.update { it.copy(loading = false, registroExitoso = true) }
                }
                is ApiResult.Error -> {
                    _state.update { it.copy(loading = false, mensajeError = result.message) }
                }
            }
        }
    }

    fun resetRegistro() {
        _state.update { it.copy(registroExitoso = false) }
    }
}
