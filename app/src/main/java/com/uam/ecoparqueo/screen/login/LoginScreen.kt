package com.uam.ecoparqueo.screen.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.uam.ecoparqueo.vmodel.LoginViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: (String) -> Unit,
    viewModel: LoginViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val colorScheme = MaterialTheme.colorScheme

    LaunchedEffect(state.loginExitoso) {
        if (state.loginExitoso) {
            onLoginSuccess(state.tipoUsuario)
            viewModel.onLoginHandled()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(colorScheme.primary, colorScheme.tertiary)
                )
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(70.dp))

        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(colorScheme.surface),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "EP",
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "EcoParqueo UAM",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = colorScheme.onPrimary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Sistema de gestión de parqueo",
            fontSize = 16.sp,
            color = colorScheme.onPrimary
        )

        Spacer(modifier = Modifier.height(35.dp))

        Card(
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            colors = CardDefaults.cardColors(containerColor = colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Inicio de sesión",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = state.nombre,
                    onValueChange = { viewModel.onNombreChange(it) },
                    label = { Text("Nombre del usuario") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Seleccione tipo de usuario:",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { viewModel.onTipoUsuarioChange("Estudiante") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (state.tipoUsuario == "Estudiante")
                            colorScheme.primary
                        else
                            colorScheme.outline
                    )
                ) {
                    Text("Estudiante", color = colorScheme.onPrimary)
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = { viewModel.onTipoUsuarioChange("Guarda") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (state.tipoUsuario == "Guarda")
                            colorScheme.primary
                        else
                            colorScheme.outline
                    )
                ) {
                    Text("Guarda de seguridad", color = colorScheme.onPrimary)
                }

                Spacer(modifier = Modifier.height(20.dp))

                if (state.mensajeError.isNotBlank()) {
                    Text(
                        text = state.mensajeError,
                        color = colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                Button(
                    onClick = { viewModel.onLogin() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorScheme.primary
                    )
                ) {
                    if (state.loading) {
                        CircularProgressIndicator(
                            color = colorScheme.onPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text("Ingresar", color = colorScheme.onPrimary)
                    }
                }
            }
        }
    }
}
