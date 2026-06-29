package com.uam.ecoparqueo.vmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uam.ecoparqueo.Graph
import com.uam.ecoparqueo.model.Vehiculo
import com.uam.ecoparqueo.model.UsuarioRef
import com.uam.ecoparqueo.model.entity.RegistroAccesoEntity
import com.uam.ecoparqueo.model.entity.UsuarioEntity
import com.uam.ecoparqueo.model.entity.VehiculoEntity
import com.uam.ecoparqueo.repository.ParqueoRepository
import com.uam.ecoparqueo.repository.VehiculoRepository
import com.uam.ecoparqueo.service.ApiResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ControlAccesoState(
    val placaText: String = "",
    val placaList: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val infoMessage: String = ""
) {
    val placaNormalized: String
        get() = placaText.trim().uppercase()

    val placaError: Boolean
        get() = placaNormalized.isBlank() || !placaNormalized.matches(Regex("^[A-Z0-9-]{6,10}$"))
}

class ControlAccesoViewModel : ViewModel() {

    private val vehiculoDao = Graph.database.vehiculoDao()
    private val parqueoDao = Graph.database.parqueoDao()
    private val registroDao = Graph.database.registroAccesoDao()

    private val vehiculoRepository = VehiculoRepository()
    private val parqueoRepository = ParqueoRepository()

    private val _state = MutableStateFlow(ControlAccesoState())
    val state: StateFlow<ControlAccesoState> = _state.asStateFlow()

    fun onPlacaTextChange(text: String) {
        _state.update { it.copy(placaText = text, errorMessage = "", infoMessage = "") }
    }

    fun registrarPlaca(nombreParqueo: String) {
        val current = _state.value
        if (current.placaText.isBlank() || current.placaError) {
            _state.update { it.copy(errorMessage = "Ingrese una placa válida") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = "", infoMessage = "") }
            val placa = current.placaNormalized

            try {
                // 1. Obtener el parqueo por su nombre
                val parqueo = parqueoDao.getParqueoByNombre(nombreParqueo)
                if (parqueo == null) {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Error: El parqueo '$nombreParqueo' no está registrado."
                        )
                    }
                    return@launch
                }

                // 2. Buscar si el vehículo existe localmente o en el servidor
                var vehiculo = vehiculoDao.getVehiculoByPlaca(placa)
                if (vehiculo == null) {
                    // Si no está en Room, lo buscamos en PostgreSQL vía API REST
                    when (val result = vehiculoRepository.findByPlaca(placa)) {
                        is ApiResult.Success -> {
                            val vDto = result.data
                            val ownerId = vDto.usuario?.id ?: "d35ac9db-2893-4605-8fd2-01afc4fd5dfb"
                            
                            // Asegurar la existencia del propietario en SQLite local para cumplir la FK
                            val localOwner = Graph.database.usuarioDao().getUsuarioById(ownerId)
                            if (localOwner == null) {
                                Graph.database.usuarioDao().insert(
                                    UsuarioEntity(
                                        id = ownerId,
                                        nombre = "Estudiante Externo",
                                        tipoUsuario = "Estudiante"
                                    )
                                )
                            }

                            // Guardar el vehículo localmente en Room
                            val entidad = VehiculoEntity(
                                id = vDto.id ?: java.util.UUID.randomUUID().toString(),
                                usuarioId = ownerId,
                                numeroPlaca = vDto.numeroPlaca,
                                marca = vDto.marca,
                                modelo = vDto.modelo,
                                anio = vDto.anio,
                                colorVehiculo = vDto.colorVehiculo,
                                tipoVehiculo = vDto.tipoVehiculo,
                                notasAdicionales = vDto.notasAdicionales
                            )
                            vehiculoDao.insert(entidad)
                            vehiculo = entidad
                        }
                        is ApiResult.Error -> {
                            // Regla #3: El guarda debe poder registrar cualquier vehículo independientemente de si existe previamente.
                            // Creamos un registro de vehículo tipo "Invitado" SIN usuario asociado
                            val guestDto = Vehiculo(
                                marca = "Invitado",
                                numeroPlaca = placa,
                                modelo = "Invitado",
                                anio = "2026",
                                colorVehiculo = "N/A",
                                tipoVehiculo = "CARRO",
                                notasAdicionales = "Registrado por Guarda en control de acceso",
                                usuario = null // Sin asociar
                            )

                            // Intentamos guardar en PostgreSQL
                            val saveResult = vehiculoRepository.save(guestDto)
                            val finalId = if (saveResult is ApiResult.Success) {
                                saveResult.data.id ?: java.util.UUID.randomUUID().toString()
                            } else {
                                java.util.UUID.randomUUID().toString()
                            }

                            val entidad = VehiculoEntity(
                                id = finalId,
                                usuarioId = null, // Sin asociar
                                numeroPlaca = placa,
                                marca = "Invitado",
                                modelo = "Invitado",
                                anio = "2026",
                                colorVehiculo = "N/A",
                                tipoVehiculo = "CARRO",
                                notasAdicionales = "Registrado por Guarda en control de acceso"
                            )
                            vehiculoDao.insert(entidad)
                            vehiculo = entidad
                        }
                        is ApiResult.Loading -> return@launch
                    }
                }

                // 3. Determinar si es un Ingreso o una Salida
                val registroActivo = registroDao.getRegistroActivoDeVehiculo(vehiculo.id)
                if (registroActivo != null) {
                    // ── SALIDA ───────────────────────────────────────────
                    registroDao.marcarSalida(registroActivo.id, System.currentTimeMillis())
                    parqueoRepository.aumentarDisponibilidad(parqueo.id)

                    _state.update {
                        it.copy(
                            placaList = listOf("🚗 (SALIDA) $placa") + it.placaList,
                            placaText = "",
                            isLoading = false,
                            infoMessage = "Salida registrada con éxito para la placa $placa"
                        )
                    }
                } else {
                    // ── INGRESO ──────────────────────────────────────────
                    if (parqueo.disponibles <= 0) {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = "Error: No hay espacios disponibles en ${parqueo.nombre}."
                            )
                        }
                        return@launch
                    }

                    registroDao.insert(
                        RegistroAccesoEntity(
                            vehiculoId = vehiculo.id,
                            parqueoId = parqueo.id,
                            estado = "DENTRO"
                        )
                    )
                    parqueoRepository.disminuirDisponibilidad(parqueo.id)

                    _state.update {
                        it.copy(
                            placaList = listOf("🚗 (INGRESO) $placa") + it.placaList,
                            placaText = "",
                            isLoading = false,
                            infoMessage = "Ingreso registrado con éxito para la placa $placa"
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Ocurrió un error inesperado: ${e.message}"
                    )
                }
            }
        }
    }

    // Regla #4: Botón o acción de acceso rápido para registrar salida de vehículo (incrementa disponibles)
    fun registrarSalidaRapida(nombreParqueo: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = "", infoMessage = "") }
            try {
                val parqueo = parqueoDao.getParqueoByNombre(nombreParqueo)
                if (parqueo == null) {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Error: El parqueo '$nombreParqueo' no está registrado."
                        )
                    }
                    return@launch
                }

                if (parqueo.disponibles >= parqueo.capacidadTotal) {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Error: El parqueo ya se encuentra completamente vacío."
                        )
                    }
                    return@launch
                }

                parqueoRepository.aumentarDisponibilidad(parqueo.id)
                _state.update {
                    it.copy(
                        placaList = listOf("🚗 (SALIDA RÁPIDA)") + it.placaList,
                        isLoading = false,
                        infoMessage = "Salida rápida registrada. Un espacio liberado."
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error al procesar la salida rápida: ${e.message}"
                    )
                }
            }
        }
    }
}