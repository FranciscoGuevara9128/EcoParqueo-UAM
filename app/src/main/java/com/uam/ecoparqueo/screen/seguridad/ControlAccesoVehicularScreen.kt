package com.uam.ecoparqueo.screen.seguridad

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uam.ecoparqueo.ui.theme.Blanco
import com.uam.ecoparqueo.ui.theme.GrisTexto
import com.uam.ecoparqueo.ui.theme.VerdeClaro
import com.uam.ecoparqueo.ui.theme.VerdeOscuro
import com.uam.ecoparqueo.ui.theme.VerdeSuave

@Composable
fun ControlAccesoVehicular(nombreParqueo: String, onBack: () -> Unit) {
    var placaText by remember { mutableStateOf("") }
    var placaList by remember { mutableStateOf(listOf<String>()) } // Lista de placas registradas

    // Validacion de placa (Ejemplo simple, se puede mejorar con regex)
    val placaNormalized = placaText.trim().uppercase()
    val placaError =
        placaNormalized.isBlank() || !placaNormalized.matches(Regex("^[A-Z0-9-]{6,10}$"))

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
    ) {
        Text(
            "Punto: $nombreParqueo",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Blanco
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Campo para nueva placa
        TextField(
            value = placaText,
            onValueChange = { placaText = it },
            label = { Text("Número de Placa", color = Blanco) },
            isError = placaError,
            modifier = Modifier.fillMaxWidth(),
            supportingText = {
                if (placaError) {
                    Text("Ingrese una placa válida (6-10 caracteres, letras y números)", color = Blanco)
                }
            }
        )

        Button(
            onClick = {
                if (placaText.isNotBlank()) {
                    placaList = placaList + placaText // Agrega a la lista (POO: Inmutabilidad)
                    placaText = "" // Limpia el campo
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = VerdeOscuro)
        ) {
            Text("Registrar Vehículo", color = Blanco)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Lista de vehículos registrados (Basado en el ejemplo de TaskItem)
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(placaList) { placa ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = VerdeSuave),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Row(modifier = Modifier.padding(16.dp)) {
                        Text("🚗 Placa: ", color = GrisTexto)
                        Text(placa, fontWeight = FontWeight.Bold, color = VerdeOscuro)
                    }
                }
            }
        }

        Button(
            onClick = onBack,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = VerdeOscuro)
        ) {
            Text("Volver", color = Blanco)
        }
    }
}