package com.uam.ecoparqueo.vmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uam.ecoparqueo.Graph
import com.uam.ecoparqueo.model.entity.ParqueoEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class EstadisticasState(
    val parqueos: List<ParqueoEntity> = emptyList(),
    val vehiculosDentro: Int = 0,
    val totalCapacidad: Int = 0,
    val totalDisponibles: Int = 0,
    val totalOcupados: Int = 0,
    val porcentajeOcupacion: Float = 0f
)

class EstadisticasViewModel : ViewModel() {
    private val repository = Graph.parqueoRepository
    private val registroRepository = Graph.registroAccesoRepository

    val state: StateFlow<EstadisticasState> = combine(
        repository.getAllParqueosFlow(),
        registroRepository.getVehiculosDentroFlow()
    ) { parqueosList, registrosList ->
        val totalCapacidad = parqueosList.sumOf { it.capacidadTotal }
        val totalDisponibles = parqueosList.sumOf { it.disponibles }
        val totalOcupados = totalCapacidad - totalDisponibles
        val porcentajeOcupacion = if (totalCapacidad > 0) (totalOcupados.toFloat() / totalCapacidad * 100) else 0f

        EstadisticasState(
            parqueos = parqueosList,
            vehiculosDentro = registrosList.size,
            totalCapacidad = totalCapacidad,
            totalDisponibles = totalDisponibles,
            totalOcupados = totalOcupados,
            porcentajeOcupacion = porcentajeOcupacion
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        EstadisticasState()
    )
}
