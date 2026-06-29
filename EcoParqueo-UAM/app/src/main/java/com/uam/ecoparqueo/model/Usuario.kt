package com.uam.ecoparqueo.model

import com.google.gson.annotations.SerializedName

data class Usuario(
    @SerializedName("id") val id: String? = null,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("tipoUsuario") val tipoUsuario: String,
    @SerializedName("contrasena") val contrasena: String? = null,
    @SerializedName("token") val token: String? = null
)
