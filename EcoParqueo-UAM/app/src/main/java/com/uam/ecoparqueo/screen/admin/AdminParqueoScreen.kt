package com.uam.ecoparqueo.screen.admin

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.uam.ecoparqueo.vmodel.AdminParqueoViewModel

@Composable
fun AdminParqueoScreen(
    onVolver: () -> Unit,
    viewModel: AdminParqueoViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val colorScheme = MaterialTheme.colorScheme
    val snackbarHostState = remember { SnackbarHostState() }

    // Mostrar mensajes de éxito o error como Snackbar
    LaunchedEffect(state.mensajeExito) {
        if (state.mensajeExito.isNotBlank()) {
            snackbarHostState.showSnackbar(state.mensajeExito)
            viewModel.onMensajeHandled()
        }
    }
    LaunchedEffect(state.mensajeError) {
        if (state.mensajeError.isNotBlank()) {
            snackbarHostState.showSnackbar(state.mensajeError)
            viewModel.onMensajeHandled()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(colorScheme.primary, colorScheme.tertiary)
                    )
                )
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text       = "Registrar Parqueo",
                fontSize   = 26.sp,
                fontWeight = FontWeight.Bold,
                color      = colorScheme.onPrimary
            )
            Text(
                text  = "Ingresa los datos y coordenadas del área de parqueo",
                color = colorScheme.onPrimary.copy(alpha = 0.85f)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape    = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
                colors   = CardDefaults.cardColors(containerColor = colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    // ── Nombre ────────────────────────────────────────────
                    OutlinedTextField(
                        value         = state.nombreInput,
                        onValueChange = { viewModel.onNombreChange(it) },
                        label         = { Text("Nombre del parqueo") },
                        isError       = state.nombreInput.isNotBlank() && state.nombreError,
                        modifier      = Modifier.fillMaxWidth(),
                        supportingText = {
                            if (state.nombreInput.isNotBlank() && state.nombreError)
                                Text("El nombre es obligatorio")
                        }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // ── Dirección ─────────────────────────────────────────
                    OutlinedTextField(
                        value         = state.direccionInput,
                        onValueChange = { viewModel.onDireccionChange(it) },
                        label         = { Text("Dirección o referencia") },
                        isError       = state.direccionInput.isNotBlank() && state.direccionError,
                        modifier      = Modifier.fillMaxWidth(),
                        supportingText = {
                            if (state.direccionInput.isNotBlank() && state.direccionError)
                                Text("La dirección es obligatoria")
                        }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // ── Capacidad ─────────────────────────────────────────
                    OutlinedTextField(
                        value         = state.capacidadInput,
                        onValueChange = { viewModel.onCapacidadChange(it) },
                        label         = { Text("Capacidad total de espacios") },
                        isError       = state.capacidadInput.isNotBlank() && state.capacidadError,
                        modifier      = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        supportingText = {
                            if (state.capacidadInput.isNotBlank() && state.capacidadError)
                                Text("Ingresa un número mayor a 0")
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text       = "Coordenadas GPS",
                        fontWeight = FontWeight.Bold,
                        color      = colorScheme.primary
                    )
                    Text(
                        text     = "Puedes obtenerlas desde Google Maps manteniendo presionado el punto deseado",
                        fontSize = 12.sp,
                        color    = colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // ── Latitud y Longitud en la misma fila ───────────────
                    Row(
                        modifier            = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value         = state.latitudInput,
                            onValueChange = { viewModel.onLatitudChange(it) },
                            label         = { Text("Latitud") },
                            placeholder   = { Text("12.1328") },
                            isError       = state.latitudInput.isNotBlank() && state.latitudError,
                            modifier      = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            supportingText = {
                                if (state.latitudInput.isNotBlank() && state.latitudError)
                                    Text("Valor inválido")
                            }
                        )

                        OutlinedTextField(
                            value         = state.longitudInput,
                            onValueChange = { viewModel.onLongitudChange(it) },
                            label         = { Text("Longitud") },
                            placeholder   = { Text("-86.2734") },
                            isError       = state.longitudInput.isNotBlank() && state.longitudError,
                            modifier      = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            supportingText = {
                                if (state.longitudInput.isNotBlank() && state.longitudError)
                                    Text("Valor inválido")
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Botón guardar ─────────────────────────────────────────────
            Button(
                onClick  = { viewModel.guardarParqueo() },
                enabled  = state.formularioValido && !state.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape  = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor         = colorScheme.surface,
                    contentColor           = colorScheme.primary,
                    disabledContainerColor = colorScheme.outline,
                    disabledContentColor   = colorScheme.onPrimary
                )
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier    = Modifier.size(22.dp),
                        strokeWidth = 2.dp,
                        color       = colorScheme.primary
                    )
                } else {
                    Text("Guardar parqueo", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ── Botón volver ──────────────────────────────────────────────
            Button(
                onClick  = onVolver,
                modifier = Modifier.fillMaxWidth(),
                shape    = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                colors   = ButtonDefaults.buttonColors(
                    containerColor = colorScheme.primary,
                    contentColor   = colorScheme.onPrimary
                )
            ) {
                Text("Volver")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}