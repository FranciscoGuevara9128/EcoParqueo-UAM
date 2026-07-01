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
    val registros: List<com.uam.ecoparqueo.model.entity.RegistroAccesoEntity> = emptyList(),
    val horasPico: List<Pair<String, Int>> = emptyList(),
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
        registroRepository.getAllRegistrosFlow()
    ) { parqueosList, registrosDentroList, registrosAllList ->
        _historial
    ) { parqueosList, registrosList, historyList ->
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
            vehiculosDentro = registrosList.size,
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
                    // Ignorar o registrar log de error
                }
            }
        }
    }
}
