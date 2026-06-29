package com.uam.ecoparqueo.vmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uam.ecoparqueo.model.Parqueo
import com.uam.ecoparqueo.service.ApiResult
import com.uam.ecoparqueo.service.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdminParqueoState(
    // Campos del formulario
    val nombreInput: String      = "",
    val direccionInput: String   = "",
    val capacidadInput: String   = "",
    val latitudInput: String     = "",
    val longitudInput: String    = "",
    // Control de UI
    val isLoading: Boolean       = false,
    val mensajeExito: String     = "",
    val mensajeError: String     = ""
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

    private val apiService = RetrofitClient.parqueoApiService
    private val parqueoDao = Graph.database.parqueoDao()

    private val _state = MutableStateFlow(AdminParqueoState())
    val state: StateFlow<AdminParqueoState> = _state.asStateFlow()

    // Flow de parqueos locales para el mapa
    val parqueos = parqueoDao.getAllParqueosFlow()
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
                val response = apiService.save(nuevoParqueo)
                if (response.isSuccessful) {
                    val guardado = response.body()
                    // Guardar también en Room para que aparezca en el mapa inmediatamente
                    guardado?.let {
                        parqueoDao.insert(
                            ParqueoEntity(
                                id             = it.id ?: java.util.UUID.randomUUID().toString(),
                                nombre         = it.name,
                                capacidadTotal = it.capacidadTotal,
                                disponibles    = it.disponibles,
                                direccion      = it.direccion,
                                latitud        = it.latitud,
                                longitud       = it.longitud
                            )
                        )
                    }
                    _state.update {
                        AdminParqueoState(
                            mensajeExito         = "Parqueo \"${nuevoParqueo.name}\" guardado",
                            ultimoParqueoGuardado = guardado
                        )
                    }
                } else {
                    _state.update {
                        it.copy(
                            isLoading    = false,
                            mensajeError = "Error ${response.code()}: ${response.message()}"
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(isLoading = false, mensajeError = e.message ?: "Error de conexión")
                }
            }
        }
    }

    fun onMensajeHandled() {
        _state.update { it.copy(mensajeExito = "", mensajeError = "") }
    }
}