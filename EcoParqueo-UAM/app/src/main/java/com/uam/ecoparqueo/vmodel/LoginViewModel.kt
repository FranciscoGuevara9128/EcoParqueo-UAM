package com.uam.ecoparqueo.vmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uam.ecoparqueo.model.LoginRequest
import com.uam.ecoparqueo.service.RetrofitClient
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

    private val apiService = RetrofitClient.usuarioApiService

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
                val response = apiService.login(
                    LoginRequest(
                        nombre = current.nombre.trim(),
                        contrasena = current.contrasena
                    )
                )
                if (response.isSuccessful) {
                    val authResponse = response.body()
                    if (authResponse != null) {
                        // Reconstruir el usuario con su respectivo token de sesión
                        val usuario = authResponse.usuario.copy(token = authResponse.token)
                        // Guardar la sesión en DataStore
                        com.uam.ecoparqueo.Graph.sessionManager.saveSession(usuario)
                        _state.update {
                            it.copy(
                                loginExitoso = true,
                                tipoUsuario = usuario.tipoUsuario
                            )
                        }
                    }
                } else {
                    // 401 Unauthorized → credenciales incorrectas
                    _state.update { it.copy(mensajeError = "Nombre o contraseña incorrectos") }
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
