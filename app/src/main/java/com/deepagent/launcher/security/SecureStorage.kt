package com.deepagent.launcher.security

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class SecureStorage(context: Context) {
    
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()
    
    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "deepagent_secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
    
    fun saveInviteToken(token: String) {
        sharedPreferences.edit().putString(KEY_INVITE_TOKEN, token).apply()
    }
    
    fun getInviteToken(): String? {
        return sharedPreferences.getString(KEY_INVITE_TOKEN, null)
    }
    
    fun isActivated(): Boolean {
        return getInviteToken() != null
    }
    
    fun saveApiKey(service: String, key: String) {
        sharedPreferences.edit().putString("api_key_$service", key).apply()
    }
    
    fun getApiKey(service: String): String? {
        return sharedPreferences.getString("api_key_$service", null)
    }
    
    fun deleteApiKey(service: String) {
        sharedPreferences.edit().remove("api_key_$service").apply()
    }
    
    fun getAllApiKeys(): Map<String, String> {
        val allKeys = mutableMapOf<String, String>()
        sharedPreferences.all.forEach { (key, value) ->
            if (key.startsWith("api_key_") && value is String) {
                allKeys[key.removePrefix("api_key_")] = value
            }
        }
        return allKeys
    }
    
    fun saveBiometricEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_BIOMETRIC_ENABLED, enabled).apply()
    }
    
    fun isBiometricEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_BIOMETRIC_ENABLED, false)
    }
    
    companion object {
        private const val KEY_INVITE_TOKEN = "invite_token"
        private const val KEY_BIOMETRIC_ENABLED = "biometric_enabled"
    }
}
