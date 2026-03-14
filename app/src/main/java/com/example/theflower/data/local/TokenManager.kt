package com.example.theflower.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val PREFERENCES_NAME = "theflower_preferences"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = PREFERENCES_NAME)

object PreferencesKeys {
    val ACCESS_TOKEN = stringPreferencesKey("access_token")
    val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
    val USER_ID = stringPreferencesKey("user_id")
    val USER_EMAIL = stringPreferencesKey("user_email")
    val TOKEN_EXPIRES_IN = stringPreferencesKey("token_expires_in")
}

/**
 * Token Manager - Handles token storage and retrieval
 */
class TokenManager(private val context: Context) {
    
    val accessTokenFlow: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.ACCESS_TOKEN]
    }
    
    val refreshTokenFlow: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.REFRESH_TOKEN]
    }
    
    val userIdFlow: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.USER_ID]
    }
    
    suspend fun saveTokens(accessToken: String, refreshToken: String, expiresIn: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.ACCESS_TOKEN] = accessToken
            preferences[PreferencesKeys.REFRESH_TOKEN] = refreshToken
            preferences[PreferencesKeys.TOKEN_EXPIRES_IN] = expiresIn.toString()
        }
    }
    
    suspend fun saveUserInfo(userId: String, email: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_ID] = userId
            preferences[PreferencesKeys.USER_EMAIL] = email
        }
    }
    
    suspend fun getAccessToken(): String? {
        var token: String? = null
        context.dataStore.data.map { preferences ->
            token = preferences[PreferencesKeys.ACCESS_TOKEN]
        }.collect { }
        return token
    }
    
    suspend fun getRefreshToken(): String? {
        var token: String? = null
        context.dataStore.data.map { preferences ->
            token = preferences[PreferencesKeys.REFRESH_TOKEN]
        }.collect { }
        return token
    }
    
    suspend fun clearTokens() {
        context.dataStore.edit { preferences ->
            preferences.remove(PreferencesKeys.ACCESS_TOKEN)
            preferences.remove(PreferencesKeys.REFRESH_TOKEN)
            preferences.remove(PreferencesKeys.USER_ID)
            preferences.remove(PreferencesKeys.USER_EMAIL)
            preferences.remove(PreferencesKeys.TOKEN_EXPIRES_IN)
        }
    }
    
    suspend fun isTokenExpired(): Boolean {
        return getAccessToken().isNullOrEmpty()
    }
}
