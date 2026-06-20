package com.uam.ecoparqueo.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class UsuarioEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val tipoUsuario: String,
    val fechaRegistro: Long = System.currentTimeMillis()
)
