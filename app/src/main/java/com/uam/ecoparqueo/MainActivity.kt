package com.uam.ecoparqueo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.Modifier
import com.uam.ecoparqueo.navigation.AppNavigation
import com.uam.ecoparqueo.ui.theme.EcoParqueoUAMTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EcoParqueoUAMTheme {
                AppNavigation(modifier = Modifier)
            }
        }
    }
}
