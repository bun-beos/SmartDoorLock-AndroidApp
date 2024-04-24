package vn.edu.hust.ttkien0311.smartlockdoor.helper

import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

object ValidateHelper {
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
}
