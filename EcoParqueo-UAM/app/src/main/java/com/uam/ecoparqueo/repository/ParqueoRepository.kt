package com.uam.ecoparqueo.repository

import com.uam.ecoparqueo.Graph
import com.uam.ecoparqueo.model.entity.ParqueoEntity
import com.uam.ecoparqueo.service.ApiResult
import com.uam.ecoparqueo.service.RetrofitClient
import kotlinx.coroutines.flow.Flow

class ParqueoRepository {

    private val parqueoDao = Graph.database.parqueoDao()
    private val apiService = RetrofitClient.parqueoApiService

    // La UI siempre observa Room (fuente de verdad local)
    fun getAllParqueosFlow(): Flow<List<ParqueoEntity>> =
        parqueoDao.getAllParqueosFlow()

    // Sincroniza los parqueos desde la API hacia Room
    suspend fun refresh(): ApiResult<Unit> {
        return try {
            val response = apiService.findAll()
            if (response.isSuccessful) {
                val remoto = response.body() ?: emptyList()
                // Borramos y reinsertamos para reflejar cambios del servidor
                parqueoDao.deleteAll()
                val entidades = remoto.map { parqueo ->
                    ParqueoEntity(
                        nombre       = parqueo.name,
                        capacidadTotal = parqueo.capacidadTotal,
                        disponibles  = parqueo.disponibles,
                        direccion    = parqueo.direccion,
                        latitud = parqueo.latitud,
                        longitud = parqueo.longitud
                    )
                }
                parqueoDao.insertAll(entidades)
                ApiResult.Success(Unit)
            } else {
                ApiResult.Error("Error ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Error de conexión al cargar parqueos")
        }
    }

    suspend fun disminuirDisponibilidad(id: Int) {
        parqueoDao.disminuirDisponibilidad(id)
    }

    suspend fun aumentarDisponibilidad(id: Int) {
        parqueoDao.aumentarDisponibilidad(id)
    }
}
