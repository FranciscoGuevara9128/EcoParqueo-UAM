package com.uam.ecoparqueo.screen.estudiante

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.uam.ecoparqueo.vmodel.RegistroVehicularViewModel

@Composable
fun RegistroVehicularScreen(
    usuarioId: String,
    onRegistroExitoso: () -> Unit,
    viewModel: RegistroVehicularViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val colorScheme = MaterialTheme.colorScheme
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.mensajeSnackbar) {
        if (state.mensajeSnackbar.isNotBlank()) {
            snackbarHostState.showSnackbar(state.mensajeSnackbar)
            viewModel.onSnackbarShown()
        }
    }

    LaunchedEffect(state.registroExitoso) {
        if (state.registroExitoso) {
            onRegistroExitoso()
            viewModel.onRegistroHandled()
        }
    }

    val textFieldColors = TextFieldDefaults.colors(
        focusedTextColor = colorScheme.primary,
        unfocusedTextColor = colorScheme.primary,
        disabledTextColor = colorScheme.primary,
        errorTextColor = colorScheme.error,
        focusedContainerColor = colorScheme.surface,
        unfocusedContainerColor = colorScheme.surface,
        disabledContainerColor = colorScheme.surface,
        errorContainerColor = colorScheme.surface,
        cursorColor = colorScheme.primary,
        errorCursorColor = colorScheme.error,
        focusedIndicatorColor = colorScheme.primary,
        unfocusedIndicatorColor = colorScheme.outline,
        disabledIndicatorColor = colorScheme.outline,
        errorIndicatorColor = colorScheme.error,
        focusedLabelColor = colorScheme.primary,
        unfocusedLabelColor = colorScheme.primary,
        disabledLabelColor = colorScheme.primary,
        errorLabelColor = colorScheme.error,
        focusedSupportingTextColor = colorScheme.primary,
        unfocusedSupportingTextColor = colorScheme.primary,
        disabledSupportingTextColor = colorScheme.primary,
        errorSupportingTextColor = colorScheme.error
    )

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorScheme.primary)
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                "Registro de vehículo",
                style = MaterialTheme.typography.headlineMedium,
                color = colorScheme.onPrimary
            )
            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Selecciona el tipo de vehículo", color = colorScheme.primary)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = state.esCarroChecked,
                            onCheckedChange = { checked ->
                                if (checked) {
                                    viewModel.onTipoVehiculoChange("CARRO", esCarro = true, esMoto = false)
                                } else if (!state.esMotoChecked) {
                                    viewModel.onTipoVehiculoChange("", esCarro = false, esMoto = false)
                                }
                            }
                        )
                        Text("Carro", modifier = Modifier.padding(start = 8.dp), color = colorScheme.primary)
                        Spacer(modifier = Modifier.size(16.dp))
                        Checkbox(
                            checked = state.esMotoChecked,
                            onCheckedChange = { checked ->
                                if (checked) {
                                    viewModel.onTipoVehiculoChange("MOTO", esCarro = false, esMoto = true)
                                } else if (!state.esCarroChecked) {
                                    viewModel.onTipoVehiculoChange("", esCarro = false, esMoto = false)
                                }
                            }
                        )
                        Text("Moto", modifier = Modifier.padding(start = 8.dp), color = colorScheme.primary)
                    }
                    if (state.tipoError) {
                        Text(
                            text = "Seleccion obligatoria",
                            color = colorScheme.error,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = state.marca,
                        onValueChange = { viewModel.onMarcaChange(it) },
                        label = { Text("Marca") },
                        isError = state.marcaError,
                        modifier = Modifier.fillMaxWidth(),
                        colors = textFieldColors,
                        supportingText = {
                            if (state.marcaError) Text(
                                if (state.marca.isBlank()) "La marca es requerida"
                                else "La marca solo debe contener letras"
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = state.modelo,
                        onValueChange = { viewModel.onModeloChange(it) },
                        label = { Text("Modelo") },
                        isError = state.modeloError,
                        modifier = Modifier.fillMaxWidth(),
                        colors = textFieldColors,
                        supportingText = {
                            if (state.modeloError) Text(
                                if (state.modelo.isBlank()) "El modelo es requerido"
                                else "El modelo solo debe contener letras"
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = state.anio,
                        onValueChange = { viewModel.onAnioChange(it) },
                        label = { Text("Año") },
                        isError = state.anioError,
                        modifier = Modifier.fillMaxWidth(),
                        colors = textFieldColors,
                        supportingText = {
                            if (state.anioError) Text(
                                if (state.anio.isBlank()) "El año es requerido"
                                else "Ingresa un año válido (1900-2100)"
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = state.numeroPlaca,
                        onValueChange = { viewModel.onNumeroPlacaChange(it) },
                        label = { Text("Número de placa") },
                        isError = state.placaError,
                        modifier = Modifier.fillMaxWidth(),
                        colors = textFieldColors,
                        supportingText = {
                            if (state.placaError) Text(
                                if (state.numeroPlaca.isBlank()) "La placa es requerida"
                                else "Formato de placa inválido (6-10 caracteres)"
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = state.colorVehiculo,
                        onValueChange = { viewModel.onColorChange(it) },
                        label = { Text("Color del vehículo") },
                        isError = state.colorError,
                        modifier = Modifier.fillMaxWidth(),
                        colors = textFieldColors,
                        supportingText = {
                            if (state.colorError) Text(
                                if (state.colorVehiculo.isBlank()) "El color es requerido"
                                else "Ingresa solo letras"
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

                // Error de API
                if (state.mensajeError.isNotBlank()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = state.mensajeError,
                            color = colorScheme.onErrorContainer,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

            Button(
                onClick = { viewModel.onEnviar(usuarioId) },
                enabled = state.formularioValido,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorScheme.surface,
                    contentColor = colorScheme.primary,
                    disabledContainerColor = colorScheme.surface,
                    disabledContentColor = colorScheme.outline
                )
            ) {
                if (state.indicadorCarga) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = colorScheme.primary
                    )
                } else {
                    Text("Enviar", color = colorScheme.primary)
                }
            }
        }
    }
}