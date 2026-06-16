package com.uam.ecoparqueo.model.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "registros_acceso",
    foreignKeys = [
        ForeignKey(
            entity = VehiculoEntity::class,
            parentColumns = ["id"],
            childColumns = ["vehiculoId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ParqueoEntity::class,
            parentColumns = ["id"],
            childColumns = ["parqueoId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class RegistroAccesoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val vehiculoId: Int,
    val parqueoId: Int,
    val fechaHoraIngreso: Long = System.currentTimeMillis(),
    val fechaHoraSalida: Long? = null,
    val estado: String // "DENTRO", "FUERA"
)
