package com.uam.ecoparqueo.repository

import com.uam.ecoparqueo.Graph
import com.uam.ecoparqueo.model.entity.RegistroAccesoEntity
import kotlinx.coroutines.flow.Flow

class RegistroAccesoRepository {
    private val registroDao = Graph.database.registroAccesoDao()

    fun getVehiculosDentroFlow(): Flow<List<RegistroAccesoEntity>> =
        registroDao.getVehiculosDentro()

    fun getAllRegistrosFlow(): Flow<List<RegistroAccesoEntity>> =
        registroDao.getAllRegistros()

    suspend fun getRegistroActivoDeVehiculo(vehiculoId: String): RegistroAccesoEntity? =
        registroDao.getRegistroActivoDeVehiculo(vehiculoId)

    suspend fun registrarEntrada(vehiculoId: String, parqueoId: String): Long {
        return registroDao.insert(
            RegistroAccesoEntity(
                vehiculoId = vehiculoId,
                parqueoId = parqueoId,
                estado = "DENTRO"
            )
        )
    }

    suspend fun registrarSalida(registroId: Int, horaSalida: Long = System.currentTimeMillis()) {
        registroDao.marcarSalida(registroId, horaSalida)
    }
}
