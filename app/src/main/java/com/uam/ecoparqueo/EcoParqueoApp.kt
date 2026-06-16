package com.uam.ecoparqueo

import android.app.Application

class EcoParqueoApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Graph.provide(this)
    }
}
