package com.uam.ecoparqueo.screen.seguridad

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.uam.ecoparqueo.ui.components.DrawerDestination
import com.uam.ecoparqueo.ui.components.EcoParqueoDrawerScaffold
import com.uam.ecoparqueo.ui.components.ParetoChartCard
import com.uam.ecoparqueo.vmodel.EstadisticasViewModel

@Composable
fun EstadisticasScreen(
    nombreUsuario: String,
    tipoUsuario: String,
    onDrawerNavigate: (DrawerDestination) -> Unit,
    onIrAlPanel: () -> Unit,
    onCerrarSesion: () -> Unit,
    viewModel: EstadisticasViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val colorScheme = MaterialTheme.colorScheme
    val scrollState = rememberScrollState()

    var activeTab by remember { mutableStateOf(0) }
    val tabs = listOf("Resumen", "Zonas", "Historial")

    EcoParqueoDrawerScaffold(
        nombreUsuario = nombreUsuario,
        tipoUsuario = tipoUsuario,
        pantallaActual = DrawerDestination.ESTADISTICAS,
        title = "Estadísticas de Ocupación",
        onNavigate = onDrawerNavigate,
        onIrAlPanel = onIrAlPanel,
        onCerrarSesion = onCerrarSesion
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Estadísticas de Ocupación", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorScheme.primary,
                    titleContentColor = colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(colorScheme.background)
        ) {
            // Segmented control / Tabs para evitar sobrecargar la pantalla
            PrimaryTabRow(
            TabRow(
                selectedTabIndex = activeTab,
                containerColor = colorScheme.surface,
                contentColor = colorScheme.primary
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = activeTab == index,
                        onClick = { activeTab = index },
                        text = { Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp) }
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp)
            ) {
                when (activeTab) {
                    0 -> {
                        // ── TAB 0: RESUMEN Y PARETO ──────────────────────
                        // 1. Resumen General Card
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            shape = RoundedCornerShape(24.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                            colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
                            border = BorderStroke(1.dp, colorScheme.outline.copy(alpha = 0.1f))
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text(
                                    text = "Resumen General",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("Capacidad Total: ${state.totalCapacidad} vehículos", fontSize = 14.sp)
                                Text("Espacios Libres: ${state.totalDisponibles} vacantes", fontSize = 14.sp)
                                Text("Espacios Ocupados: ${state.totalOcupados} ocupados", fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(12.dp))
                                LinearProgressIndicator(
                                    progress = { state.porcentajeOcupacion / 100f },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp),
                                    color = colorScheme.primary,
                                    trackColor = colorScheme.primaryContainer
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Ocupación total: ${String.format(java.util.Locale.getDefault(), "%.1f", state.porcentajeOcupacion)}%",
                                    text = "Ocupación total: ${String.format("%.1f", state.porcentajeOcupacion)}%",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(16.dp))
                                HorizontalDivider(color = colorScheme.outline.copy(alpha = 0.1f), thickness = 1.dp)
                                HorizontalDivider(color = colorScheme.outline.copy(alpha = 0.1f))
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Vehículos Activos (Dentro): ${state.vehiculosDentro}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = colorScheme.onSurface
                                )
                            }
                        }

                        // 2. Gráfico de Pareto real
                        ParetoChartCard(
                            parqueos = state.parqueos,
                            modifier = Modifier.padding(bottom = 20.dp)
                        )
                    }
                    1 -> {
                        // ── TAB 1: OCUPACIÓN POR ZONAS (Carrusel con flechas) ──
                        Text(
                            text = "Ocupación por Zonas de Parqueo",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.primary,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        if (state.parqueos.isEmpty()) {
                            Text(
                                text = "No hay parqueos registrados en el sistema.",
                                color = colorScheme.onSurfaceVariant,
                                fontSize = 13.sp
                            )
                        } else {
                            var selectedIndex by remember { mutableStateOf(0) }
                            val index = selectedIndex.coerceIn(0, state.parqueos.size - 1)
                            val p = state.parqueos[index]

                            val ocupadosP = p.capacidadTotal - p.disponibles
                            val porcP = if (p.capacidadTotal > 0) (ocupadosP.toFloat() / p.capacidadTotal) else 0f

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp),
                                shape = RoundedCornerShape(28.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                                colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
                                border = BorderStroke(1.dp, colorScheme.outline.copy(alpha = 0.1f))
                            ) {
                                Column(modifier = Modifier.padding(24.dp)) {
                                    // Selector de parqueos con flechas izquierda / derecha
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        IconButton(
                                            onClick = {
                                                selectedIndex = if (selectedIndex == 0) state.parqueos.size - 1 else selectedIndex - 1
                                            }
                                        ) {
                                            Text("◀", fontSize = 18.sp, color = colorScheme.primary)
                                        }

                                        Text(
                                            text = p.nombre,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp,
                                            color = colorScheme.primary,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.weight(1f)
                                        )

                                        IconButton(
                                            onClick = {
                                                selectedIndex = (selectedIndex + 1) % state.parqueos.size
                                            }
                                        ) {
                                            Text("▶", fontSize = 18.sp, color = colorScheme.primary)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(24.dp))

                                    Text(
                                        text = "Ocupación: ${ocupadosP} de ${p.capacidadTotal} espacios",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "Disponibles: ${p.disponibles} espacios libres",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = colorScheme.secondary
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    LinearProgressIndicator(
                                        progress = { porcP },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(12.dp)
                                            .clip(RoundedCornerShape(6.dp)),
                                        color = if (porcP > 0.85f) colorScheme.error else colorScheme.secondary,
                                        trackColor = colorScheme.secondaryContainer
                                    )
                                }
                            }
                        }
                        }

                        // 2. Gráfico de Pareto real
                        ParetoChartCard(
                            registros = state.registros,
                            modifier = Modifier.padding(bottom = 20.dp)
                        )
                    }
                    1 -> {
                        // ── TAB 1: OCUPACIÓN POR ZONAS (Carrusel con flechas) ──
                        Text(
                            text = "Ocupación por Zonas de Parqueo",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.primary,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        if (state.parqueos.isEmpty()) {
                            Text(
                                text = "No hay parqueos registrados en el sistema.",
                                color = colorScheme.onSurfaceVariant,
                                fontSize = 13.sp
                            )
                        } else {
                            var selectedIndex by remember { mutableStateOf(0) }
                            val index = selectedIndex.coerceIn(0, state.parqueos.size - 1)
                            val p = state.parqueos[index]

                            val ocupadosP = p.capacidadTotal - p.disponibles
                            val porcP = if (p.capacidadTotal > 0) (ocupadosP.toFloat() / p.capacidadTotal) else 0f

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp),
                                shape = RoundedCornerShape(28.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                                colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
                                border = BorderStroke(1.dp, colorScheme.outline.copy(alpha = 0.1f))
                            ) {
                                Column(modifier = Modifier.padding(24.dp)) {
                                    // Selector de parqueos con flechas izquierda / derecha
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        IconButton(
                                            onClick = {
                                                selectedIndex = if (selectedIndex == 0) state.parqueos.size - 1 else selectedIndex - 1
                                            }
                                        ) {
                                            Text("◀", fontSize = 18.sp, color = colorScheme.primary)
                                        }

                                        Text(
                                            text = p.nombre,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp,
                                            color = colorScheme.primary,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.weight(1f)
                                        )

                                        IconButton(
                                            onClick = {
                                                selectedIndex = (selectedIndex + 1) % state.parqueos.size
                                            }
                                        ) {
                                            Text("▶", fontSize = 18.sp, color = colorScheme.primary)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(24.dp))

                                    Text(
                                        text = "Ocupación: ${ocupadosP} de ${p.capacidadTotal} espacios",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "Disponibles: ${p.disponibles} espacios libres",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = colorScheme.secondary
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    LinearProgressIndicator(
                                        progress = { porcP },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(12.dp)
                                            .clip(RoundedCornerShape(6.dp)),
                                        color = if (porcP > 0.85f) colorScheme.error else colorScheme.secondary,
                                        trackColor = colorScheme.secondaryContainer
                                    )
                                }
                            }
                        }
                    }
                    2 -> {
                        // ── TAB 2: HISTORIAL Y HORAS PICO ────────────────
                        // 1. Horas Pico Card
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            shape = RoundedCornerShape(24.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                            colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
                            border = BorderStroke(1.dp, colorScheme.outline.copy(alpha = 0.1f))
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text(
                                    text = "Horas Pico de Mayor Flujo",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(12.dp))

                                if (state.horasPico.isEmpty()) {
                                    Text("Sin datos registrados", fontSize = 13.sp, color = colorScheme.onSurfaceVariant)
                                } else {
                                    state.horasPico.forEachIndexed { index, pair ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "${index + 1}. Hora: ${pair.first}",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = colorScheme.onSurface
                                            )
                                            Text(
                                                text = "${pair.second} accesos",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = colorScheme.secondary
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // 2. Historial de accesos
                        Text(
                            text = "Historial de Accesos (Soporte)",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.primary,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        if (state.registros.isEmpty()) {
                            Text(
                                text = "No hay registros históricos en el servidor.",
                                color = colorScheme.onSurfaceVariant,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                        } else {
                            state.registros.sortedByDescending { it.fechaHoraIngreso }.take(10).forEach { log ->
                                val formatoHora = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault())
                                val formatoFecha = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
                                val hora = formatoHora.format(java.util.Date(log.fechaHoraIngreso))
                                val fecha = formatoFecha.format(java.util.Date(log.fechaHoraIngreso))
                            state.registros.sortedByDescending { it.fechaHora }.take(10).forEach { log ->
                                val formatoHora = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault())
                                val formatoFecha = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
                                val hora = formatoHora.format(java.util.Date(log.fechaHora))
                                val fecha = formatoFecha.format(java.util.Date(log.fechaHora))

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 8.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(14.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Surface(
                                                color = colorScheme.primaryContainer,
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Text(
                                                    text = log.vehiculoId,
                                                    text = log.placa,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 13.sp,
                                                    color = colorScheme.primary,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = log.parqueoId,
                                                text = log.parqueoNombre,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = colorScheme.onSurface
                                            )
                                        }
                                        Column(horizontalAlignment = Alignment.End) {
                                            Text(
                                                text = hora,
                                                fontSize = 13.sp,
                                                color = colorScheme.primary,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = fecha,
                                                fontSize = 11.sp,
                                                color = colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedButton(
                    onClick = onVolver,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, colorScheme.primary)
                ) {
                    Text("Volver al Panel", fontWeight = FontWeight.Bold, color = colorScheme.primary)
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}