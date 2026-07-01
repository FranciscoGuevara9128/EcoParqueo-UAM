package com.uam.ecoparqueo.service

import com.uam.ecoparqueo.model.RegistroAcceso
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface RegistroAccesoApiService {

    @GET("registro-acceso/all")
    suspend fun findAll(): Response<List<RegistroAcceso>>

    @POST("registro-acceso/save")
    suspend fun registrarAcceso(@Body request: RegistroAcceso): Response<RegistroAcceso>
}
