package com.uam.ecoparqueo.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.uam.ecoparqueo.model.entity.ParqueoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ParqueoDao {
    @Insert
    suspend fun insert(parqueo: ParqueoEntity): Long

    @Query("SELECT * FROM parqueos")
    fun getAllParqueosFlow(): Flow<List<ParqueoEntity>>

    @Query("SELECT * FROM parqueos WHERE nombre = :nombre LIMIT 1")
    suspend fun getParqueoByNombre(nombre: String): ParqueoEntity?

    @Query("UPDATE parqueos SET disponibles = disponibles - 1 WHERE id = :id AND disponibles > 0")
    suspend fun disminuirDisponibilidad(id: Int)

    @Query("UPDATE parqueos SET disponibles = disponibles + 1 WHERE id = :id AND disponibles < capacidadTotal")
    suspend fun aumentarDisponibilidad(id: Int)
}
