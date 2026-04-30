package com.uam.ecoparqueo.ui.screens.estudiante

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.uam.ecoparqueo.data.Vehiculo
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun RegistroVehicular() {
    var mostrarSeleccion by remember { mutableStateOf(false) }
    // Si ya se completó el registro, mostramos la pantalla de selección directamente
    if (mostrarSeleccion) {
        SeleccionParqueo(tab = 2)
    } else {
        // Campos del formulario
        var tipoVehiculo by remember { mutableStateOf("") } // "CARRO" o "MOTO"
        var esCarroChecked by remember { mutableStateOf(false) }
        var esMotoChecked by remember { mutableStateOf(false) }
        var marca by remember { mutableStateOf("") }
        var modelo by remember { mutableStateOf("") }
        var anio by remember { mutableStateOf("") }
        var numeroPlaca by remember { mutableStateOf("") }
        var colorVehiculo by remember { mutableStateOf("") }
        var indicadorCarga by remember { mutableStateOf(false) }

        // Validaciones (sin funciones auxiliares, expresiones directas)
        val tipoError = tipoVehiculo.isBlank()
        val marcaError = marca.isBlank() || marca.any { !it.isLetter() && !it.isWhitespace() }
        val modeloError = modelo.isBlank() || modelo.any { !it.isLetter() && !it.isWhitespace() }
        val anioNum = modelo.toIntOrNull()
        val anioError =
            anio.isBlank() || !anio.all { it.isDigit() } || anioNum == null || anioNum !in 1900..2100
        val placaNormalized = numeroPlaca.trim().uppercase()
        val placaError =
            placaNormalized.isBlank() || !placaNormalized.matches(Regex("^[A-Z0-9-]{6,10}$"))
        val colorError =
            colorVehiculo.isBlank() || colorVehiculo.any { !it.isLetter() && !it.isWhitespace() }

        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()

        val formularioValido =
            !(tipoError || marcaError || modeloError || anioError || placaError || colorError) && !indicadorCarga

        Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    "Registro de vehículo (Estudiante)",
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Selección tipo
                Text("Selecciona el tipo de vehículo")
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = esCarroChecked,
                        onCheckedChange = { checked ->
                            esCarroChecked = checked
                            if (checked) {
                                esMotoChecked = false
                                tipoVehiculo = "CARRO"
                            } else if (!esMotoChecked) {
                                tipoVehiculo = ""
                            }
                        }
                    )
                    Text("Carro", modifier = Modifier.padding(start = 8.dp))
                    Spacer(modifier = Modifier.size(16.dp))
                    Checkbox(
                        checked = esMotoChecked,
                        onCheckedChange = { checked ->
                            esMotoChecked = checked
                            if (checked) {
                                esCarroChecked = false
                                tipoVehiculo = "MOTO"
                            } else if (!esCarroChecked) {
                                tipoVehiculo = ""
                            }
                        }
                    )
                    Text("Moto", modifier = Modifier.padding(start = 8.dp))
                }
                if (tipoError) {
                    Text(
                        text = "Selecciona Carro o Moto",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = marca,
                    onValueChange = { marca = it },
                    label = { Text("Marca") },
                    isError = marcaError,
                    modifier = Modifier.fillMaxWidth(),
                    supportingText = {
                        if (marcaError) Text(if (marca.isBlank()) "La marca es requerida" else "La marca solo debe contener letras")
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = modelo,
                    onValueChange = { modelo = it },
                    label = { Text("Modelo") },
                    isError = modeloError,
                    modifier = Modifier.fillMaxWidth(),
                    supportingText = {
                        if (modeloError) Text(if (modelo.isBlank()) "El modelo es requerido" else "El modelo solo debe contener letras")
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = anio,
                    onValueChange = { anio = it },
                    label = { Text("Año") },
                    isError = anioError,
                    modifier = Modifier.fillMaxWidth(),
                    supportingText = {
                        if (anioError) Text(if (modelo.isBlank()) "El año es requerido" else "Ingresa un año válido (1900-2100)")
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = numeroPlaca,
                    onValueChange = { numeroPlaca = it.uppercase() },
                    label = { Text("Número de placa") },
                    isError = placaError,
                    modifier = Modifier.fillMaxWidth(),
                    supportingText = {
                        if (placaError) Text(if (numeroPlaca.isBlank()) "La placa es requerida" else "Formato de placa inválido (6-10 caracteres)")
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = colorVehiculo,
                    onValueChange = { colorVehiculo = it },
                    label = { Text("Color del vehículo") },
                    isError = colorError,
                    modifier = Modifier.fillMaxWidth(),
                    supportingText = {
                        if (colorError) Text(if (colorVehiculo.isBlank()) "El color es requerido" else "Ingresa solo letras")
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {
                    scope.launch {
                        indicadorCarga = true
                        try {
                            delay(800)
                            val vehiculo = Vehiculo(
                                marca = marca.trim(),
                                numeroPlaca = numeroPlaca.trim(),
                                modelo = modelo.trim(),
                                colorVehiculo = colorVehiculo.trim(),
                                tipoVehiculo = tipoVehiculo.trim()
                            )
                            // Aquí solo simulamos el guardado; limpiamos el formulario
                            marca = ""
                            modelo = ""
                            numeroPlaca = ""
                            colorVehiculo = ""
                            esCarroChecked = false
                            esMotoChecked = false
                            tipoVehiculo = ""
                            snackbarHostState.showSnackbar("Vehículo registrado: ${vehiculo.numeroPlaca}")
                            // Cambiamos al flujo de selección desde este mismo composable
                            mostrarSeleccion = true
                        } finally {
                            indicadorCarga = false
                        }
                    }
                }, enabled = formularioValido) {
                    if (indicadorCarga) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Enviar")
                    }
                }
            }
        }
    }
}