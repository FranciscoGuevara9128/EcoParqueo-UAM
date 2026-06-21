package com.uam.ecoparqueo.model

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("nombre") val nombre: String,
    @SerializedName("contrasena") val contrasena: String
)
