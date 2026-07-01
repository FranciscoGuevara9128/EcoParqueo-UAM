package com.uam.ecoparqueo.model

import com.google.gson.annotations.SerializedName

data class RegistroAcceso(
    @SerializedName("id") val id: String? = null,
    @SerializedName("placa") val placa: String,
    @SerializedName("parqueoNombre") val parqueoNombre: String,
    @SerializedName("fechaHora") val fechaHora: Long
)
