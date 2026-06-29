package com.uam.ecoparqueo.service

import com.uam.ecoparqueo.model.LoginRequest
import com.uam.ecoparqueo.model.Usuario
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface UsuarioApiService {

    @POST("usuario/login")
    suspend fun login(@Body request: LoginRequest): Response<Usuario>

    @POST("usuario/save")
    suspend fun register(@Body usuario: Usuario): Response<Usuario>
}
