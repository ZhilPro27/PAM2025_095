package com.example.perpustakaan_app.repostitori

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session_setting")

class UserPreferences(private val context: Context) {
    companion object {
        private val TOKEN_KEY = stringPreferencesKey("auth_token")
        private val ID_USER_KEY = stringPreferencesKey("id_user")
        private val EMAIL_KEY = stringPreferencesKey("email_user")
    }
    val authToken: Flow<String?> = context.dataStore.data.map { it[TOKEN_KEY] }
    val getUserId: Flow<String?> = context.dataStore.data.map { it[ID_USER_KEY] }
    val userEmail: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[EMAIL_KEY] ?: "Email tidak ditemukan"
        }

    suspend fun saveSession(token: String, id: String, email: String) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
            preferences[ID_USER_KEY] = id
            preferences[EMAIL_KEY] = email
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}