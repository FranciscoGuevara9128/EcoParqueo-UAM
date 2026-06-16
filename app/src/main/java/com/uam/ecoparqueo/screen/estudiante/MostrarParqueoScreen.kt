package com.uam.ecoparqueo.screen.estudiante

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import com.uam.ecoparqueo.ui.theme.Blanco
import com.uam.ecoparqueo.ui.theme.GrisTexto
import com.uam.ecoparqueo.ui.theme.VerdeOscuro
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MostrarParqueo(parqueo: Parqueo, onVolver: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Button(onClick = onVolver, colors = ButtonDefaults.buttonColors(containerColor = VerdeOscuro)) {
                Text("Volver", color = Blanco)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp),
            colors = CardDefaults.cardColors(containerColor = Blanco)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Espacio reservado para mapa (Google Maps API)", color = GrisTexto)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        Text(parqueo.name, style = MaterialTheme.typography.headlineSmall, color = VerdeOscuro)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Direccion: ${parqueo.address}", color = GrisTexto)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Parqueos disponibles: ${parqueo.available}", color = GrisTexto)
    }
}


