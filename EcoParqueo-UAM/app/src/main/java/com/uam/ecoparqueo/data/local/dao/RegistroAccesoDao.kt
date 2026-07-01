package com.uam.ecoparqueo.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.uam.ecoparqueo.model.entity.RegistroAccesoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RegistroAccesoDao {
    @Insert
    suspend fun insert(registro: RegistroAccesoEntity): Long

    @Query("UPDATE registros_acceso SET fechaHoraSalida = :horaSalida, estado = 'FUERA' WHERE id = :registroId")
    suspend fun marcarSalida(registroId: Int, horaSalida: Long)

    @Query("SELECT * FROM registros_acceso WHERE estado = 'DENTRO'")
    fun getVehiculosDentro(): Flow<List<RegistroAccesoEntity>>

    @Query("SELECT * FROM registros_acceso ORDER BY fechaHoraIngreso DESC")
    fun getAllRegistros(): Flow<List<RegistroAccesoEntity>>

    @Query("SELECT * FROM registros_acceso WHERE vehiculoId = :vehiculoId AND estado = 'DENTRO' LIMIT 1")
    suspend fun getRegistroActivoDeVehiculo(vehiculoId: String): RegistroAccesoEntity?
}
