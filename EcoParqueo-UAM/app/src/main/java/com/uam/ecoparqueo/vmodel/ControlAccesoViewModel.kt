package com.uam.ecoparqueo.vmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ControlAccesoState(
    val placaText: String = "",
    val placaList: List<String> = emptyList()
) {
    val placaNormalized: String
        get() = placaText.trim().uppercase()

    val placaError: Boolean
        get() = placaNormalized.isBlank() || !placaNormalized.matches(Regex("^[A-Z0-9-]{6,10}$"))
}

class ControlAccesoViewModel : ViewModel() {

    private val _state = MutableStateFlow(ControlAccesoState())
    val state: StateFlow<ControlAccesoState> = _state.asStateFlow()

    fun onPlacaTextChange(text: String) {
        _state.update { it.copy(placaText = text) }
    }

    fun registrarPlaca() {
        val current = _state.value
        if (current.placaText.isNotBlank() && !current.placaError) {
            _state.update {
                it.copy(
                    placaList = it.placaList + it.placaNormalized,
                    placaText = ""
                )
            }
        }
    }
}