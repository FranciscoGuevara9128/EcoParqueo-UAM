package com.uam.ecoparqueo.vmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uam.ecoparqueo.Graph
import com.uam.ecoparqueo.service.ApiResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginState(
    val nombre: String = "",
    val contrasena: String = "",
    val contrasenaVisible: Boolean = false,
    val loading: Boolean = false,
    val mensajeError: String = "",
    val loginExitoso: Boolean = false,
    val tipoUsuario: String = ""
)

class LoginViewModel : ViewModel() {

    private val authRepository = Graph.authRepository

    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    fun onNombreChange(nombre: String) {
        _state.update { it.copy(nombre = nombre, mensajeError = "") }
    }

    fun onContrasenaChange(contrasena: String) {
        _state.update { it.copy(contrasena = contrasena, mensajeError = "") }
    }

    fun onToggleContrasenaVisible() {
        _state.update { it.copy(contrasenaVisible = !it.contrasenaVisible) }
    }

    fun onLogin() {
        val current = _state.value

        if (current.nombre.isBlank() || current.contrasena.isBlank()) {
            _state.update { it.copy(mensajeError = "Ingrese su nombre y contraseña") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(loading = true, mensajeError = "") }
            try {
                when (val result = authRepository.login(current.nombre, current.contrasena)) {
                    is ApiResult.Success -> {
                        _state.update {
                            it.copy(
                                loginExitoso = true,
                                tipoUsuario = result.data.tipoUsuario
                            )
                        }
                    }
                    is ApiResult.Error -> {
                        _state.update { it.copy(mensajeError = result.message) }
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(mensajeError = "Sin conexión con el servidor")
                }
            } finally {
                _state.update { it.copy(loading = false) }
            }
        }
    }

    fun onLoginHandled() {
        _state.update { it.copy(loginExitoso = false) }
    }
}
