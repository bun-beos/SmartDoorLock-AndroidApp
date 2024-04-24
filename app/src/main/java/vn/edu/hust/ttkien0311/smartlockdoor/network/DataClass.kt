package vn.edu.hust.ttkien0311.smartlockdoor.network

import com.squareup.moshi.Json

data class AccountDto (
    @Json(name = "Email") val email : String,
    @Json(name = "Password") val password : String,
    @Json(name = "Username") val username : String,
)

data class Token (
    @Json(name = "AccessToken") val accessToken : String,
    @Json(name = "RefreshToken") val refreshToken : String,
)

data class ErrorResponse (
    @Json(name = "DevMessage") val devMessage : String,
    @Json(name = "UserMessage") val userMessage : String
)