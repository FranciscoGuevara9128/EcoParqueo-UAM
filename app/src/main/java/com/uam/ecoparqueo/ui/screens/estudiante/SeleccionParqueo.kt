package com.uam.ecoparqueo.ui.screens.estudiante

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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class Parqueo(val name: String, val available: Int, val address: String)
private enum class PantallaParqueo {
    SELECCION,
    CARGANDO,
    MOSTRAR
}

@Composable
fun SeleccionParqueo(tab: Int) {
    // Lista de parqueos (A..F) con cantidad disponible y dirección
    val parqueosIniciales = listOf(
        Parqueo("Parqueo A", 50, "Parqueo contiguo a Bancentro, en frente de recepcion"),
        Parqueo("Parqueo B", 35, "Parqueo de tierra, ubicado al lado del chilamate"),
        Parqueo("Parqueo C", 0, "Parqueo detras de clinicas odontologicas"),
        Parqueo("Parqueo D", 8, "Parqueo detras del edificio P"),
        Parqueo("Parqueo E", 0, "Parqueo al lado del food"),
        Parqueo("Parqueo F", 20, "Parqueo detras de biblioteca")
    )

    var parqueos by remember { mutableStateOf(parqueosIniciales) }
    var selectedName by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }
    var pantallaActual by remember { mutableStateOf(PantallaParqueo.SELECCION) }
    val scope = rememberCoroutineScope()

    // tab 0: parqueos con cupo, tab 1: parqueos sin cupo, otro: todos
    val parqueosFiltrados = when (tab) {
        0 -> parqueos.filter { it.available > 0 }
        1 -> parqueos.filter { it.available == 0 }
        else -> parqueos
    }

    // Conteo total de parqueos con disponibilidad > 0
    val totalDisponibles = parqueos.count { it.available > 0 }

    when (pantallaActual) {
        PantallaParqueo.CARGANDO -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(12.dp))
                Text("Cargando dirección")
            }
        }

        PantallaParqueo.MOSTRAR -> {
            val parqueoSeleccionado = parqueos.find { it.name == selectedName }
            if (parqueoSeleccionado != null) {
                MostrarParqueo(
                    parqueo = parqueoSeleccionado,
                    onVolver = {
                        parqueos = parqueos.map {
                            if (it.name == parqueoSeleccionado.name) {
                                it.copy(available = if (it.available > 0) it.available - 1 else 0)
                            } else it
                        }
                        selectedName = null
                        pantallaActual = PantallaParqueo.SELECCION
                    }
                )
            } else {
                pantallaActual = PantallaParqueo.SELECCION
            }
        }

        PantallaParqueo.SELECCION -> Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Espacio reservado para mapa (Google Maps API)")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text("Parqueos disponibles: $totalDisponibles", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(12.dp))

        Button(onClick = {
            // Recalcula aleatoriamente (simulación) las disponibilidades
            scope.launch {
                loading = true
                delay(600)
                parqueos = parqueos.shuffled()
                loading = false
            }
        }, modifier = Modifier.fillMaxWidth()) {
            if (loading) CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
            else Text("Actualizar disponibilidad")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = when (tab) {
                0 -> "Mostrando parqueos con cupo"
                1 -> "Mostrando parqueos sin cupo"
                else -> "Mostrando todos los parqueos"
            },
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(parqueosFiltrados) { parqueo ->
                Card(modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .clickable(enabled = parqueo.available > 0) {
                        // Selección exclusiva (solo si hay cupo)
                        if (parqueo.available > 0) {
                            selectedName = if (selectedName == parqueo.name) null else parqueo.name
                        }
                    }
                ) {
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp), verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            val disabledColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                            Text(
                                parqueo.name,
                                style = MaterialTheme.typography.titleMedium,
                                color = if (parqueo.available > 0) MaterialTheme.colorScheme.onSurface else disabledColor
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Disponibles: ${parqueo.available}",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (parqueo.available > 0) MaterialTheme.colorScheme.onSurface else disabledColor
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = selectedName == parqueo.name,
                                onCheckedChange = { checked ->
                                    if (parqueo.available > 0) {
                                        selectedName = if (checked) parqueo.name else null
                                    }
                                },
                                enabled = parqueo.available > 0
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                scope.launch {
                    pantallaActual = PantallaParqueo.CARGANDO
                    delay(2000)
                    pantallaActual = PantallaParqueo.MOSTRAR
                }
            },
            enabled = selectedName != null,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ir al parqueo")
        }
    }
    }
}
