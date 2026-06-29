package com.uam.ecoparqueo.model.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "vehiculos",
    foreignKeys = [
        ForeignKey(
            entity = UsuarioEntity::class,
            parentColumns = ["id"],
            childColumns = ["usuarioId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["usuarioId"])]
)
data class VehiculoEntity(
    @PrimaryKey val id: String,
    val usuarioId: String,
    val numeroPlaca: String,
    val marca: String,
    val modelo: String,
    val anio: String,
    val colorVehiculo: String,
    val tipoVehiculo: String,
    val notasAdicionales: String
)
