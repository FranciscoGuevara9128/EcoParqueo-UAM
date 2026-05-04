package com.uam.ecoparqueo.ui.screens.seguridad

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uam.ecoparqueo.ui.theme.Blanco
import com.uam.ecoparqueo.ui.theme.VerdeClaro
import com.uam.ecoparqueo.ui.theme.VerdeOscuro

@Composable
fun SeleccionSeguridad(onParkingSelected: (String) -> Unit) {
    val options = listOf("Aguja", "Parqueo Plazoleta", "Parqueo Recepción")

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
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Seleccione Punto de Control",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Blanco,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        options.forEach { location ->
            Button(
                onClick = { onParkingSelected(location) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = VerdeOscuro)
            ) {
                Text(location, color = Blanco)
            }
        }
    }
}