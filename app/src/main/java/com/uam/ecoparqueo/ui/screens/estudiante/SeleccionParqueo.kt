package com.uam.ecoparqueo.ui.screens.estudiante

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uam.ecoparqueo.ui.theme.Blanco
import com.uam.ecoparqueo.ui.theme.GrisInactivo
import com.uam.ecoparqueo.ui.theme.GrisSuave
import com.uam.ecoparqueo.ui.theme.GrisTexto
import com.uam.ecoparqueo.ui.theme.VerdeClaro
import com.uam.ecoparqueo.ui.theme.VerdeOscuro
import com.uam.ecoparqueo.ui.theme.VerdePrincipal
import com.uam.ecoparqueo.ui.theme.VerdeSuave
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
    // Lista de parqueos (A..F) con cantidad disponible y dirección.
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

    // tab 0: parqueos con cupo, tab 1: parqueos sin cupo, otro: todos.
    val parqueosFiltrados = when (tab) {
        0 -> parqueos.filter { it.available > 0 }
        1 -> parqueos.filter { it.available == 0 }
        else -> parqueos
    }

    // Conteo total de parqueos con disponibilidad > 0.
    // Hacemos este contador mutable para poder decrementar únicamente el total mostrado
    // cuando el usuario confirme/visite un parqueo (sin modificar la lista original).
    var totalDisponibles by remember { mutableStateOf(parqueos.count { it.available > 0 }) }

    val checkboxColors = CheckboxDefaults.colors(
        checkedColor = VerdeOscuro,
        uncheckedColor = GrisInactivo,
        checkmarkColor = Color.White
    )

    when (pantallaActual) {
        PantallaParqueo.CARGANDO -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                VerdeOscuro,
                                VerdeClaro
                            )
                        )
                    )
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(color = Blanco)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Cargando dirección",
                    color = Blanco,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        PantallaParqueo.MOSTRAR -> {
            val parqueoSeleccionado = parqueos.find { it.name == selectedName }
            if (parqueoSeleccionado == null) {
                pantallaActual = PantallaParqueo.SELECCION
            } else {
                MostrarParqueo(
                    parqueo = parqueoSeleccionado,
                    onVolver = {
                        selectedName = null
                        pantallaActual = PantallaParqueo.SELECCION
                    }
                )
            }
        }

        PantallaParqueo.SELECCION -> Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            VerdeOscuro,
                            VerdeClaro
                        )
                    )
                )
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Selecciona un parqueo",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Blanco
            )
            Text(
                text = "Elige una opción disponible para continuar con la navegación.",
                color = Blanco.copy(alpha = 0.9f)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Card(
                shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Blanco),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Espacio reservado para mapa\n(Google Maps API)",
                        color = GrisTexto
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = VerdeSuave),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Parqueos disponibles: $totalDisponibles",
                        fontWeight = FontWeight.Bold,
                        color = VerdeOscuro
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = when (tab) {
                            0 -> "Mostrando parqueos con cupo"
                            1 -> "Mostrando parqueos sin cupo"
                            else -> "Mostrando todos los parqueos"
                        },
                        color = GrisTexto
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    // Simulación simple de actualización de disponibilidad.
                    scope.launch {
                        loading = true
                        delay(600)
                        parqueos = parqueos.shuffled()
                        // Recalcula el total disponible después de actualizar la lista
                        totalDisponibles = parqueos.count { it.available > 0 }
                        loading = false
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = VerdeOscuro)
            ) {
                if (loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = Blanco
                    )
                } else {
                    Text("Actualizar disponibilidad")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Lista de parqueos",
                fontWeight = FontWeight.Bold,
                color = Blanco
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(330.dp)
            ) {
                items(parqueosFiltrados) { parqueo ->
                    Card(
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(18.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .clickable(enabled = parqueo.available > 0) {
                                // Selección exclusiva (solo si hay cupo).
                                if (parqueo.available > 0) {
                                    selectedName =
                                        if (selectedName == parqueo.name) null else parqueo.name
                                }
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = when {
                                selectedName == parqueo.name -> VerdeSuave
                                parqueo.available == 0 -> GrisSuave
                                else -> Blanco
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
                            Column {
                                Text(
                                    parqueo.name,
                                    fontWeight = FontWeight.Bold,
                                    color = if (parqueo.available > 0) VerdeOscuro else GrisTexto
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Dirección: ${parqueo.address}",
                                    color = if (parqueo.available > 0) GrisTexto else GrisTexto.copy(alpha = 0.7f)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Disponibles: ${parqueo.available}",
                                    color = if (parqueo.available > 0) VerdePrincipal else GrisTexto.copy(alpha = 0.7f)
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
                                    enabled = parqueo.available > 0,
                                    colors = checkboxColors
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
                        // Al iniciar la navegación al parqueo, decrementamos la disponibilidad
                        // del parqueo seleccionado (si tiene > 0) y actualizamos el contador
                        // mostrado. Esto asegura que el cambio se vea inmediatamente.
                        if (selectedName != null) {
                            parqueos = parqueos.map { p ->
                                if (p.name == selectedName && p.available > 0) p.copy(available = p.available - 1)
                                else p
                            }
                            totalDisponibles = parqueos.count { it.available > 0 }
                        }

                        pantallaActual = PantallaParqueo.CARGANDO
                        delay(2000)
                        pantallaActual = PantallaParqueo.MOSTRAR
                    }
                },
                enabled = selectedName != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = VerdeOscuro,
                    disabledContainerColor = GrisInactivo
                )
            ) {
                Text("Ir al parqueo")
            }
        }
    }
}
