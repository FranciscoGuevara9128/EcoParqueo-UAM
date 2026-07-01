package com.uam.ecoparqueo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

/**
 * Identifica la pantalla activa dentro del flujo protegido por el menú
 * hamburguesa, tanto para el Estudiante como para el Guarda. Se usa para
 * resaltar el ítem actual y para saber a dónde puede navegar cada rol.
 */
enum class DrawerDestination {
    // Flujo Estudiante
    BUSCAR_PARQUEO,
    MIS_VEHICULOS,
    // Flujo Guarda
    CONTROL_ACCESO,
    ESTADISTICAS,
    REGISTRAR_PARQUEO
}

private data class DrawerItemData(
    val destino: DrawerDestination,
    val label: String,
    val icon: ImageVector
)

// El estudiante solo puede alternar entre estas dos pantallas
private val ITEMS_ESTUDIANTE = listOf(
    DrawerItemData(DrawerDestination.BUSCAR_PARQUEO, "Buscar Parqueo", Icons.Default.LocalParking),
    DrawerItemData(DrawerDestination.MIS_VEHICULOS, "Mis Vehículos", Icons.Default.DirectionsCar)
)

// El guarda puede alternar entre estas tres pantallas
private val ITEMS_GUARDA = listOf(
    DrawerItemData(DrawerDestination.CONTROL_ACCESO, "Control de Acceso", Icons.Default.Shield),
    DrawerItemData(DrawerDestination.ESTADISTICAS, "Estadísticas del Día", Icons.Default.QueryStats),
    DrawerItemData(DrawerDestination.REGISTRAR_PARQUEO, "Registrar Parqueo", Icons.Default.LocalParking)
)

/**
 * Contenido del menú hamburguesa:
 *  - Ícono representativo del rol (Estudiante | Guarda) arriba a la izquierda.
 *  - Saludo distinto según el rol.
 *  - Línea divisora.
 *  - Opciones de navegación permitidas para ese rol.
 */
@Composable
fun AppDrawerContent(
    nombreUsuario: String,
    tipoUsuario: String, // "Estudiante" | "Guarda"
    pantallaActual: DrawerDestination,
    onNavigate: (DrawerDestination) -> Unit,
    onIrAlPanel: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val esGuarda = tipoUsuario.equals("Guarda", ignoreCase = true)
    val items = if (esGuarda) ITEMS_GUARDA else ITEMS_ESTUDIANTE
    val saludo = if (esGuarda) "Hola, Guarda $nombreUsuario!" else "¡Hola, $nombreUsuario!"

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(vertical = 24.dp)
    ) {
        // ── Encabezado: icono del rol + saludo ──────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (esGuarda) Icons.Default.Shield else Icons.Default.Person,
                    contentDescription = if (esGuarda) "Guarda" else "Estudiante",
                    tint = colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Text(
                text = saludo,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                color = colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(color = colorScheme.outline.copy(alpha = 0.3f), thickness = 1.dp)
        Spacer(modifier = Modifier.height(8.dp))

        // ── Opciones de navegación permitidas para el rol ───────────
        items.forEach { item ->
            val seleccionado = item.destino == pantallaActual
            NavigationDrawerItem(
                label = { Text(item.label) },
                selected = seleccionado,
                icon = { Icon(imageVector = item.icon, contentDescription = null) },
                onClick = { if (!seleccionado) onNavigate(item.destino) },
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
            )
        }

        Spacer(modifier = Modifier.height(0.dp))
        Spacer(modifier = Modifier.weight(1f))

        HorizontalDivider(color = colorScheme.outline.copy(alpha = 0.3f), thickness = 1.dp)
        Spacer(modifier = Modifier.height(8.dp))

        NavigationDrawerItem(
            label = { Text("Panel Principal") },
            selected = false,
            icon = { Icon(imageVector = Icons.Default.Home, contentDescription = null) },
            onClick = onIrAlPanel,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
        )

        NavigationDrawerItem(
            label = { Text("Cerrar Sesión") },
            selected = false,
            icon = { Icon(imageVector = Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null) },
            onClick = onCerrarSesion,
            colors = NavigationDrawerItemDefaults.colors(
                unselectedTextColor = colorScheme.error,
                unselectedIconColor = colorScheme.error
            ),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
        )
    }
}

/**
 * Scaffold reutilizable que envuelve cualquier pantalla protegida con:
 *  - Un [ModalNavigationDrawer] cuyo panel ocupa SOLO la mitad del ancho
 *    de la pantalla (no tapa toda la ventana; el resto queda con scrim).
 *  - Una TopAppBar cuyo ícono de navegación es el menú hamburguesa,
 *    sustituyendo cualquier botón de "volver".
 *
 * Se usa en toda pantalla donde el menú hamburguesa deba estar disponible
 * (a partir de "Buscar Parqueo" / "Mis Vehículos" para el estudiante, y a
 * partir de "Control de Acceso" / "Estadísticas" / "Registrar Parqueo"
 * para el guarda).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EcoParqueoDrawerScaffold(
    nombreUsuario: String,
    tipoUsuario: String,
    pantallaActual: DrawerDestination,
    title: String,
    onNavigate: (DrawerDestination) -> Unit,
    onIrAlPanel: () -> Unit,
    onCerrarSesion: () -> Unit,
    topBarActions: @Composable RowScope.() -> Unit = {},
    snackbarHostState: SnackbarHostState? = null,
    content: @Composable (PaddingValues) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                // El menú solo llega hasta la mitad de la pantalla.
                modifier = Modifier.fillMaxWidth(0.5f)
            ) {
                AppDrawerContent(
                    nombreUsuario = nombreUsuario,
                    tipoUsuario = tipoUsuario,
                    pantallaActual = pantallaActual,
                    onNavigate = { destino ->
                        scope.launch { drawerState.close() }
                        onNavigate(destino)
                    },
                    onIrAlPanel = {
                        scope.launch { drawerState.close() }
                        onIrAlPanel()
                    },
                    onCerrarSesion = {
                        scope.launch { drawerState.close() }
                        onCerrarSesion()
                    }
                )
            }
        }
    ) {
        Scaffold(
            snackbarHost = { snackbarHostState?.let { SnackbarHost(it) } },
            topBar = {
                TopAppBar(
                    title = { Text(title, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Abrir menú"
                            )
                        }
                    },
                    actions = topBarActions,
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = colorScheme.primary,
                        titleContentColor = colorScheme.onPrimary,
                        navigationIconContentColor = colorScheme.onPrimary,
                        actionIconContentColor = colorScheme.onPrimary
                    )
                )
            }
        ) { padding ->
            content(padding)
        }
    }
}

