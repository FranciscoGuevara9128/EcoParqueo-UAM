package com.uam.ecoparqueo.screen.seguridad

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.uam.ecoparqueo.vmodel.EstadisticasViewModel

@Composable
fun EstadisticasScreen(
    onVolver: () -> Unit,
    viewModel: EstadisticasViewModel = viewModel()
) {
    val parqueos by viewModel.parqueosStats.collectAsState()
    val vehiculosDentro by viewModel.vehiculosDentro.collectAsState()

    val totalCapacidad = parqueos.sumOf { it.capacidadTotal }
    val totalDisponibles = parqueos.sumOf { it.disponibles }
    val totalOcupados = totalCapacidad - totalDisponibles

    val porcentajeOcupacion = if (totalCapacidad > 0) (totalOcupados.toFloat() / totalCapacidad * 100) else 0f

    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text("Estadísticas del Día") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {

            Card(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Resumen General", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Capacidad Total: $totalCapacidad")
                    Text("Espacios Libres: $totalDisponibles")
                    Text("Espacios Ocupados: $totalOcupados")
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { porcentajeOcupacion / 100f },
                        modifier = Modifier.fillMaxWidth().height(8.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Ocupación: ${String.format("%.1f", porcentajeOcupacion)}%", style = MaterialTheme.typography.bodySmall)

                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Vehículos Registrados (Dentro): $vehiculosDentro", fontWeight = FontWeight.Bold)
                }
            }

            Text("Ocupación por Parqueo", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(parqueos) { p ->
                    val ocupadosP = p.capacidadTotal - p.disponibles
                    val porcP = if (p.capacidadTotal > 0) (ocupadosP.toFloat() / p.capacidadTotal) else 0f

                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(p.nombre, fontWeight = FontWeight.Bold)
                            Text("Libres: ${p.disponibles} / ${p.capacidadTotal}")
                            Spacer(modifier = Modifier.height(8.dp))
                            LinearProgressIndicator(
                                progress = { porcP },
                                modifier = Modifier.fillMaxWidth().height(6.dp),
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onVolver, modifier = Modifier.fillMaxWidth()) {
                Text("Volver")
            }
        }
    }
}
