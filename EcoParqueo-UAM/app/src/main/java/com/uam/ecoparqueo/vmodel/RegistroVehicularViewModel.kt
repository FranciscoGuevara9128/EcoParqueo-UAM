package com.uam.ecoparqueo.vmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uam.ecoparqueo.model.UsuarioRef
import com.uam.ecoparqueo.model.Vehiculo
import com.uam.ecoparqueo.repository.VehiculoRepository
import com.uam.ecoparqueo.service.ApiResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RegistroVehicularState(
    val tipoVehiculo: String = "",
    val esCarroChecked: Boolean = false,
    val esMotoChecked: Boolean = false,
    val marca: String = "",
    val modelo: String = "",
    val anio: String = "",
    val numeroPlaca: String = "",
    val colorVehiculo: String = "",
    val indicadorCarga: Boolean = false,
    val registroExitoso: Boolean = false,
    val mensajeSnackbar: String = "",
    val mensajeError: String = ""
) {
    val tipoError: Boolean
        get() = tipoVehiculo.isBlank()

    val marcaError: Boolean
        get() = marca.isBlank() || marca.any { !it.isLetter() && !it.isWhitespace() }

    val modeloError: Boolean
        get() = modelo.isBlank() || modelo.any { !it.isLetter() && !it.isWhitespace() }

    val anioNum: Int?
        get() = anio.toIntOrNull()

    val anioError: Boolean
        get() {
            val num = anio.toIntOrNull()
            return anio.isBlank() || num == null || num !in 1900..2100
        }

    val placaNormalized: String
        get() = numeroPlaca.trim().uppercase()

    val placaError: Boolean
        get() = placaNormalized.isBlank() || !placaNormalized.matches(Regex("^[A-Z0-9-]{6,10}$"))

    val colorError: Boolean
        get() = colorVehiculo.isBlank() || colorVehiculo.any { !it.isLetter() && !it.isWhitespace() }

    val formularioValido: Boolean
        get() = !(tipoError || marcaError || modeloError || anioError || placaError || colorError) && !indicadorCarga
}

class RegistroVehicularViewModel : ViewModel() {

    private val repository = VehiculoRepository()
    private val _state = MutableStateFlow(RegistroVehicularState())
    val state: StateFlow<RegistroVehicularState> = _state.asStateFlow()

    fun onTipoVehiculoChange(tipo: String, esCarro: Boolean, esMoto: Boolean) {
        _state.update {
            it.copy(tipoVehiculo = tipo, esCarroChecked = esCarro, esMotoChecked = esMoto)
        }
    }

    fun onMarcaChange(marca: String) {
        _state.update { it.copy(marca = marca) }
    }

    fun onModeloChange(modelo: String) {
        _state.update { it.copy(modelo = modelo) }
    }

    fun onAnioChange(anio: String) {
        _state.update { it.copy(anio = anio) }
    }

    fun onNumeroPlacaChange(placa: String) {
        _state.update { it.copy(numeroPlaca = placa.uppercase()) }
    }

    fun onColorChange(color: String) {
        _state.update { it.copy(colorVehiculo = color) }
    }

    fun onEnviar(usuarioId: String) {
        val current = _state.value
        if (!current.formularioValido) return

        viewModelScope.launch {
            _state.update { it.copy(indicadorCarga = true, mensajeError = "") }
            val vehiculo = Vehiculo(
                marca          = current.marca.trim(),
                numeroPlaca    = current.placaNormalized,
                modelo         = current.modelo.trim(),
                anio           = (current.anioNum ?: 0).toString(),
                colorVehiculo  = current.colorVehiculo.trim(),
                tipoVehiculo   = current.tipoVehiculo.trim(),
                notasAdicionales = "",
                // Referencia mínima al propietario: el backend resuelve la relación @ManyToOne
                // por este UUID sin necesitar el objeto Usuario completo.
                usuario        = UsuarioRef(id = usuarioId)
            )

            when (val result = repository.save(vehiculo)) {
                is ApiResult.Success -> {
                    _state.value = RegistroVehicularState(
                        registroExitoso  = true,
                        mensajeSnackbar  = "Vehículo ${result.data.numeroPlaca} registrado correctamente"
                    )
                }
                is ApiResult.Error -> {
                    _state.update {
                        it.copy(
                            indicadorCarga = false,
                            mensajeError   = result.message
                        )
                    }
                }
                // Loading no es emitido por repository.save() (es suspend), pero
                // se requiere para que el when sobre la sealed class sea exhaustivo.
                is ApiResult.Loading -> Unit
            }
        }
    }

    fun onRegistroHandled() {
        _state.update { it.copy(registroExitoso = false) }
    }

    fun onSnackbarShown() {
        _state.update { it.copy(mensajeSnackbar = "") }
    }
}