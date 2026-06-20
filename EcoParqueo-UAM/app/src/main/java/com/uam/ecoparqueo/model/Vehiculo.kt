package com.uam.ecoparqueo.model

import com.google.gson.annotations.SerializedName

data class Vehiculo(
	@SerializedName("id") val id: String = "",
	@SerializedName("marca") val marca: String,
	@SerializedName("numero_placa") val numeroPlaca: String,
	@SerializedName("modelo") val modelo: String,
	@SerializedName("anio") val anio: String,
	@SerializedName("color_vehiculo") val colorVehiculo: String,
	@SerializedName("tipo_vehiculo") val tipoVehiculo: String,
	@SerializedName("notas_adicionales") val notasAdicionales: String = ""
)
