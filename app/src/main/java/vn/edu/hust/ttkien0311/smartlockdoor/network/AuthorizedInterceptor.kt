package vn.edu.hust.ttkien0311.smartlockdoor.network

import android.content.Context
import android.content.Intent
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import vn.edu.hust.ttkien0311.smartlockdoor.WelcomeActivity
import vn.edu.hust.ttkien0311.smartlockdoor.helper.EncryptedSharedPreferencesManager
import java.util.Date

class AuthorizedInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val sharedPreferencesManager = EncryptedSharedPreferencesManager(context)
        val accessToken = sharedPreferencesManager.getAccessToken()

        if (accessToken.isNotEmpty()) {
            val request = chain.request()
            val newRequest =
                request.newBuilder().addHeader("Authorization", "Bearer $accessToken").build()

            val response = chain.proceed(newRequest)
            if (response.code == 401) {
                Log.d("SLD", "Interceptor: Unauthorized, statusCode = 401")
                response.close()

                val refreshToken = sharedPreferencesManager.getRefreshToken()
                val refreshTokenExpires = sharedPreferencesManager.getRefreshTokenExpires()
                val currentTime = Date()

                return if (refreshToken.isNotEmpty() && refreshTokenExpires?.after(currentTime) == true) {
                    refreshToken(chain, request, sharedPreferencesManager)
                } else {
                    response
                }
            } else {
                return response
            }
        } else {
            return chain.proceed(chain.request())
        }
    }

    private fun logOut(sharedPreferencesManager: EncryptedSharedPreferencesManager) {
        Log.d("SLD", "Log out from interceptor")
        sharedPreferencesManager.saveAccessToken("")
        sharedPreferencesManager.saveRefreshToken("")
        sharedPreferencesManager.saveRefreshTokenExpires("")
        sharedPreferencesManager.saveLoginStatus(false)

        val intent = Intent(context, WelcomeActivity::class.java)
        context.startActivity(intent)
//        Activity().finish()
    }

    private fun refreshToken(
        chain: Interceptor.Chain,
        request: Request,
        sharedPreferencesManager: EncryptedSharedPreferencesManager
    ): Response {
        val response =
            ServerApi(context).retrofitService.refreshAccessToken(sharedPreferencesManager.getRefreshToken())
                .execute()
        return if (response.isSuccessful) {
            val accessToken = response.body()?.accessToken
            sharedPreferencesManager.saveAccessToken(response.body()?.accessToken!!)
            sharedPreferencesManager.saveRefreshToken(response.body()?.refreshToken!!)
            sharedPreferencesManager.saveRefreshTokenExpires(response.body()?.refreshTokenExpires!!)
            val newRequest = request.newBuilder()
                .addHeader("Authorization", "Bearer $accessToken")
                .build()

            chain.proceed(newRequest)
        } else {
            if (response.code() == 400) {
                logOut(sharedPreferencesManager)
            }
            chain.proceed(request)
        }
    }
}