package com.uam.ecoparqueo.data.local

import com.uam.ecoparqueo.model.entity.ParqueoEntity

/**
 * Parqueos del campus UAM con coordenadas fijas.
 * Para actualizar coordenadas: clic derecho en Google Maps
 * sobre el punto deseado y copia el primer valor (latitud)
 * y el segundo (longitud).
 *
 * Ejemplo de coordenadas UAM Managua — ajusta según
 * la ubicación real de cada parqueo en el campus.
 *
 * Para obtener las coordenadas sigue los siguienres pasos
 * Ve a https://maps.google.com
 * Busca "Universidad Americana UAM Managua Nicaragua"
 * Una vez ubicado el campus, navega hasta el área exacta de cada parqueo
 * Haz clic derecho sobre el punto exacto del parqueo
 * En el menú que aparece, el primer elemento son las coordenadas, por ejemplo 12.1328, -86.2734. Haz clic en ese número y se copia automáticamente
 * El primer número es la latitud y el segundo la longitud
 */
object ParqueosLocales {

    val lista = listOf(
        // Lista de ejemplos de parqueos, falta poner las coordenadas reales
        ParqueoEntity(
            id             = 1,
            nombre         = "Parqueo Plazoleta",
            capacidadTotal = 30,
            disponibles    = 30,
            direccion      = "Frente a la plazoleta principal, UAM",
            latitud        = 12.13301,   // <-- reemplaza con tu clic derecho en Maps
            longitud       = -86.27289   // <-- reemplaza con tu clic derecho en Maps
        ),
        ParqueoEntity(
            id             = 2,
            nombre         = "Parqueo Recepción",
            capacidadTotal = 20,
            disponibles    = 20,
            direccion      = "Entrada principal, UAM",
            latitud        = 12.13265,   // <-- reemplaza con tu clic derecho en Maps
            longitud       = -86.27340   // <-- reemplaza con tu clic derecho en Maps
        )
        // Agrega más parqueos aquí siguiendo el mismo formato
    )
}