package com.uam.ecoparqueo.screen.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.uam.ecoparqueo.vmodel.RegistroUsuarioViewModel

@Composable
fun RegistroUsuarioScreen(
    onVolverAlLogin: () -> Unit,
    viewModel: RegistroUsuarioViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val colorScheme = MaterialTheme.colorScheme
    val focusManager = LocalFocusManager.current

    LaunchedEffect(state.registroExitoso) {
        if (state.registroExitoso) {
            onVolverAlLogin()
            viewModel.resetRegistro()
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
        Spacer(modifier = Modifier.height(50.dp))

        // Logo
        Box(
            modifier = Modifier
                .size(90.dp)
                .clip(CircleShape)
                .background(colorScheme.surface),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "EP",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Crear Cuenta",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = colorScheme.onPrimary
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Regístrate en EcoParqueo UAM",
            fontSize = 15.sp,
            color = colorScheme.onPrimary
        )

        Spacer(modifier = Modifier.height(25.dp))

        // Formulario
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
                    text = "Registro de usuario",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Campo Nombre
                OutlinedTextField(
                    value = state.nombre,
                    onValueChange = { viewModel.onNombreChange(it) },
                    label = { Text("Nombre de usuario") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Campo Contraseña
                OutlinedTextField(
                    value = state.contrasena,
                    onValueChange = { viewModel.onContrasenaChange(it) },
                    label = { Text("Contraseña (mínimo 4 caracteres)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = if (state.contrasenaVisible)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            viewModel.onRegistrar()
                        }
                    ),
                    trailingIcon = {
                        IconButton(onClick = { viewModel.onToggleContrasenaVisible() }) {
                            Icon(
                                imageVector = if (state.contrasenaVisible)
                                    Icons.Filled.Visibility
                                else
                                    Icons.Filled.VisibilityOff,
                                contentDescription = "Mostrar/Ocultar contraseña",
                                tint = colorScheme.onSurfaceVariant
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Mensaje de Error
                if (state.mensajeError.isNotBlank()) {
                    Text(
                        text = state.mensajeError,
                        color = colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Botón registrar
                Button(
                    onClick = { viewModel.onRegistrar() },
                    enabled = state.isFormValid && !state.loading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primary)
                ) {
                    if (state.loading) {
                        CircularProgressIndicator(
                            color = colorScheme.onPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text("Registrarse", color = colorScheme.onPrimary)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Enlace volver al login
                Text(
                    text = "¿Ya tienes cuenta? Inicia sesión aquí",
                    color = colorScheme.primary,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .clickable { onVolverAlLogin() }
                )
            }
        }
    }
}
