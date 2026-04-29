package com.uam.ecoparqueo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.uam.ecoparqueo.ui.screens.estudiante.RegistroVehicular
import com.uam.ecoparqueo.ui.screens.estudiante.SeleccionParqueo
import com.uam.ecoparqueo.ui.theme.EcoParqueoUAMTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EcoParqueoUAMTheme {
                RegistroVehicular()
            }
        }
    }
}
