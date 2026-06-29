package com.uam.ecoparqueo.model

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    @SerializedName("token") val token: String,
    @SerializedName("usuario") val usuario: Usuario
)
