package com.uam.ecoparqueo.repository

import com.uam.ecoparqueo.Graph
import com.uam.ecoparqueo.model.Parqueo
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

    suspend fun getLocalParqueoByNombre(nombre: String): ParqueoEntity? =
        parqueoDao.getParqueoByNombre(nombre)

    // Sincroniza los parqueos desde la API hacia Room
    suspend fun refresh(): ApiResult<Unit> {
        return try {
            val response = apiService.findAll()
            if (response.isSuccessful) {
                val remoto = response.body() ?: emptyList()
                if (remoto.isNotEmpty()) {
                    // Merge: insertar/actualizar sin borrar los locales
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
                    // insertAll usa REPLACE, actualiza existentes y agrega nuevos
                    // SIN borrar los que solo existen localmente
                    parqueoDao.insertAll(entidades)
                }
                ApiResult.Success(Unit)
            } else {
                ApiResult.Error("Error al refrescar parqueos: ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Sin conexión con el servidor")
        }
    }

    suspend fun save(nuevoParqueo: Parqueo): ApiResult<Parqueo> {
        return try {
            val response = apiService.save(nuevoParqueo)
            if (response.isSuccessful) {
                val guardado = response.body()
                if (guardado != null) {
                    // Guardar también en Room
                    parqueoDao.insert(
                        ParqueoEntity(
                            id             = guardado.id ?: java.util.UUID.randomUUID().toString(),
                            nombre         = guardado.name,
                            capacidadTotal = guardado.capacidadTotal,
                            disponibles    = guardado.disponibles,
                            direccion      = guardado.direccion,
                            latitud        = guardado.latitud,
                            longitud       = guardado.longitud
                        )
                    )
                    ApiResult.Success(guardado)
                } else {
                    ApiResult.Error("El servidor retornó una respuesta vacía")
                }
            } else {
                ApiResult.Error("Error ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Error de conexión")
        }
    }

    suspend fun disminuirDisponibilidad(id: String) {
        parqueoDao.disminuirDisponibilidad(id)
        syncParqueoToServer(id)
    }

    suspend fun aumentarDisponibilidad(id: String) {
        parqueoDao.aumentarDisponibilidad(id)
        syncParqueoToServer(id)
    }

    private suspend fun syncParqueoToServer(id: String) {
        try {
            val local = parqueoDao.getParqueoById(id)
            if (local != null) {
                val dto = com.uam.ecoparqueo.model.Parqueo(
                    id             = local.id,
                    name           = local.nombre,
                    capacidadTotal = local.capacidadTotal,
                    disponibles    = local.disponibles,
                    direccion      = local.direccion,
                    latitud        = local.latitud,
                    longitud       = local.longitud
                )
                apiService.update(dto)
            }
        } catch (e: Exception) {
            // Ignoramos errores de red: Room sigue siendo fuente de verdad temporal
        }
    }
}
