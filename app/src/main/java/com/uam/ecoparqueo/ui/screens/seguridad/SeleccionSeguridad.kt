package com.uam.ecoparqueo.ui.screens.seguridad

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SeleccionSeguridad(onParkingSelected: (String) -> Unit) {
    val options = listOf("Aguja", "Parqueo Plazoleta", "Parqueo Recepción")

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Seleccione Punto de Control", fontSize = 20.sp, modifier = Modifier.padding(bottom = 20.dp))

        options.forEach { location ->
            Button(
                onClick = { onParkingSelected(location) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            ) {
                Text(location)
            }
        }
    }
}