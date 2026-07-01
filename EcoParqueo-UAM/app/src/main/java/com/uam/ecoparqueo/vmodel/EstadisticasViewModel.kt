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
    val registros: List<com.uam.ecoparqueo.model.entity.RegistroAccesoEntity> = emptyList(),
    val horasPico: List<Pair<String, Int>> = emptyList(),
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
        registroRepository.getVehiculosDentroFlow(),
        registroRepository.getAllRegistrosFlow()
    ) { parqueosList, registrosDentroList, registrosAllList ->
        val totalCapacidad = parqueosList.sumOf { it.capacidadTotal }
        val totalDisponibles = parqueosList.sumOf { it.disponibles }
        val totalOcupados = totalCapacidad - totalDisponibles
        val porcentajeOcupacion = if (totalCapacidad > 0) (totalOcupados.toFloat() / totalCapacidad * 100) else 0f

        // Calcular horas pico: agrupar por hora de ingreso (HH:00) y tomar los más frecuentes
        val horasMap = registrosAllList.groupingBy {
            val cal = java.util.Calendar.getInstance().apply { timeInMillis = it.fechaHoraIngreso }
            String.format(java.util.Locale.getDefault(), "%02d:00", cal.get(java.util.Calendar.HOUR_OF_DAY))
        }.eachCount()

        val horasPicoList = horasMap.entries
            .sortedByDescending { it.value }
            .map { it.key to it.value }
            .take(5)

        EstadisticasState(
            parqueos = parqueosList,
            registros = registrosAllList,
            horasPico = horasPicoList,
            vehiculosDentro = registrosDentroList.size,
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
