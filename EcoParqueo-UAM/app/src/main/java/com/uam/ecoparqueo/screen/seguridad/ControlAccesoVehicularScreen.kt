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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.uam.ecoparqueo.vmodel.ControlAccesoViewModel

@Composable
fun ControlAccesoVehicularScreen(
    nombreParqueo: String,
    onBack: () -> Unit,
    viewModel: ControlAccesoViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val colorScheme = MaterialTheme.colorScheme

    val textFieldColors = TextFieldDefaults.colors(
        focusedTextColor = colorScheme.onPrimary,
        unfocusedTextColor = colorScheme.onPrimary,
        focusedContainerColor = colorScheme.primary.copy(alpha = 0.3f),
        unfocusedContainerColor = colorScheme.primary.copy(alpha = 0.2f),
        cursorColor = colorScheme.onPrimary,
        focusedIndicatorColor = colorScheme.onPrimary,
        unfocusedIndicatorColor = colorScheme.onPrimary.copy(alpha = 0.6f),
        errorIndicatorColor = colorScheme.error,
        focusedLabelColor = colorScheme.onPrimary,
        unfocusedLabelColor = colorScheme.onPrimary.copy(alpha = 0.8f),
        errorLabelColor = colorScheme.error,
        focusedSupportingTextColor = colorScheme.onPrimary,
        errorSupportingTextColor = colorScheme.onPrimary
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(colorScheme.primary, colorScheme.tertiary)
                )
            )
            .padding(16.dp)
    ) {
        Text(
            "Punto: $nombreParqueo",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = colorScheme.onPrimary
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = state.placaText,
            onValueChange = { viewModel.onPlacaTextChange(it) },
            label = { Text("Número de Placa", color = colorScheme.onPrimary) },
            isError = state.placaText.isNotBlank() && state.placaError,
            modifier = Modifier.fillMaxWidth(),
            colors = textFieldColors,
            supportingText = {
                if (state.placaText.isNotBlank() && state.placaError) {
                    Text(
                        "Ingrese una placa válida (6-10 caracteres, letras y números)",
                        color = colorScheme.onPrimary
                    )
                }
            }
        )

        Button(
            onClick = { viewModel.registrarPlaca() },
            enabled = state.placaText.isNotBlank() && !state.placaError,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorScheme.surface,
                contentColor = colorScheme.primary,
                disabledContainerColor = colorScheme.outline,
                disabledContentColor = colorScheme.onPrimary
            )
        ) {
            Text("Registrar Vehículo")
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(state.placaList) { placa ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = colorScheme.primaryContainer),
                    elevation = CardDefaults.cardElevation(4.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(modifier = Modifier.padding(16.dp)) {
                        Text("🚗 Placa: ", color = colorScheme.onSurfaceVariant)
                        Text(placa, fontWeight = FontWeight.Bold, color = colorScheme.primary)
                    }
                }
            }
        }

        Button(
            onClick = onBack,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorScheme.surface,
                contentColor = colorScheme.primary
            )
        ) {
            Text("Volver")
        }
    }
}