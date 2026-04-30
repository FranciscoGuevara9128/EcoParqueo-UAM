package com.uam.ecoparqueo.ui.screens.seguridad

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ControlAccesoVehicular(nombreParqueo: String, onBack: () -> Unit) {
    var placaText by remember { mutableStateOf("") }
    var placaList by remember { mutableStateOf(listOf<String>()) } // Lista de placas registradas

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Punto: $nombreParqueo", fontSize = 18.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(16.dp))

        // Campo para nueva placa
        TextField(
            value = placaText,
            onValueChange = { placaText = it },
            label = { Text("Número de Placa") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                if (placaText.isNotBlank()) {
                    placaList = placaList + placaText // Agrega a la lista (POO: Inmutabilidad)
                    placaText = "" // Limpia el campo
                }
            },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        ) {
            Text("Registrar Vehículo")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Lista de vehículos registrados (Basado en el ejemplo de TaskItem)
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(placaList) { placa ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Row(modifier = Modifier.padding(16.dp)) {
                        Text("🚗 Placa: ", color = Color.Gray)
                        Text(placa, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Button(onClick = onBack, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text("Volver")
        }
    }
}