package com.uam.ecoparqueo.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.uam.ecoparqueo.data.local.dao.ParqueoDao
import com.uam.ecoparqueo.data.local.dao.RegistroAccesoDao
import com.uam.ecoparqueo.data.local.dao.UsuarioDao
import com.uam.ecoparqueo.data.local.dao.VehiculoDao
import com.uam.ecoparqueo.model.entity.ParqueoEntity
import com.uam.ecoparqueo.model.entity.RegistroAccesoEntity
import com.uam.ecoparqueo.model.entity.UsuarioEntity
import com.uam.ecoparqueo.model.entity.VehiculoEntity

@Database(
    entities = [
        UsuarioEntity::class,
        VehiculoEntity::class,
        ParqueoEntity::class,
        RegistroAccesoEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class EcoParqueoDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun vehiculoDao(): VehiculoDao
    abstract fun parqueoDao(): ParqueoDao
    abstract fun registroAccesoDao(): RegistroAccesoDao

    companion object {
        @Volatile
        private var INSTANCE: EcoParqueoDatabase? = null

        fun getDatabase(context: Context): EcoParqueoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EcoParqueoDatabase::class.java,
                    "ecoparqueo_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
