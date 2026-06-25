package com.uam.ecoparqueo.vmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uam.ecoparqueo.Graph
import com.uam.ecoparqueo.model.entity.VehiculoEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class GestionVehiculosState(
    val placaInput: String = "",
    val marcaInput: String = "",
    val modeloInput: String = "",
    val colorInput: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false
) {
    val isFormValid: Boolean get() = placaInput.isNotBlank() && marcaInput.isNotBlank() && modeloInput.isNotBlank() && colorInput.isNotBlank()
}

class GestionVehiculosViewModel : ViewModel() {
    private val vehiculoDao = Graph.database.vehiculoDao()

    // Suponiendo ID de usuario de prueba (estudiante actual)
    val misVehiculos = vehiculoDao.getVehiculosDeUsuario("d35ac9db-2893-4605-8fd2-01afc4fd5dfb")
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _state = MutableStateFlow(GestionVehiculosState())
    val state = _state.asStateFlow()

    fun onPlacaChange(value: String) = _state.update { it.copy(placaInput = value.uppercase()) }
    fun onMarcaChange(value: String) = _state.update { it.copy(marcaInput = value) }
    fun onModeloChange(value: String) = _state.update { it.copy(modeloInput = value) }
    fun onColorChange(value: String) = _state.update { it.copy(colorInput = value) }

    fun guardarVehiculo() {
        val current = _state.value
        if (!current.isFormValid) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val nuevoVehiculo = VehiculoEntity(
                id = java.util.UUID.randomUUID().toString(),
                usuarioId = "d35ac9db-2893-4605-8fd2-01afc4fd5dfb",
                numeroPlaca = current.placaInput,
                marca = current.marcaInput,
                modelo = current.modeloInput,
                anio = "2026",
                colorVehiculo = current.colorInput,
                tipoVehiculo = "CARRO",
                notasAdicionales = ""
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
    }

    fun onSuccessHandled() {
        _state.update { it.copy(isSuccess = false) }
    }
}
