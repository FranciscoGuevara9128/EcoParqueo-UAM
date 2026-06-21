package com.uam.ecoparqueo.model

import com.google.gson.annotations.SerializedName

data class Usuario(
    @SerializedName("id") val id: String? = null,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("tipoUsuario") val tipoUsuario: String
    // contrasena no se recibe: el backend la omite con @JsonProperty(WRITE_ONLY)
)
