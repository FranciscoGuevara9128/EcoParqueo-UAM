package com.uam.ecoparqueo.vmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uam.ecoparqueo.Graph
import com.uam.ecoparqueo.model.RegistroAcceso
import com.uam.ecoparqueo.model.entity.ParqueoEntity
import com.uam.ecoparqueo.service.ApiResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class EstadisticasState(
    val parqueos: List<ParqueoEntity> = emptyList(),
    val registros: List<RegistroAcceso> = emptyList(),
    val vehiculosDentro: Int = 0,
    val totalCapacidad: Int = 0,
    val totalDisponibles: Int = 0,
    val totalOcupados: Int = 0,
    val porcentajeOcupacion: Float = 0f,
    val horasPico: List<Pair<String, Int>> = emptyList()
)

class EstadisticasViewModel : ViewModel() {
    private val repository = Graph.parqueoRepository
    private val registroRepository = Graph.registroAccesoRepository

    private val _historial = MutableStateFlow<List<RegistroAcceso>>(emptyList())

    val state: StateFlow<EstadisticasState> = combine(
        repository.getAllParqueosFlow(),
        registroRepository.getVehiculosDentroFlow(),
        _historial
    ) { parqueosList, registrosDentroList, historyList ->
        val totalCapacidad = parqueosList.sumOf { it.capacidadTotal }
        val totalDisponibles = parqueosList.sumOf { it.disponibles }
        val totalOcupados = totalCapacidad - totalDisponibles
        val porcentajeOcupacion = if (totalCapacidad > 0) (totalOcupados.toFloat() / totalCapacidad * 100) else 0f

        val formatoHoraSimple = SimpleDateFormat("hh a", Locale.getDefault())

        val horasPico = historyList
            .map { formatoHoraSimple.format(Date(it.fechaHora)) }
            .groupBy { it }
            .map { (hora, list) -> Pair(hora, list.size) }
            .sortedByDescending { it.second }
            .take(3)

        EstadisticasState(
            parqueos = parqueosList,
            registros = historyList,
            vehiculosDentro = registrosDentroList.size,
            totalCapacidad = totalCapacidad,
            totalDisponibles = totalDisponibles,
            totalOcupados = totalOcupados,
            porcentajeOcupacion = porcentajeOcupacion,
            horasPico = horasPico
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        EstadisticasState()
    )

    init {
        cargarHistorial()
    }

    fun cargarHistorial() {
        viewModelScope.launch {
            when (val result = registroRepository.getTodosLosRegistros()) {
                is ApiResult.Success -> {
                    _historial.value = result.data
                }
                is ApiResult.Error -> {
                    // Ignorar
                }
            }
        }
    }
}
