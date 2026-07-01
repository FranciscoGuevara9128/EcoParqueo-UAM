package com.uam.ecoparqueo.screen.estudiante

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.uam.ecoparqueo.ui.components.DrawerDestination
import com.uam.ecoparqueo.ui.components.EcoParqueoDrawerScaffold
import com.uam.ecoparqueo.vmodel.SeleccionParqueoViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeleccionParqueoScreen(
    tab: Int,
    nombreUsuario: String,
    tipoUsuario: String,
    onDrawerNavigate: (DrawerDestination) -> Unit,
    onIrAlPanel: () -> Unit,
    onCerrarSesion: () -> Unit,
    onIrAlParqueo: (String, String, Int, Double, Double) -> Unit,
    viewModel: SeleccionParqueoViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val colorScheme = MaterialTheme.colorScheme
    val scope = rememberCoroutineScope()
    var navegando by remember { mutableStateOf(false) }

    LaunchedEffect(tab) {
        viewModel.onTabChange(tab)
    }

    val parqueosList = state.parqueosFiltrados

    // Auto-seleccionar el primero al cargar los parqueos
    LaunchedEffect(parqueosList) {
        if (parqueosList.isNotEmpty() && state.selectedName == null) {
            viewModel.onParqueoSelected(parqueosList.first().nombre)
        }
    }

    val selectedIndex = remember(state.selectedName, parqueosList) {
        val idx = parqueosList.indexOfFirst { it.nombre == state.selectedName }
        if (idx == -1) 0 else idx
    }

    val selectedParqueo = remember(selectedIndex, parqueosList) {
        if (parqueosList.isNotEmpty() && selectedIndex in parqueosList.indices) {
            parqueosList[selectedIndex]
        } else null
    }

    if (navegando) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(colorScheme.primary, colorScheme.secondary)
                    )
                )
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(color = colorScheme.onPrimary)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Cargando indicaciones de navegación...",
                color = colorScheme.onPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    } else {
        EcoParqueoDrawerScaffold(
            nombreUsuario = nombreUsuario,
            tipoUsuario = tipoUsuario,
            pantallaActual = DrawerDestination.BUSCAR_PARQUEO,
            title = "Buscar Parqueo",
            onNavigate = onDrawerNavigate,
            onIrAlPanel = onIrAlPanel,
            onCerrarSesion = onCerrarSesion,
            topBarActions = {
                IconButton(onClick = { viewModel.actualizarDisponibilidad() }) {
                    if (state.loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Actualizar",
                            tint = colorScheme.onPrimary
                        )
                    }
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(colorScheme.background)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Mensaje de error de red
                if (state.errorMessage.isNotBlank()) {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = "⚠ Error de conexión: ${state.errorMessage}",
                            color = colorScheme.onErrorContainer,
                            modifier = Modifier.padding(12.dp),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Mapa grande y expansivo
                Card(
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f) // Esto hace que el mapa tome todo el espacio central
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    border = BorderStroke(1.dp, colorScheme.outline.copy(alpha = 0.15f))
                ) {
                    ParqueoMapView(
                        parqueos     = state.parqueos,
                        selectedName = state.selectedName,
                        zoomLatitud  = selectedParqueo?.latitud,
                        zoomLongitud = selectedParqueo?.longitud,
                        onParqueoClick = { clickedName ->
                            viewModel.onParqueoSelected(clickedName)
                        },
                        modifier     = Modifier.fillMaxSize()
                    )
                }

                // Tarjeta de Control inferior (Detalles del parqueo seleccionado con flechas)
                Card(
                    shape = RoundedCornerShape(28.dp),
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
                    border = BorderStroke(1.dp, colorScheme.outline.copy(alpha = 0.12f))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Flecha Izquierda
                            IconButton(
                                onClick = {
                                    if (parqueosList.isNotEmpty()) {
                                        val prevIndex = if (selectedIndex - 1 < 0) parqueosList.size - 1 else selectedIndex - 1
                                        viewModel.onParqueoSelected(parqueosList[prevIndex].nombre)
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                    contentDescription = "Anterior",
                                    modifier = Modifier.size(32.dp),
                                    tint = colorScheme.primary
                                )
                            }

                            // Información Central
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                selectedParqueo?.let { parqueo ->
                                    Text(
                                        text = parqueo.nombre,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        color = colorScheme.primary,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    if (parqueo.disponibles > 0) colorScheme.secondary
                                                    else colorScheme.error
                                                )
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (parqueo.disponibles > 0)
                                                "Disponibles: ${parqueo.disponibles} / ${parqueo.capacidadTotal} espacios"
                                            else
                                                "Lleno · Sin espacios",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (parqueo.disponibles > 0) colorScheme.onSurfaceVariant
                                            else colorScheme.error
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = parqueo.direccion,
                                        fontSize = 11.sp,
                                        color = colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                                        textAlign = TextAlign.Center,
                                        maxLines = 2
                                    )
                                } ?: Text(
                                    "No hay parqueos que coincidan con la búsqueda",
                                    fontSize = 13.sp,
                                    color = colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }

                            // Flecha Derecha
                            IconButton(
                                onClick = {
                                    if (parqueosList.isNotEmpty()) {
                                        val nextIndex = (selectedIndex + 1) % parqueosList.size
                                        viewModel.onParqueoSelected(parqueosList[nextIndex].nombre)
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                    contentDescription = "Siguiente",
                                    modifier = Modifier.size(32.dp),
                                    tint = colorScheme.primary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Botón de Confirmación / Viaje
                        Button(
                            onClick = {
                                selectedParqueo?.let { parqueo ->
                                    if (parqueo.disponibles > 0) {
                                        navegando = true
                                        scope.launch {
                                            delay(1500)
                                            navegando = false
                                            onIrAlParqueo(
                                                parqueo.nombre,
                                                parqueo.direccion,
                                                parqueo.disponibles,
                                                parqueo.latitud ?: 12.108503522103808,
                                                parqueo.longitud ?: -86.25693253419533
                                            )
                                        }
                                    }
                                }
                            },
                            enabled = selectedParqueo != null && selectedParqueo.disponibles > 0,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colorScheme.primary,
                                disabledContainerColor = colorScheme.outline.copy(alpha = 0.5f)
                            )
                        ) {
                            Text(
                                text = if (selectedParqueo != null && selectedParqueo.disponibles == 0) "Parqueo agotado"
                                else "Ir al parqueo seleccionado",
                                color = colorScheme.onPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            }
        }
    }
}