package com.uam.ecoparqueo.vmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uam.ecoparqueo.Graph
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class EstadisticasViewModel : ViewModel() {
    private val parqueoDao = Graph.database.parqueoDao()
    private val registroDao = Graph.database.registroAccesoDao()

    val parqueosStats = parqueoDao.getAllParqueosFlow().stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        emptyList()
    )

    val vehiculosDentro = registroDao.getVehiculosDentro().map { it.size }.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        0
    )
}
