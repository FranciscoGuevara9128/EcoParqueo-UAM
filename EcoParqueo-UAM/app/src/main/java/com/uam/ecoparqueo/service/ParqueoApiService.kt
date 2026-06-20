package com.uam.ecoparqueo.service

import com.uam.ecoparqueo.model.Parqueo
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ParqueoApiService {

    @GET("parqueo/all")
    suspend fun findAll(): Response<List<Parqueo>>

    @GET("parqueo/get/{id}")
    suspend fun findById(@Path("id") id: String): Response<Parqueo>

    @POST("parqueo/save")
    suspend fun save(@Body parqueo: Parqueo): Response<Parqueo>

    @PUT("parqueo/update")
    suspend fun update(@Body parqueo: Parqueo): Response<Parqueo>

    @DELETE("parqueo/delete/{id}")
    suspend fun delete(@Path("id") id: String): Response<Void>
}
