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
                // Solo sincroniza desde el servidor si este devuelve parqueos,
                // si no, se mantienen los parqueos locales precargados
                if (remoto.isNotEmpty()) {
                    parqueoDao.deleteAll()
                    val entidades = remoto.map { parqueo ->
                        ParqueoEntity(
                            id             = parqueo.id ?: java.util.UUID.randomUUID().toString(),
                            nombre         = parqueo.name,
                            capacidadTotal = parqueo.capacidadTotal,
                            disponibles    = parqueo.disponibles,
                            direccion      = parqueo.direccion,
                            latitud        = parqueo.latitud,
                            longitud       = parqueo.longitud
                        )
                    }
                    parqueoDao.insertAll(entidades)
                }
                ApiResult.Success(Unit)
            } else {
                // Error del servidor: no borramos los datos locales
                ApiResult.Success(Unit)
            }
        } catch (e: Exception) {
            // Sin conexión: los parqueos locales siguen disponibles
            ApiResult.Success(Unit)
        }
    }

    suspend fun disminuirDisponibilidad(id: String) {
        parqueoDao.disminuirDisponibilidad(id)
    }

    suspend fun aumentarDisponibilidad(id: String) {
        parqueoDao.aumentarDisponibilidad(id)
    }
}
