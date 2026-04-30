package com.uam.ecoparqueo.ui.screens.estudiante

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
			Button(onClick = onVolver) {
				Text("<- Volver")
			}
		}

		Spacer(modifier = Modifier.height(12.dp))

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

		Spacer(modifier = Modifier.height(20.dp))
		Text(parqueo.name, style = MaterialTheme.typography.headlineSmall)
		Spacer(modifier = Modifier.height(8.dp))
		Text("Direccion: ${parqueo.address}")
		Spacer(modifier = Modifier.height(8.dp))
		Text("Parqueos disponibles: ${parqueo.available}")
	}
}

