package com.uam.ecoparqueo.repository

import com.uam.ecoparqueo.model.Vehiculo
import com.uam.ecoparqueo.service.ApiResult
import com.uam.ecoparqueo.service.RetrofitClient

class VehiculoRepository {

    private val apiService = RetrofitClient.vehiculoApiService

    suspend fun findAll(): ApiResult<List<Vehiculo>> {
        return try {
            val response = apiService.findAll()
            if (response.isSuccessful) {
                ApiResult.Success(response.body() ?: emptyList())
            } else {
                ApiResult.Error("Error ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Error desconocido de conexión")
        }
    }

    suspend fun findById(id: Int): ApiResult<Vehiculo> {
        return try {
            val response = apiService.findById(id)
            if (response.isSuccessful) {
                response.body()?.let { ApiResult.Success(it) }
                    ?: ApiResult.Error("Vehículo no encontrado")
            } else {
                ApiResult.Error("Error ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Error desconocido de conexión")
        }
    }

    suspend fun save(vehiculo: Vehiculo): ApiResult<Vehiculo> {
        return try {
            val response = apiService.save(vehiculo)
            if (response.isSuccessful) {
                response.body()?.let { ApiResult.Success(it) }
                    ?: ApiResult.Error("Error al guardar el vehículo")
            } else {
                ApiResult.Error("Error ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Error desconocido de conexión")
        }
    }
}