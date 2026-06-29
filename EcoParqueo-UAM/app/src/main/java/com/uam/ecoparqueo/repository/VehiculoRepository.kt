package com.uam.ecoparqueo.repository

import com.uam.ecoparqueo.Graph
import com.uam.ecoparqueo.model.Vehiculo
import com.uam.ecoparqueo.model.entity.VehiculoEntity
import com.uam.ecoparqueo.service.ApiResult
import com.uam.ecoparqueo.service.RetrofitClient
import kotlinx.coroutines.flow.Flow

class VehiculoRepository {

    private val apiService = RetrofitClient.vehiculoApiService
    private val vehiculoDao = Graph.database.vehiculoDao()

    // --- Local DB (Room) Operations ---

    fun getVehiculosDeUsuarioFlow(usuarioId: String): Flow<List<VehiculoEntity>> =
        vehiculoDao.getVehiculosDeUsuario(usuarioId)

    suspend fun getLocalVehiculoByPlaca(placa: String): VehiculoEntity? =
        vehiculoDao.getVehiculoByPlaca(placa)

    suspend fun insertLocalVehiculo(entidad: VehiculoEntity) {
        vehiculoDao.insert(entidad)
    }

    // --- Remote API (Retrofit) Operations ---

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

    suspend fun findById(id: String): ApiResult<Vehiculo> {
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

    suspend fun findByPlaca(placa: String): ApiResult<Vehiculo> {
        return try {
            val response = apiService.findByPlaca(placa)
            if (response.isSuccessful) {
                response.body()?.let { ApiResult.Success(it) }
                    ?: ApiResult.Error("Vehículo no encontrado")
            } else {
                ApiResult.Error("Vehículo no registrado en el sistema")
            }
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Error de conexión con el servidor")
        }
    }

    suspend fun update(vehiculo: Vehiculo): ApiResult<Vehiculo> {
        return try {
            val response = apiService.update(vehiculo)
            if (response.isSuccessful) {
                response.body()?.let { ApiResult.Success(it) }
                    ?: ApiResult.Error("Error al actualizar el vehículo")
            } else {
                ApiResult.Error("Error ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Error de conexión")
        }
    }
}