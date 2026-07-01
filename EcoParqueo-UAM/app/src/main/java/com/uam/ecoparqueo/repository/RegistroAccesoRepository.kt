package com.uam.ecoparqueo.repository

import com.uam.ecoparqueo.Graph
import com.uam.ecoparqueo.model.RegistroAcceso
import com.uam.ecoparqueo.model.entity.RegistroAccesoEntity
import com.uam.ecoparqueo.service.ApiResult
import com.uam.ecoparqueo.service.RetrofitClient
import kotlinx.coroutines.flow.Flow

class RegistroAccesoRepository {
    private val registroDao = Graph.database.registroAccesoDao()
    private val apiService = RetrofitClient.registroAccesoApiService

    fun getVehiculosDentroFlow(): Flow<List<RegistroAccesoEntity>> =
        registroDao.getVehiculosDentro()

    suspend fun getRegistroActivoDeVehiculo(vehiculoId: String): RegistroAccesoEntity? =
        registroDao.getRegistroActivoDeVehiculo(vehiculoId)

    suspend fun registrarEntrada(
        vehiculoId: String,
        parqueoId: String,
        placa: String,
        parqueoNombre: String
    ): Long {
        // Guardar localmente
        val localId = registroDao.insert(
            RegistroAccesoEntity(
                vehiculoId = vehiculoId,
                parqueoId = parqueoId,
                estado = "DENTRO"
            )
        )
        
        // Sincronizar con el servidor de forma asíncrona como un evento de acceso
        try {
            val dto = RegistroAcceso(
                placa = placa,
                parqueoNombre = parqueoNombre,
                fechaHora = System.currentTimeMillis()
            )
            apiService.registrarAcceso(dto)
        } catch (e: Exception) {
            // Ignorar fallas de red para robustez offline
        }

        return localId
    }

    suspend fun registrarSalida(
        registroId: Int,
        placa: String,
        parqueoNombre: String,
        horaSalida: Long = System.currentTimeMillis()
    ) {
        // Guardar localmente
        registroDao.marcarSalida(registroId, horaSalida)

        // Sincronizar con el servidor
        try {
            val dto = RegistroAcceso(
                placa = placa,
                parqueoNombre = parqueoNombre,
                fechaHora = horaSalida
            )
            apiService.registrarAcceso(dto)
        } catch (e: Exception) {
            // Ignorar fallas de red
        }
    }

    // Obtener estadísticas reales de accesos del servidor
    suspend fun getTodosLosRegistros(): ApiResult<List<RegistroAcceso>> {
        return try {
            val response = apiService.findAll()
            if (response.isSuccessful) {
                ApiResult.Success(response.body() ?: emptyList())
            } else {
                ApiResult.Error("Error ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Sin conexión con el servidor")
        }
    }
}
