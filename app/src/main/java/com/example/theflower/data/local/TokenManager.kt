package com.example.theflower.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val PREFERENCES_NAME = "theflower_preferences"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = PREFERENCES_NAME)

object PreferencesKeys {
    val ACCESS_TOKEN = stringPreferencesKey("access_token")
    val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
    val USER_ID = stringPreferencesKey("user_id")
    val USER_EMAIL = stringPreferencesKey("user_email")
    val USER_NAME = stringPreferencesKey("user_name")
    val USER_PHONE = stringPreferencesKey("user_phone")
    val USER_ADDRESS = stringPreferencesKey("user_address")
    val USER_ROLE = stringPreferencesKey("user_role")
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

    val userEmailFlow: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.USER_EMAIL]
    }
    
    suspend fun saveTokens(accessToken: String, refreshToken: String, expiresIn: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.ACCESS_TOKEN] = accessToken
            preferences[PreferencesKeys.REFRESH_TOKEN] = refreshToken
            preferences[PreferencesKeys.TOKEN_EXPIRES_IN] = expiresIn.toString()
        }
    }
    
    suspend fun saveUserInfo(
        userId: String,
        email: String,
        userName: String = "",
        phoneNumber: String = "",
        address: String = "",
        role: String = ""
    ) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_ID] = userId
            preferences[PreferencesKeys.USER_EMAIL] = email
            preferences[PreferencesKeys.USER_NAME] = userName
            preferences[PreferencesKeys.USER_PHONE] = phoneNumber
            preferences[PreferencesKeys.USER_ADDRESS] = address
            preferences[PreferencesKeys.USER_ROLE] = role
        }
    }

    suspend fun getUserId(): String? {
        return context.dataStore.data.first()[PreferencesKeys.USER_ID]
    }

    suspend fun getUserEmail(): String? {
        return context.dataStore.data.first()[PreferencesKeys.USER_EMAIL]
    }

    suspend fun getUserName(): String? {
        return context.dataStore.data.first()[PreferencesKeys.USER_NAME]
    }

    suspend fun getUserPhone(): String? {
        return context.dataStore.data.first()[PreferencesKeys.USER_PHONE]
    }

    suspend fun getUserAddress(): String? {
        return context.dataStore.data.first()[PreferencesKeys.USER_ADDRESS]
    }

    suspend fun getUserRole(): String? {
        return context.dataStore.data.first()[PreferencesKeys.USER_ROLE]
    }
    
    suspend fun getAccessToken(): String? {
        return context.dataStore.data.first()[PreferencesKeys.ACCESS_TOKEN]
    }
    
    suspend fun getRefreshToken(): String? {
        return context.dataStore.data.first()[PreferencesKeys.REFRESH_TOKEN]
    }
    
    suspend fun clearTokens() {
        context.dataStore.edit { preferences ->
            preferences.remove(PreferencesKeys.ACCESS_TOKEN)
            preferences.remove(PreferencesKeys.REFRESH_TOKEN)
            preferences.remove(PreferencesKeys.USER_ID)
            preferences.remove(PreferencesKeys.USER_EMAIL)
            preferences.remove(PreferencesKeys.USER_NAME)
            preferences.remove(PreferencesKeys.USER_PHONE)
            preferences.remove(PreferencesKeys.USER_ADDRESS)
            preferences.remove(PreferencesKeys.USER_ROLE)
            preferences.remove(PreferencesKeys.TOKEN_EXPIRES_IN)
        }
    }
    
    suspend fun isTokenExpired(): Boolean {
        return getAccessToken().isNullOrEmpty()
    }
}
