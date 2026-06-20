package com.uam.ecoparqueo.model

import com.google.gson.annotations.SerializedName

data class Parqueo(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("name") val name: String,
    @SerializedName("available") val available: Int,
    @SerializedName("address") val address: String
)