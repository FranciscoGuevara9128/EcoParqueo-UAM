package com.uam.ecoparqueo

import android.content.Context
import com.uam.ecoparqueo.data.local.EcoParqueoDatabase
import com.uam.ecoparqueo.data.local.SessionManager

object Graph {
    lateinit var database: EcoParqueoDatabase
    lateinit var sessionManager: SessionManager

    fun provide(context: Context) {
        database = EcoParqueoDatabase.getDatabase(context)
        sessionManager = SessionManager(context)
    }
}

