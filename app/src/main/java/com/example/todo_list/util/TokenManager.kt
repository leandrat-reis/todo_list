package com.example.todoapp.util

import android.content.Context
import androidx.datastore.core.DataStore // <--- Importação NECESSÁRIA
import androidx.datastore.preferences.core.Preferences // <--- Importação NECESSÁRIA
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore // <--- Importação NECESSÁRIA
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

// Extensão para DataStore para acesso mais fácil
// Deve estar no nível superior do arquivo (fora de qualquer classe)
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class TokenManager(private val context: Context) {

    companion object {
        private val AUTH_TOKEN_KEY = stringPreferencesKey("auth_token")
    }

    suspend fun saveAuthToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[AUTH_TOKEN_KEY] = token
        }
    }

    suspend fun getAuthToken(): String? {
        return context.dataStore.data.map { preferences ->
            preferences[AUTH_TOKEN_KEY]
        }.first()
    }

    suspend fun clearAuthToken() {
        context.dataStore.edit { preferences ->
            preferences.remove(AUTH_TOKEN_KEY)
        }
    }
}