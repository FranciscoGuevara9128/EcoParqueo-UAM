package com.uam.ecoparqueo.service

import kotlinx.coroutines.flow.firstOrNull
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val BASE_URL = "http://192.168.1.25:8181/api/"

    private val authInterceptor = okhttp3.Interceptor { chain ->
        val originalRequest = chain.request()

        // Obtener el token de forma bloqueante síncrona breve desde el DataStore de la sesión
        val token = kotlinx.coroutines.runBlocking {
            com.uam.ecoparqueo.Graph.sessionManager.userSession.firstOrNull()?.token
        }

        val newRequest = if (!token.isNullOrBlank()) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            originalRequest
        }

        chain.proceed(newRequest)
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val vehiculoApiService: VehiculoApiService =
        retrofit.create(VehiculoApiService::class.java)

    val parqueoApiService: ParqueoApiService =
        retrofit.create(ParqueoApiService::class.java)

    val usuarioApiService: UsuarioApiService =
        retrofit.create(UsuarioApiService::class.java)

    val registroAccesoApiService: RegistroAccesoApiService =
        retrofit.create(RegistroAccesoApiService::class.java)
}
