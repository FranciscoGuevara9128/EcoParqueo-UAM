package com.uam.ecoparqueo

import android.content.Context
import com.uam.ecoparqueo.data.local.EcoParqueoDatabase

object Graph {
    lateinit var database: EcoParqueoDatabase

    fun provide(context: Context) {
        database = EcoParqueoDatabase.getDatabase(context)
    }
}
