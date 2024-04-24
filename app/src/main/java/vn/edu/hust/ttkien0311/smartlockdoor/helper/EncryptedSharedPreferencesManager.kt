package vn.edu.hust.ttkien0311.smartlockdoor.helper

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

class EncryptedSharedPreferencesManager(context : Context) {
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

    fun saveLoginStatus(status : Boolean) {
        sharedPreferences.edit()
            .putBoolean("is_login", status)
            .apply()
    }

    fun getLoginStatus() : Boolean {
        return sharedPreferences.getBoolean("is_login", false)
    }
}