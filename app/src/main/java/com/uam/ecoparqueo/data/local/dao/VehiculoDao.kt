package com.uam.ecoparqueo.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.uam.ecoparqueo.model.entity.VehiculoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VehiculoDao {
    @Insert
    suspend fun insert(vehiculo: VehiculoEntity): Long

    @Query("SELECT * FROM vehiculos WHERE usuarioId = :usuarioId")
    fun getVehiculosDeUsuario(usuarioId: Int): Flow<List<VehiculoEntity>>

    @Query("SELECT * FROM vehiculos WHERE placa = :placa")
    suspend fun getVehiculoByPlaca(placa: String): VehiculoEntity?
}
