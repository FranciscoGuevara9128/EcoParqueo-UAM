package com.uam.ecoparqueo.navigation

import kotlinx.serialization.Serializable

@kotlinx.serialization.Serializable
object SecuritySelectionScreen // Pantalla 1 del Guarda

@Serializable
data class ControlAccesoVehicularScreen(
    val nombreParqueo: String // Pasa el nombre del parqueo seleccionado
)