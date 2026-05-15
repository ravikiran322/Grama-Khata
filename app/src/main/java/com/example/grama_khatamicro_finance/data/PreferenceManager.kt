package com.example.grama_khatamicro_finance.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class PreferenceManager(context: Context) {
    private val dataStore = context.dataStore

    // Helper to generate user-specific keys
    private fun shopKey(userId: String) = stringPreferencesKey("shop_name_$userId")
    private fun pinKey(userId: String) = stringPreferencesKey("security_pin_$userId")
    private val darkKey = booleanPreferencesKey("dark_mode") // Dark mode can remain global or be user-specific

    fun getShopName(userId: String): Flow<String?> = dataStore.data.map { it[shopKey(userId)] }
    fun getSecurityPin(userId: String): Flow<String?> = dataStore.data.map { it[pinKey(userId)] }
    val isDarkMode: Flow<Boolean> = dataStore.data.map { it[darkKey] ?: false }

    suspend fun saveShopName(userId: String, name: String) {
        dataStore.edit { it[shopKey(userId)] = name }
    }

    suspend fun savePin(userId: String, pin: String) {
        dataStore.edit { it[pinKey(userId)] = pin }
    }

    suspend fun clearPin(userId: String) {
        dataStore.edit { it.remove(pinKey(userId)) }
    }

    suspend fun setDarkMode(enabled: Boolean) {
        dataStore.edit { it[darkKey] = enabled }
    }
}
