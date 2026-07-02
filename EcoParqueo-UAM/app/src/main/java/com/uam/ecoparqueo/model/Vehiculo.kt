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
    @SerializedName("marca") val marca: String? = null,
    @SerializedName("numero_placa") val numeroPlaca: String,
    @SerializedName("modelo") val modelo: String? = null,
    @SerializedName("anio") val anio: String? = null,
    @SerializedName("color_vehiculo") val colorVehiculo: String? = null,
    @SerializedName("tipo_vehiculo") val tipoVehiculo: String? = null,
    @SerializedName("notas_adicionales") val notasAdicionales: String? = null,
    // nullable: se omite si es null (Gson omite campos null por defecto con serializeNulls=false)
    @SerializedName("usuario") val usuario: UsuarioRef? = null
)
