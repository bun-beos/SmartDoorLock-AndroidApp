package vn.edu.hust.ttkien0311.smartlockdoor.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

private const val BASE_URL = "http://192.168.0.105:8011"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val okHttpClient = OkHttpClient.Builder()
    .readTimeout(10, TimeUnit.SECONDS)
    .build()

private val retrofit = Retrofit.Builder()
    .client(okHttpClient)
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

object ServerApi {
    val retrofitService :ServerApiService by lazy {
        retrofit.create(ServerApiService::class.java)
    }
}

interface ServerApiService {
    // Account
    // Đăng ký tài khoản
    @POST("/api/v1/Accounts/Registration")
    suspend fun register(@Body accountDto: AccountDto) : Int

    // Đăng nhập
    @POST("/api/v1/Accounts/Login")
    suspend fun login(@Body accountDto: AccountDto) : Token

    // Quên mật khẩu
    @POST("/api/v1/Accounts/ForgotPassword")
    suspend fun forgotPassword(@Body email : String) : Int

    // Đặt lại mật khẩu
    @POST("/api/v1/Accounts/ResetPassword")
    suspend fun resetPassword(@Body passwordReset: PasswordReset) : Int

}