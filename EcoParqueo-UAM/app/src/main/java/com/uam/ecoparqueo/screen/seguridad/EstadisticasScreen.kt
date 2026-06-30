package com.uam.ecoparqueo.screen.seguridad

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.uam.ecoparqueo.ui.components.ParetoChartCard
import com.uam.ecoparqueo.vmodel.EstadisticasViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EstadisticasScreen(
    onVolver: () -> Unit,
    viewModel: EstadisticasViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val colorScheme = MaterialTheme.colorScheme
    val scrollState = rememberScrollState()

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
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .background(colorScheme.background)
                .padding(16.dp)
        ) {
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
                        text = "Ocupación total: ${String.format("%.1f", state.porcentajeOcupacion)}%",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(16.dp))
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

            // 2. Gráfico de Pareto (Buscado por el usuario aquí en estadísticas)
            ParetoChartCard(
                parqueos = state.parqueos,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            // 3. Ocupación por Parqueo List
            Text(
                text = "Ocupación por Zonas",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = colorScheme.primary,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            state.parqueos.forEach { p ->
                val ocupadosP = p.capacidadTotal - p.disponibles
                val porcP = if (p.capacidadTotal > 0) (ocupadosP.toFloat() / p.capacidadTotal) else 0f

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
                    border = BorderStroke(1.dp, colorScheme.outline.copy(alpha = 0.08f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = p.nombre,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${ocupadosP}/${p.capacidadTotal}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = colorScheme.primary,
                                maxLines = 1
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Libres: ${p.disponibles}",
                            fontSize = 12.sp,
                            color = colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { porcP },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp),
                            color = if (porcP > 0.85f) colorScheme.error else colorScheme.secondary,
                            trackColor = colorScheme.secondaryContainer
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            
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
