package com.uam.ecoparqueo.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class UsuarioEntity(
    @PrimaryKey val id: String,
    val nombre: String,
    val tipoUsuario: String,
    val fechaRegistro: Long = System.currentTimeMillis()
)
