package com.uam.ecoparqueo.vmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uam.ecoparqueo.Graph
import com.uam.ecoparqueo.model.Vehiculo
import com.uam.ecoparqueo.model.UsuarioRef
import com.uam.ecoparqueo.model.entity.VehiculoEntity
import com.uam.ecoparqueo.repository.VehiculoRepository
import com.uam.ecoparqueo.service.ApiResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class GestionVehiculosState(
    val placaInput: String = "",
    val marcaInput: String = "",
    val modeloInput: String = "",
    val colorInput: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String = ""
) {
    val isFormValid: Boolean get() = placaInput.isNotBlank() && marcaInput.isNotBlank() && modeloInput.isNotBlank() && colorInput.isNotBlank()
}

class GestionVehiculosViewModel : ViewModel() {
    private val vehiculoDao = Graph.database.vehiculoDao()
    private val repository = VehiculoRepository()

    @OptIn(ExperimentalCoroutinesApi::class)
    val misVehiculos = Graph.sessionManager.userSession
        .flatMapLatest { usuario ->
            if (usuario?.id != null) {
                vehiculoDao.getVehiculosDeUsuario(usuario.id)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _state = MutableStateFlow(GestionVehiculosState())
    val state = _state.asStateFlow()

    init {
        syncVehiculos()
    }

    fun syncVehiculos() {
        viewModelScope.launch {
            val usuario = Graph.sessionManager.userSession.first() ?: return@launch
            _state.update { it.copy(isLoading = true, errorMessage = "") }
            when (val result = repository.findAll()) {
                is ApiResult.Success -> {
                    // Filtrar vehículos que pertenecen al usuario logueado
                    val userVehicles = result.data.filter { it.usuario?.id == usuario.id }
                    
                    // Convertir a entidades de Room e insertar
                    userVehicles.forEach { v ->
                        vehiculoDao.insert(
                            VehiculoEntity(
                                id = v.id ?: java.util.UUID.randomUUID().toString(),
                                usuarioId = usuario.id ?: "",
                                numeroPlaca = v.numeroPlaca,
                                marca = v.marca,
                                modelo = v.modelo,
                                anio = v.anio,
                                colorVehiculo = v.colorVehiculo,
                                tipoVehiculo = v.tipoVehiculo,
                                notasAdicionales = v.notasAdicionales
                            )
                        )
                    }
                    _state.update { it.copy(isLoading = false) }
                }
                is ApiResult.Error -> {
                    _state.update { it.copy(isLoading = false, errorMessage = "Error al sincronizar: ${result.message}") }
                }
                is ApiResult.Loading -> Unit
            }
        }
    }

    fun onPlacaChange(value: String) = _state.update { it.copy(placaInput = value.uppercase()) }
    fun onMarcaChange(value: String) = _state.update { it.copy(marcaInput = value) }
    fun onModeloChange(value: String) = _state.update { it.copy(modeloInput = value) }
    fun onColorChange(value: String) = _state.update { it.copy(colorInput = value) }

    fun guardarVehiculo() {
        val current = _state.value
        if (!current.isFormValid) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = "") }

            val usuario = Graph.sessionManager.userSession.first()
            if (usuario == null) {
                _state.update { it.copy(isLoading = false, errorMessage = "Error: no hay sesión activa") }
                return@launch
            }

            val vehiculoDto = Vehiculo(
                marca = current.marcaInput.trim(),
                numeroPlaca = current.placaInput.trim().uppercase(),
                modelo = current.modeloInput.trim(),
                anio = "2026",
                colorVehiculo = current.colorInput.trim(),
                tipoVehiculo = "CARRO",
                notasAdicionales = "",
                usuario = UsuarioRef(id = usuario.id ?: "")
            )

            // 1. Guardar en PostgreSQL vía REST API
            when (val result = repository.save(vehiculoDto)) {
                is ApiResult.Success -> {
                    val savedVehiculo = result.data
                    
                    // 2. Guardar en la base de datos local (Room) con el ID devuelto por el servidor
                    val nuevoVehiculo = VehiculoEntity(
                        id = savedVehiculo.id ?: java.util.UUID.randomUUID().toString(),
                        usuarioId = usuario.id ?: "",
                        numeroPlaca = savedVehiculo.numeroPlaca,
                        marca = savedVehiculo.marca,
                        modelo = savedVehiculo.modelo,
                        anio = savedVehiculo.anio,
                        colorVehiculo = savedVehiculo.colorVehiculo,
                        tipoVehiculo = savedVehiculo.tipoVehiculo,
                        notasAdicionales = savedVehiculo.notasAdicionales
                    )
                    
                    vehiculoDao.insert(nuevoVehiculo)

                    _state.update {
                        it.copy(
                            placaInput = "", marcaInput = "", modeloInput = "", colorInput = "",
                            isLoading = false,
                            isSuccess = true
                        )
                    }
                }
                is ApiResult.Error -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Error al guardar en el servidor: ${result.message}"
                        )
                    }
                }
                is ApiResult.Loading -> Unit
            }
        }
    }

    fun onSuccessHandled() {
        _state.update { it.copy(isSuccess = false) }
    }
}
