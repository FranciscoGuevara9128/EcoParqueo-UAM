package com.uam.ecoparqueo.model

import com.google.gson.annotations.SerializedName

data class Parqueo(
    @SerializedName("id") val id: String? = null,
    @SerializedName("name") val name: String,
    @SerializedName("capacidadTotal") val capacidadTotal: Int,
    @SerializedName("disponibles") val disponibles: Int,
    @SerializedName("direccion") val direccion: String
)