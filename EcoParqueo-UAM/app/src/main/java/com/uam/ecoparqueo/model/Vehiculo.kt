package com.uam.ecoparqueo.model

import com.google.gson.annotations.SerializedName

/**
 * DTO de referencia mínima de usuario que el backend acepta en el campo "usuario"
 * al crear o actualizar un vehículo. Solo se necesita el id (UUID) para satisfacer
 * la relación @ManyToOne en la entidad Vehiculo del servidor.
 */
data class UsuarioRef(
    @SerializedName("id") val id: String
)

data class Vehiculo(
    @SerializedName("id") val id: String? = null,
    @SerializedName("marca") val marca: String,
    @SerializedName("numero_placa") val numeroPlaca: String,
    @SerializedName("modelo") val modelo: String,
    @SerializedName("anio") val anio: String,
    @SerializedName("color_vehiculo") val colorVehiculo: String,
    @SerializedName("tipo_vehiculo") val tipoVehiculo: String,
    @SerializedName("notas_adicionales") val notasAdicionales: String = "",
    // nullable: se omite si es null (Gson omite campos null por defecto con serializeNulls=false)
    @SerializedName("usuario") val usuario: UsuarioRef? = null
)
