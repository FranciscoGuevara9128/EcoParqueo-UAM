package com.uam.ecoparqueo

import android.content.Context
import com.uam.ecoparqueo.data.local.EcoParqueoDatabase
import com.uam.ecoparqueo.data.local.SessionManager
import com.uam.ecoparqueo.repository.AuthRepository
import com.uam.ecoparqueo.repository.ParqueoRepository
import com.uam.ecoparqueo.repository.RegistroAccesoRepository
import com.uam.ecoparqueo.repository.VehiculoRepository

const val FALLBACK_USER_ID = "d35ac9db-2893-4605-8fd2-01afc4fd5dfb"

object Graph {
    lateinit var database: EcoParqueoDatabase
    lateinit var sessionManager: SessionManager

    val authRepository by lazy { AuthRepository() }
    val vehiculoRepository by lazy { VehiculoRepository() }
    val parqueoRepository by lazy { ParqueoRepository() }
    val registroAccesoRepository by lazy { RegistroAccesoRepository() }

    fun provide(context: Context) {
        database = EcoParqueoDatabase.getDatabase(context)
        sessionManager = SessionManager(context)
    }
}
