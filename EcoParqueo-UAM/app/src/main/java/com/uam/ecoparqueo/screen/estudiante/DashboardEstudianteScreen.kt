package com.uam.ecoparqueo.screen.estudiante

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DashboardEstudianteScreen(
    nombreUsuario: String,
    onNavigateToGestionVehiculos: () -> Unit,
    onNavigateToSeleccionParqueo: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Bienvenido, $nombreUsuario", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Panel de Estudiante", style = MaterialTheme.typography.headlineMedium)

        Button(
            onClick = onNavigateToGestionVehiculos,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text("Mis Vehículos")
        }
        Button(
            onClick = onNavigateToSeleccionParqueo,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text("Buscar Parqueo")
        }

        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onCerrarSesion,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError
            )
        ) {
            Text("Cerrar Sesión")
        }
    }
}
