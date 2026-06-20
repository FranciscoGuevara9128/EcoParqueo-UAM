package com.uam.ecoparqueo.repository

import com.uam.ecoparqueo.Graph
import com.uam.ecoparqueo.model.entity.ParqueoEntity
import kotlinx.coroutines.flow.Flow

class ParqueoRepository {

    private val parqueoDao = Graph.database.parqueoDao()

    fun getAllParqueosFlow(): Flow<List<ParqueoEntity>> {
        return parqueoDao.getAllParqueosFlow()
    }

    suspend fun disminuirDisponibilidad(id: Int) {
        parqueoDao.disminuirDisponibilidad(id)
    }

    suspend fun aumentarDisponibilidad(id: Int) {
        parqueoDao.aumentarDisponibilidad(id)
    }
}
