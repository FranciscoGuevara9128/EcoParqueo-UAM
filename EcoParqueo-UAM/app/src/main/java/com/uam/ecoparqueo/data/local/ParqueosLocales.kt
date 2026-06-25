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
            id             = "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11",
            nombre         = "Parqueo Plazoleta",
            capacidadTotal = 200,
            disponibles    = 200,
            direccion      = "Frente a la plazoleta principal, UAM",
            12.109286199300668, -86.25694437766967
        ),
        ParqueoEntity(
            id             = "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12",
            nombre         = "Parqueo Recepción",
            capacidadTotal = 300,
            disponibles    = 300,
            direccion      = "Entrada principal, UAM",
            12.108470246581334, -86.25633540354046
        ),
        ParqueoEntity(
            id             = "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13",
            nombre         = "Parqueo Clinicas",
            capacidadTotal = 30,
            disponibles    = 30,
            direccion      = "Frente a las clínicas, UAM",
            12.107609891543099, -86.25707959414085
        )
        // Agrega más parqueos aquí siguiendo el mismo formato
    )
}