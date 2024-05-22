package vn.edu.hust.ttkien0311.smartlockdoor.helper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Base64
import android.util.Patterns
import androidx.annotation.RequiresApi
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.io.ByteArrayOutputStream
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

object Helper {
    fun validateEmail(emailInput: TextInputEditText, emailLayout: TextInputLayout): Boolean {
        val emailPattern = Regex("[a-zA-Z\\d._-]+@[a-z]+\\.+[a-z]+")
        val email = emailInput.text.toString().trim()
        return when {
            email.isEmpty() -> {
                emailLayout.helperText = "Email không được để trống."
                false
            }

            !email.matches(emailPattern) -> {
                emailLayout.helperText = "Email không hợp lệ."
                false
            }

            else -> {
                emailLayout.helperText = " "
                true
            }
        }
    }

    fun validatePassword(
        passwordInput: TextInputEditText,
        passwordLayout: TextInputLayout
    ): Boolean {
        val password = passwordInput.text.toString().trim()
        return if (password.length < 6) {
            passwordLayout.helperText = "Mật khẩu cần có độ dài tối thiểu 6 ký tự."
            false
        } else if (password.contains(" ")) {
            passwordLayout.helperText = "Mật khẩu không được chứa dấu cách."
            false
        } else {
            passwordLayout.helperText = " "
            true
        }
    }

    fun validateUsername(
        usernameInput: TextInputEditText,
        userNameLayout: TextInputLayout
    ): Boolean {
        val username = usernameInput.text.toString().trim()
        return if (username.length > 50) {
            userNameLayout.helperText = "Tên không được vượt quá 50 ký tự."
            false
        } else {
            userNameLayout.helperText = " "
            true
        }
    }

    fun validatePasswordToken(
        passwordTokenInput: TextInputEditText,
        passwordTokenLayout: TextInputLayout
    ): Boolean {
        return if (passwordTokenInput.text.toString().trim().length == 8) {
            passwordTokenLayout.helperText = " "
            true
        } else {
            passwordTokenLayout.helperText = "Mã xác nhận có độ dài 8 ký tự."
            false
        }
    }

    fun comparePassword(
        newPassword: String,
        confirmNewPassword: String,
        confirmPasswordLayout: TextInputLayout
    ): Boolean {
        return if (newPassword.equals(confirmNewPassword)) {
            confirmPasswordLayout.helperText = " "
            true
        } else {
            confirmPasswordLayout.helperText = "Mật khẩu không trùng khớp."
            false
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun formatDateTime(date: String?, pattern: String): String {
        if (date.isNullOrEmpty()) {
            return ""
        }
        val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
        val dateTime = OffsetDateTime.parse(date, formatter)
        return dateTime.format(DateTimeFormatter.ofPattern(pattern))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun validateDateOfBirth(dateOfBirth: String?): Boolean {
        if (dateOfBirth.isNullOrEmpty()) {
            return true
        }
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        return try {
            val date = LocalDate.parse(dateOfBirth, formatter)
            val today = LocalDate.now()

            date.isBefore(today)
        } catch (e: DateTimeParseException) {
            false
        }

    }

    fun validatePhoneNumber(phoneNumber: String?): Boolean {
        if (phoneNumber.isNullOrEmpty()) {
            return true
        }
        val regex = Regex("""^(0|\\+84)([0-9]{8}|[0-9]{9})$""")
        return regex.matches(phoneNumber)
    }

    fun bitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }
}
