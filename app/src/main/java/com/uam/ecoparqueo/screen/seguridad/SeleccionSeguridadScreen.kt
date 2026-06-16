package com.uam.ecoparqueo.screen.seguridad

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SeleccionSeguridadScreen(onParkingSelected: (String) -> Unit) {
    val colorScheme = MaterialTheme.colorScheme
    val options = listOf("Aguja", "Parqueo Plazoleta", "Parqueo Recepción")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(colorScheme.primary, colorScheme.tertiary)
                )
            )
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Seleccione Punto de Control",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = colorScheme.onPrimary,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        options.forEach { location ->
            Button(
                onClick = { onParkingSelected(location) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primary)
            ) {
                Text(location, color = colorScheme.onPrimary)
            }
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}
