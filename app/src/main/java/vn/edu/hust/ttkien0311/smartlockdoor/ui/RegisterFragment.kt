package vn.edu.hust.ttkien0311.smartlockdoor.ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.launch
import retrofit2.HttpException
import vn.edu.hust.ttkien0311.smartlockdoor.R
import vn.edu.hust.ttkien0311.smartlockdoor.databinding.FragmentRegisterBinding
import vn.edu.hust.ttkien0311.smartlockdoor.helper.AlertDialogHelper
import vn.edu.hust.ttkien0311.smartlockdoor.network.AccountDto
import vn.edu.hust.ttkien0311.smartlockdoor.network.ErrorResponse
import vn.edu.hust.ttkien0311.smartlockdoor.network.ServerApi
import vn.edu.hust.ttkien0311.smartlockdoor.network.decodeUnicodeEscape
import java.net.SocketException
import java.net.SocketTimeoutException

class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding

    private lateinit var emailInput: TextInputEditText
    private lateinit var emailLayout: TextInputLayout
    private lateinit var passwordInput: TextInputEditText
    private lateinit var passwordLayout: TextInputLayout
    private lateinit var usernameInput: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_register, container, false)
        return binding.root
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        emailInput = binding.email
        emailLayout = binding.emailL
        passwordInput = binding.password
        passwordLayout = binding.passwordL
        usernameInput = binding.username

        emailInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            @SuppressLint("UseCompatLoadingForDrawables")
            override fun afterTextChanged(s: Editable?) {
                when {
                    emailInput.text.toString().trim().isEmpty() -> {
                        emailLayout.error = "Email không được để trống."
                        emailInput.background =
                            resources.getDrawable(R.drawable.custom_edittext_error, null)
                    }

                    emailInput.text.toString().trim().isNotEmpty() -> {
                        emailLayout.error = null
                        emailInput.background =
                            resources.getDrawable(R.drawable.custom_edittext, null)
                    }
                }
            }
        })

        passwordInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            @SuppressLint("UseCompatLoadingForDrawables")
            override fun afterTextChanged(s: Editable?) {
                when (passwordInput.text.toString().trim().length) {
                    in 6..12 -> {
                        passwordLayout.error = null
                        passwordInput.background =
                            resources.getDrawable(R.drawable.custom_edittext, null)
                    }

                    else -> {
                        passwordLayout.error = "Mật khẩu cần có độ dài 6-12 ký tự."
                        passwordInput.background =
                            resources.getDrawable(R.drawable.custom_edittext_error, null)
                    }
                }
            }
        })

        binding.buttonRegister.setOnClickListener {
            if (!validateEmail()) {
                emailInput.background =
                    resources.getDrawable(R.drawable.custom_edittext_error, null)
            }

            if (!validatePassword()) {
                passwordInput.background =
                    resources.getDrawable(R.drawable.custom_edittext_error, null)
            }

            if (validateEmail() && validatePassword()) {
                emailInput.background = resources.getDrawable(R.drawable.custom_edittext, null)

                lifecycleScope.launch {
                    val accountDto = AccountDto(
                        emailInput.text.toString().trim(),
                        passwordInput.text.toString().trim(),
                        usernameInput.text.toString().trim()
                    )
                    try {
                        activity?.let { AlertDialogHelper.showLoading(it) }

                        val response = ServerApi.retrofitService.register(accountDto)
                        Log.d("SLD", "Response: $response")

                        AlertDialogHelper.hideLoading()

                        val action =
                            RegisterFragmentDirections.actionRegisterFragmentToEmailNotifyFragment(
                                emailInput.text.toString().trim()
                            )

                        view.findNavController().navigate(action)
                    } catch (ex : Exception) {
                        AlertDialogHelper.hideLoading()

                        when (ex) {
                            is SocketException -> {
                                activity?.let { it1 ->
                                    AlertDialogHelper.showAlertDialog(
                                        it1,
                                        "Lỗi kết nối",
                                        "Không thể kết nối đến hệ thống. Vui lòng kiểm tra kết nối mạng hoặc thử lại sau."
                                    )
                                }
                            }

                            is HttpException -> {
                                val msg =
                                    decodeUnicodeEscape(ex.response()?.errorBody()?.string()!!)
                                val adapter: JsonAdapter<ErrorResponse> =
                                    Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                                        .adapter(ErrorResponse::class.java)
                                val jsonMsg = adapter.fromJson(msg)
                                Toast.makeText(
                                    activity,
                                    "${jsonMsg?.userMessage}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                            else -> {
                                Log.d("SLD", "$ex")
                                Toast.makeText(
                                    activity,
                                    "Có lỗi xảy ra, vui lòng thử lại sau",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                }
            }
        }

        binding.loginNow.setOnClickListener {
            view.findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }

    private fun validateEmail(): Boolean {
        val emailPattern = Regex("[a-zA-Z\\d._-]+@[a-z]+\\.+[a-z]+")
        return when {
            emailInput.text.toString().trim().isEmpty() -> {
                emailLayout.error = "Email không được để trống."
                false
            }

            !emailInput.text.toString().trim().matches(emailPattern) -> {
                emailLayout.error = "Email không hợp lệ."
                false
            }

            else -> {
                emailLayout.error = null
                true
            }
        }
    }

    private fun validatePassword(): Boolean {
        return when {
            passwordInput.text.toString().trim().length < 6 || passwordInput.text.toString()
                .trim().length > 12 -> {
                passwordLayout.error = "Mật khẩu cần có độ dài 6-12 ký tự."
                false
            }

            else -> {
                passwordLayout.error = null
                true
            }
        }
    }
}