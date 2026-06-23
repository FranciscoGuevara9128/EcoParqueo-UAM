package com.uam.ecoparqueo.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "parqueos")
data class ParqueoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val capacidadTotal: Int,
    val disponibles: Int,
    val direccion: String,
    val latitud: Double? = null,
    val longitud: Double? = null
)
