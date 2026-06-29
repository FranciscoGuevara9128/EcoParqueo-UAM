package com.uam.ecoparqueo.screen.estudiante

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.uam.ecoparqueo.vmodel.SeleccionParqueoViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.uam.ecoparqueo.screen.estudiante.ParqueoMapView

@Composable
fun SeleccionParqueoScreen(
    tab: Int,
    onIrAlParqueo: (String, String, Int) -> Unit,
    viewModel: SeleccionParqueoViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val colorScheme = MaterialTheme.colorScheme
    val scope = rememberCoroutineScope()
    var navegando by remember { mutableStateOf(false) }

    LaunchedEffect(tab) {
        viewModel.onTabChange(tab)
    }

    val checkboxColors = CheckboxDefaults.colors(
        checkedColor = colorScheme.primary,
        uncheckedColor = colorScheme.outline,
        checkmarkColor = colorScheme.onPrimary
    )

    if (navegando) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(colorScheme.primary, colorScheme.tertiary)
                    )
                )
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(color = colorScheme.onPrimary)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Cargando dirección",
                color = colorScheme.onPrimary,
                fontWeight = FontWeight.Bold
            )
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(colorScheme.primary, colorScheme.tertiary)
                    )
                )
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Selecciona un parqueo",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onPrimary
            )
            Text(
                text = "Elige una opción disponible para continuar con la navegación.",
                color = colorScheme.onPrimary.copy(alpha = 0.9f)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Card(
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
            ) {
                ParqueoMapView(
                    parqueos     = state.parqueos,
                    selectedName = state.selectedName,
                    zoomLatitud  = state.zoomLatitud,
                    zoomLongitud = state.zoomLongitud,
                    modifier     = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = colorScheme.primaryContainer),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Parqueos disponibles: ${state.totalDisponibles}",
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = when (state.tab) {
                            0 -> "Mostrando parqueos con cupo"
                            1 -> "Mostrando parqueos sin cupo"
                            else -> "Mostrando todos los parqueos"
                        },
                        color = colorScheme.onSurfaceVariant
                    )
                }
            }

            // Mensaje de error de red
            if (state.errorMessage.isNotBlank()) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = "⚠ Sin conexión: ${state.errorMessage}",
                        color = colorScheme.onErrorContainer,
                        modifier = Modifier.padding(12.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { viewModel.actualizarDisponibilidad() },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primary)
            ) {
                if (state.loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = colorScheme.onPrimary
                    )
                } else {
                    Text("Actualizar disponibilidad", color = colorScheme.onPrimary)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Lista de parqueos",
                fontWeight = FontWeight.Bold,
                color = colorScheme.onPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(state.parqueosFiltrados) { parqueo ->
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .clickable(enabled = parqueo.disponibles > 0) {
                                if (parqueo.disponibles > 0) {
                                    viewModel.onParqueoSelected(parqueo.nombre)
                                }
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = when {
                                state.selectedName == parqueo.nombre -> colorScheme.primaryContainer
                                parqueo.disponibles == 0 -> colorScheme.surfaceVariant
                                else -> colorScheme.surface
                            }
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    parqueo.nombre,
                                    fontWeight = FontWeight.Bold,
                                    color = if (parqueo.disponibles > 0) colorScheme.primary
                                    else colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Dirección: ${parqueo.direccion}",
                                    color = if (parqueo.disponibles > 0) colorScheme.onSurfaceVariant
                                    else colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Disponibles: ${parqueo.disponibles}",
                                    color = if (parqueo.disponibles > 0) colorScheme.secondary
                                    else colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                )
                            }
                            Checkbox(
                                checked = state.selectedName == parqueo.nombre,
                                onCheckedChange = { checked ->
                                    if (parqueo.disponibles > 0) {
                                        viewModel.onParqueoSelected(
                                            if (checked) parqueo.nombre else null
                                        )
                                    }
                                },
                                enabled = parqueo.disponibles > 0,
                                colors = checkboxColors
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    navegando = true
                    scope.launch {
                        delay(2000)
                        navegando = false
                        val updated = viewModel.state.value.parqueoSeleccionado
                        if (updated != null) {
                            onIrAlParqueo(updated.nombre, updated.direccion, updated.disponibles)
                        }
                    }
                },
                enabled = state.selectedName != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorScheme.primary,
                    disabledContainerColor = colorScheme.outline
                )
            ) {
                Text("Ir al parqueo", color = colorScheme.onPrimary)
            }
        }
    }
}
