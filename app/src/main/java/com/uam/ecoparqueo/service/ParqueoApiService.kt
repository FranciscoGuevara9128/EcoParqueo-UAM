package com.uam.ecoparqueo.service

import com.uam.ecoparqueo.model.Parqueo
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ParqueoApiService {

    @GET("parqueo/all")
    suspend fun findAll(): Response<List<Parqueo>>

    @GET("parqueo/getId/{id}")
    suspend fun findById(@Path("id") id: Int): Response<Parqueo>
}
