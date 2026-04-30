package com.uam.ecoparqueo.ui.screens.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@Composable
fun AppScreen(modifier: Modifier = Modifier) {

    var logged by remember { mutableStateOf(false) }
    var tipoUsuario by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {

        AnimatedVisibility(
            visible = !logged,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            LoginScreen(
                onLoginSuccess = { usuarioSeleccionado ->
                    tipoUsuario = usuarioSeleccionado
                    logged = true
                }
            )
        }

        AnimatedVisibility(
            visible = logged,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            if (tipoUsuario == "Estudiante") {
                // Luego aquí irá la pantalla de Editar Perfil
                HomeTemporalScreen(
                    texto = "Bienvenido Estudiante",
                    onLogout = {
                        logged = false
                        tipoUsuario = ""
                    }
                )
            } else {
                // Luego aquí irá la pantalla del Guarda de Seguridad
                HomeTemporalScreen(
                    texto = "Bienvenido Guarda de Seguridad",
                    onLogout = {
                        logged = false
                        tipoUsuario = ""
                    }
                )
            }
        }
    }
}