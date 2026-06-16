package com.uam.ecoparqueo.service

import com.uam.ecoparqueo.model.Vehiculo
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface VehiculoApiService {

    @GET("vehiculo/all")
    suspend fun findAll(): Response<List<Vehiculo>>

    @GET("vehiculo/getId/{id}")
    suspend fun findById(@Path("id") id: Int): Response<Vehiculo>

    @POST("vehiculo/save")
    suspend fun save(@Body vehiculo: Vehiculo): Response<Vehiculo>
}
