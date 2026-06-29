package com.uam.ecoparqueo.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.uam.ecoparqueo.model.Usuario
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_session")

class SessionManager(private val context: Context) {

    companion object {
        private val KEY_USER_ID = stringPreferencesKey("user_id")
        private val KEY_USER_NOMBRE = stringPreferencesKey("user_nombre")
        private val KEY_USER_TIPO = stringPreferencesKey("user_tipo")
        private val KEY_TOKEN = stringPreferencesKey("token")
    }

    val userSession: Flow<Usuario?> = context.dataStore.data.map { preferences ->
        val id = preferences[KEY_USER_ID]
        val nombre = preferences[KEY_USER_NOMBRE]
        val tipoUsuario = preferences[KEY_USER_TIPO]
        val token = preferences[KEY_TOKEN]

        if (id != null && nombre != null && tipoUsuario != null) {
            Usuario(id = id, nombre = nombre, tipoUsuario = tipoUsuario, token = token)
        } else {
            null
        }
    }

    suspend fun saveSession(usuario: Usuario) {
        context.dataStore.edit { preferences ->
            preferences[KEY_USER_ID] = usuario.id ?: ""
            preferences[KEY_USER_NOMBRE] = usuario.nombre
            preferences[KEY_USER_TIPO] = usuario.tipoUsuario
            preferences[KEY_TOKEN] = usuario.token ?: ""
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
