package com.uam.ecoparqueo.data

data class Vehiculo(
	val marca: String,
	val numeroPlaca: String,
	val modelo: String,
	val colorVehiculo: String,
	val tipoVehiculo: String,
	val notasAdicionales: String = ""
)
