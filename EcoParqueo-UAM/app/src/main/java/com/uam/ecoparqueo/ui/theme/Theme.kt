package com.uam.ecoparqueo.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = VerdePrimaryDark,
    onPrimary = VerdeOscuro,
    primaryContainer = VerdeOscuro,
    onPrimaryContainer = VerdeSuave,
    secondary = VerdeSecondaryDark,
    onSecondary = VerdeOscuro,
    secondaryContainer = VerdePrincipal,
    onSecondaryContainer = VerdeSuave,
    tertiary = VerdeTertiaryDark,
    onTertiary = VerdeOscuro,
    tertiaryContainer = VerdePrincipal,
    onTertiaryContainer = VerdeSuave,
    background = BackgroundDark,
    onBackground = OnSurfaceDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceDark,
    onSurfaceVariant = OnSurfaceDark,
    outline = GrisInactivo,
    error = RojoErrorClaro,
    onError = Negro
)

private val LightColorScheme = lightColorScheme(
    primary = VerdeOscuro,
    onPrimary = Blanco,
    primaryContainer = VerdeSuave,
    onPrimaryContainer = VerdeOscuro,
    secondary = VerdePrincipal,
    onSecondary = Blanco,
    secondaryContainer = VerdeMuyClaro,
    onSecondaryContainer = VerdeOscuro,
    tertiary = VerdeClaro,
    onTertiary = Blanco,
    tertiaryContainer = VerdeSuave,
    onTertiaryContainer = VerdeOscuro,
    background = Blanco,
    onBackground = Negro,
    surface = Blanco,
    onSurface = Negro,
    surfaceVariant = GrisSuave,
    onSurfaceVariant = GrisTexto,
    outline = GrisInactivo,
    error = RojoError,
    onError = Blanco
)

@Composable
fun EcoParqueoUAMTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}