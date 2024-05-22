package vn.edu.hust.ttkien0311.smartlockdoor.network

import androidx.lifecycle.LiveData
import com.squareup.moshi.Json
import java.time.LocalDate
import java.util.Date

data class AccountDto(
    @Json(name = "Email") val email: String,
    @Json(name = "Password") val password: String,
    @Json(name = "Username") val username: String,
)

data class Account(
    @Json(name = "AccountId") val accountId: String,
    @Json(name = "Email") val email: String,
    @Json(name = "Username") val username: String,
    @Json(name = "UserImage") val userImage: String,
    @Json(name = "PasswordHash") val passwordHash: String,
    @Json(name = "PasswordSalt") val passwordSalt: String,
    @Json(name = "VerifyToken") val verifyToken: String,
    @Json(name = "VerifyTokenExpires") val verifyTokenExpires: String,
    @Json(name = "VerifiedDate") val verifiedDate: String,
    @Json(name = "RefreshToken") val refreshToken: String,
    @Json(name = "RefreshTokenExpires") val refreshTokenExpires: String,
    @Json(name = "PasswordToken") val passwordToken: String,
    @Json(name = "PasswordTokenExpires") val passwordTokenExpires: String,
    @Json(name = "ModifiedDate") val modifiedDate: String,
)

data class Token(
    @Json(name = "AccessToken") val accessToken: String,
    @Json(name = "RefreshToken") val refreshToken: String,
    @Json(name = "RefreshTokenExpires") val refreshTokenExpires: String
)

data class ErrorResponse(
    @Json(name = "DevMessage") val devMessage: String,
    @Json(name = "UserMessage") val userMessage: String
)

data class PasswordReset(
    @Json(name = "PasswordToken") val passwordToken: String,
    @Json(name = "NewPassword") val newPassword: String,
    @Json(name = "CompareNewPassword") val compareNewPassword: String
)

data class PasswordChange(
    @Json(name = "CurrentPassword") val currentPassword: String,
    @Json(name = "NewPassword") val newPassword: String,
    @Json(name = "CompareNewPassword") val compareNewPassword: String
)

data class MemberDto(
    @Json(name = "MemberName") val memberName: String,
    @Json(name = "MemberPhoto") val memberPhoto: String,
    @Json(name = "DateOfBirth") val dateOfBirth: String?,
    @Json(name = "PhoneNumber") val phoneNumber: String?
)

data class Member(
    @Json(name = "MemberId") val memberId: String,
    @Json(name = "MemberName") val memberName: String,
    @Json(name = "MemberPhoto") val memberPhoto: String,
    @Json(name = "DateOfBirth") var dateOfBirth: String?,
    @Json(name = "PhoneNumber") val phoneNumber: String?,
    @Json(name = "CreatedDate") var createdDate: String,
    @Json(name = "CreatedBy") val createdBy: String,
    @Json(name = "ModifiedDate") var modifiedDate: String,
    @Json(name = "ModifiedBy") val modifiedBy: String
)

data class Image(
    @Json(name = "ImageId") val imageId: String,
    @Json(name = "ImageLink") val imageLink: String,
    @Json(name = "CreatedDate") var createdDate: String,
    @Json(name = "CreatedBy") val createdBy: String,
    @Json(name = "MemberId") val memberId: String,
    @Json(name = "MemberName") val memberName: String,
    @Json(name = "MemberPhoto") val memberPhoto: String

)

data class Date(
    var date: LocalDate,
    var isExpanded: Boolean = false
)