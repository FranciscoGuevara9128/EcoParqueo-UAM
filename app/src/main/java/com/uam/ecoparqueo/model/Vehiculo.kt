package com.uam.ecoparqueo.model

data class Vehiculo(
	val marca: String,
	val numeroPlaca: String,
	val modelo: String,
	var anio: String,
	val colorVehiculo: String,
	val tipoVehiculo: String,
	val notasAdicionales: String = ""
)