package com.uam.ecoparqueo.service

import com.uam.ecoparqueo.model.Vehiculo
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface VehiculoApiService {

    @GET("vehiculo/all")
    suspend fun findAll(): Response<List<Vehiculo>>

    @GET("vehiculo/getId/{id}")
    suspend fun findById(@Path("id") id: String): Response<Vehiculo>

    @GET("vehiculo/placa/{placa}")
    suspend fun findByPlaca(@Path("placa") placa: String): Response<Vehiculo>


    @POST("vehiculo/save")
    suspend fun save(@Body vehiculo: Vehiculo): Response<Vehiculo>

    @PUT("vehiculo/update")
    suspend fun update(@Body vehiculo: Vehiculo): Response<Vehiculo>

    @DELETE("vehiculo/delete/{id}")
    suspend fun delete(@Path("id") id: String): Response<Void>
}
