package com.uam.ecoparqueo.repository

import com.uam.ecoparqueo.Graph
import com.uam.ecoparqueo.model.LoginRequest
import com.uam.ecoparqueo.model.Usuario
import com.uam.ecoparqueo.model.entity.UsuarioEntity
import com.uam.ecoparqueo.service.ApiResult
import com.uam.ecoparqueo.service.RetrofitClient

class AuthRepository {
    private val apiService = RetrofitClient.usuarioApiService
    private val sessionManager = Graph.sessionManager
    private val usuarioDao = Graph.database.usuarioDao()

    suspend fun login(nombre: String, contrasena: String): ApiResult<Usuario> {
        return try {
            val response = apiService.login(
                LoginRequest(
                    nombre = nombre.trim(),
                    contrasena = contrasena
                )
            )
            if (response.isSuccessful) {
                val authResponse = response.body()
                if (authResponse != null) {
                    val usuarioDto = authResponse.usuario.copy(token = authResponse.token)
                    
                    // Asegurar la existencia en SQLite local para FKs
                    usuarioDto.id?.let { userId ->
                        val localUser = usuarioDao.getUsuarioById(userId)
                        if (localUser == null) {
                            usuarioDao.insert(
                                UsuarioEntity(
                                    id = userId,
                                    nombre = usuarioDto.nombre,
                                    tipoUsuario = usuarioDto.tipoUsuario
                                )
                            )
                        }
                    }
                    
                    sessionManager.saveSession(usuarioDto)
                    ApiResult.Success(usuarioDto)
                } else {
                    ApiResult.Error("Error: Respuesta vacía del servidor")
                }
            } else {
                ApiResult.Error("Nombre o contraseña incorrectos")
            }
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Sin conexión con el servidor")
        }
    }

    suspend fun register(nombre: String, contrasena: String): ApiResult<Usuario> {
        return try {
            val response = apiService.register(
                Usuario(
                    nombre = nombre.trim(),
                    tipoUsuario = "Estudiante",
                    contrasena = contrasena
                )
            )
            if (response.isSuccessful) {
                val usuarioDto = response.body()
                if (usuarioDto != null) {
                    // Asegurar la existencia en SQLite local para FKs
                    usuarioDto.id?.let { userId ->
                        val localUser = usuarioDao.getUsuarioById(userId)
                        if (localUser == null) {
                            usuarioDao.insert(
                                UsuarioEntity(
                                    id = userId,
                                    nombre = usuarioDto.nombre,
                                    tipoUsuario = usuarioDto.tipoUsuario
                                )
                            )
                        }
                    }
                    ApiResult.Success(usuarioDto)
                } else {
                    ApiResult.Error("Error: Respuesta vacía del servidor")
                }
            } else {
                val rawError = response.errorBody()?.string() ?: "Error de servidor al registrar"
                ApiResult.Error(rawError)
            }
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Error de conexión")
        }
    }

    suspend fun logout() {
        sessionManager.clearSession()
    }

    suspend fun getUsuarioById(id: String): UsuarioEntity? {
        return usuarioDao.getUsuarioById(id)
    }

    suspend fun insertUsuario(usuario: UsuarioEntity) {
        usuarioDao.insert(usuario)
    }
}
