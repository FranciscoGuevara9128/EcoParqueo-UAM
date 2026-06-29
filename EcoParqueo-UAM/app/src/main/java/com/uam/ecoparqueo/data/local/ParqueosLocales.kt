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

        ParqueoEntity(
            id             = "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11",
            nombre         = "Parqueo Plazoleta",
            capacidadTotal = 200,
            disponibles    = 200,
            direccion      = "De la entrada principal de la UAM a mano derecha se encuentra el parqueo de la plazoleta, UAM",
            12.109286199300668, -86.25694437766967
        ),
        ParqueoEntity(
            id             = "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12",
            nombre         = "Parqueo Recepción",
            capacidadTotal = 300,
            disponibles    = 300,
            direccion      = "A la izquierda de la entrada principal de la UAM se encuentra el parqueo de recepción, UAM",
            12.108470246581334, -86.25633540354046
        ),
        ParqueoEntity(
            id             = "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13",
            nombre         = "Parqueo en frente del edificio C",
            capacidadTotal = 30,
            disponibles    = 30,
            direccion      = "A mano derecha de recepcion, frente al edificio C, UAM",
            12.108216265921767, -86.25684895417058
        ),
        ParqueoEntity(
            id             = "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14",
            nombre         = "Parqueo Clinicas",
            capacidadTotal = 30,
            disponibles    = 30,
            direccion      = "Frente a las clínicas, UAM",
            12.107615434907427, -86.25699119977222
        ),
        ParqueoEntity(
            id             = "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15",
            nombre         = "Parqueo Observatorio Astronómico",
            capacidadTotal = 150,
            disponibles    = 150,
            direccion      = "Detras del edificio astronomico, UAM",
            12.107302037937508, -86.25703861497777
        ),
        ParqueoEntity(
            id             = "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a16",
            nombre         = "Parqueo Biblioteca",
            capacidadTotal = 20,
            disponibles    = 20,
            direccion      = "Frente a Biblioteca, UAM",
            12.109473971728345, -86.25802205035862
        ),
        ParqueoEntity(
            id             = "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a17",
            nombre         = "Parqueo Edificio M",
            capacidadTotal = 30,
            disponibles    = 30,
            direccion      = "Frente a Edificio M, UAM",
            12.108395536268286, -86.25768054080436
        ),
        ParqueoEntity(
            id             = "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a18",
            nombre         = "Parqueo FIA",
            capacidadTotal = 30,
            disponibles    = 30,
            direccion      = "Frente a la facultad de ingeniería y arquitectura, UAM",
            12.107640020633736, -86.25774433483002
        )
        // Agrega más parqueos aquí siguiendo el mismo formato
    )
}