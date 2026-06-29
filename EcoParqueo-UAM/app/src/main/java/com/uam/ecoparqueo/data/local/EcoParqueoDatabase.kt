package com.uam.ecoparqueo.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.uam.ecoparqueo.FALLBACK_USER_ID
import com.uam.ecoparqueo.data.local.dao.ParqueoDao
import com.uam.ecoparqueo.data.local.dao.RegistroAccesoDao
import com.uam.ecoparqueo.data.local.dao.UsuarioDao
import com.uam.ecoparqueo.data.local.dao.VehiculoDao
import com.uam.ecoparqueo.model.entity.ParqueoEntity
import com.uam.ecoparqueo.model.entity.RegistroAccesoEntity
import com.uam.ecoparqueo.model.entity.UsuarioEntity
import com.uam.ecoparqueo.model.entity.VehiculoEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UsuarioEntity::class,
        VehiculoEntity::class,
        ParqueoEntity::class,
        RegistroAccesoEntity::class
    ],
    version = 6,
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
                    .fallbackToDestructiveMigration(true)
                    .addCallback(object : RoomDatabase.Callback() {

                        override fun onCreate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // El usuario de prueba también se inserta en onOpen para garantizar
                            // su existencia sin importar el ciclo de vida de la BD.
                        }

                        // onOpen se ejecuta cada vez que abre la BD,
                        // usamos esto para insertar los parqueos si no existen
                        // and para garantizar que el usuario de sesión de prueba siempre exista.
                        override fun onOpen(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                            super.onOpen(db)
                            // Usuario de prueba: INSERT OR IGNORE garantiza idempotencia.
                            // TODO: eliminar cuando se implemente autenticación con DataStore.
                            db.execSQL(
                                "INSERT OR IGNORE INTO usuarios (id, nombre, tipoUsuario, fechaRegistro) " +
                                        "VALUES ('$FALLBACK_USER_ID', 'Estudiante de Prueba', 'Estudiante', ${System.currentTimeMillis()})"
                            )
                            INSTANCE?.let { database ->
                                CoroutineScope(Dispatchers.IO).launch {
                                    insertarParqueosLocales(database.parqueoDao())
                                }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        /**
         * Inserta los parqueos definidos en ParqueosLocales solo si
         * no existen ya en la base de datos, usando REPLACE para
         * actualizar coordenadas si cambian en el código.
         */
        private suspend fun insertarParqueosLocales(parqueoDao: ParqueoDao) {
            parqueoDao.insertAll(ParqueosLocales.lista)
        }
    }
}