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

private const val BASE_URL = "http://192.168.0.104:8011"

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
    @POST("/api/v1/Accounts/Registration")
    suspend fun register(@Body accountDto: AccountDto) : Int

    @POST("/api/v1/Accounts/Login")
    suspend fun login(@Body accountDto: AccountDto) : Token
}

fun decodeUnicodeEscape(input: String): String {
    val pattern = Pattern.compile("\\\\u([0-9a-fA-F]{4})")
    val matcher = pattern.matcher(input)
    val buffer = StringBuffer(input.length)
    while (matcher.find()) {
        val hex = matcher.group(1)
        val codePoint = Integer.parseInt(hex!!, 16)
        matcher.appendReplacement(buffer, String(Character.toChars(codePoint)))
    }
    matcher.appendTail(buffer)
    return buffer.toString()
}