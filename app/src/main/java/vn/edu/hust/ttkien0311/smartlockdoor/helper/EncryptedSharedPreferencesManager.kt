package vn.edu.hust.ttkien0311.smartlockdoor.helper

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EncryptedSharedPreferencesManager(context: Context) {
    private val masterKeys = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
    private val sharedPreferences = EncryptedSharedPreferences.create(
        "token_shared_prefs",
        masterKeys,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveAccessToken(accessToken: String) {
        sharedPreferences.edit()
            .putString("access_token", accessToken)
            .apply()
    }

    fun getAccessToken(): String {
        return sharedPreferences.getString("access_token", "").toString()
    }

    fun saveRefreshToken(refreshToken: String) {
        sharedPreferences.edit()
            .putString("refresh_token", refreshToken)
            .apply()
    }

    fun getRefreshToken(): String {
        return sharedPreferences.getString("refresh_token", "").toString()
    }

    fun saveRefreshTokenExpires(refreshTokeExpires: String) {
        sharedPreferences.edit()
            .putString("refresh_token_expires", refreshTokeExpires)
            .apply()
    }

    fun getRefreshTokenExpires(): Date? {
        val tokenExpires = sharedPreferences.getString("refresh_token_expires", "").toString()
        return if (tokenExpires.isNotEmpty()) {
            SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss",
                Locale.getDefault()
            ).parse(tokenExpires.substring(0, 20))
        } else null
    }

    fun saveLoginStatus(status: Boolean) {
        sharedPreferences.edit()
            .putBoolean("is_login", status)
            .apply()
    }

    fun getLoginStatus(): Boolean {
        return sharedPreferences.getBoolean("is_login", false)
    }

    fun saveSelectedDevice(deviceId: String) {
        sharedPreferences.edit()
            .putString("device_id", deviceId)
            .apply()
    }

    fun getSelectedDevice(): String {
        return sharedPreferences.getString("device_id", "").toString()
    }

    fun saveAccountId(accountId: String) {
        sharedPreferences.edit()
            .putString("account_id", accountId)
            .apply()
    }
    
    fun getAccountId(): String {
        return sharedPreferences.getString("account_id", "").toString()
    }
}