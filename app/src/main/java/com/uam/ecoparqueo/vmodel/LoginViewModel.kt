package com.uam.ecoparqueo.vmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginState(
    val nombre: String = "",
    val tipoUsuario: String = "",
    val loading: Boolean = false,
    val mensajeError: String = "",
    val loginExitoso: Boolean = false
)

class LoginViewModel : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    fun onNombreChange(nombre: String) {
        _state.update { it.copy(nombre = nombre, mensajeError = "") }
    }

    fun onTipoUsuarioChange(tipo: String) {
        _state.update { it.copy(tipoUsuario = tipo, mensajeError = "") }
    }

    fun onLogin() {
        val current = _state.value
        if (current.nombre.isBlank() || current.tipoUsuario.isBlank()) {
            _state.update {
                it.copy(mensajeError = "Debe ingresar su nombre y seleccionar un usuario")
            }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(loading = true) }
            delay(1000)
            _state.update { it.copy(loading = false, loginExitoso = true) }
        }
    }

    fun onLoginHandled() {
        _state.update { it.copy(loginExitoso = false) }
    }
}
