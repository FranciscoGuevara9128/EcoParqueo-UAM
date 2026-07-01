package com.uam.ecoparqueo.screen.estudiante

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.uam.ecoparqueo.ui.components.DrawerDestination
import com.uam.ecoparqueo.ui.components.EcoParqueoDrawerScaffold
import com.uam.ecoparqueo.vmodel.GestionVehiculosViewModel

@Composable
fun GestionVehiculosScreen(
    nombreUsuario: String,
    tipoUsuario: String,
    onDrawerNavigate: (DrawerDestination) -> Unit,
    onIrAlPanel: () -> Unit,
    onCerrarSesion: () -> Unit,
    viewModel: GestionVehiculosViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val vehiculos by viewModel.misVehiculos.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            snackbarHostState.showSnackbar("Vehículo agregado correctamente")
            viewModel.onSuccessHandled()
        }
    }

    EcoParqueoDrawerScaffold(
        nombreUsuario = nombreUsuario,
        tipoUsuario = tipoUsuario,
        pantallaActual = DrawerDestination.MIS_VEHICULOS,
        title = "Mis Vehículos",
        onNavigate = onDrawerNavigate,
        onIrAlPanel = onIrAlPanel,
        onCerrarSesion = onCerrarSesion,
        snackbarHostState = snackbarHostState
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {

            Card(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Agregar Vehículo", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = state.placaInput,
                        onValueChange = { viewModel.onPlacaChange(it) },
                        label = { Text("Número de Placa") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = state.marcaInput,
                            onValueChange = { viewModel.onMarcaChange(it) },
                            label = { Text("Marca") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = state.modeloInput,
                            onValueChange = { viewModel.onModeloChange(it) },
                            label = { Text("Modelo") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = state.colorInput,
                        onValueChange = { viewModel.onColorChange(it) },
                        label = { Text("Color") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (state.errorMessage.isNotBlank()) {
                        Text(
                            text = state.errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    Button(
                        onClick = { viewModel.guardarVehiculo() },
                        enabled = state.isFormValid && !state.isLoading,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (state.isLoading) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                        else Text("Agregar Vehículo")
                    }
                }
            }

            Text("Vehículos Registrados", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(vehiculos) { vehiculo ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Row(modifier = Modifier.padding(16.dp)) {
                            Text("🚗", style = MaterialTheme.typography.headlineMedium)
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(vehiculo.numeroPlaca, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                                Text("${vehiculo.marca} ${vehiculo.modelo} - ${vehiculo.colorVehiculo}", style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }
        }
    }
}