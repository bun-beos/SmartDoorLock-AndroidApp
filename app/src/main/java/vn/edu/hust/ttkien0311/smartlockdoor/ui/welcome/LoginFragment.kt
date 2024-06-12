package vn.edu.hust.ttkien0311.smartlockdoor.ui.welcome

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import vn.edu.hust.ttkien0311.smartlockdoor.ui.main.MainActivity
import vn.edu.hust.ttkien0311.smartlockdoor.R
import vn.edu.hust.ttkien0311.smartlockdoor.databinding.FragmentLoginBinding
import vn.edu.hust.ttkien0311.smartlockdoor.helper.AlertDialogHelper.hideLoading
import vn.edu.hust.ttkien0311.smartlockdoor.helper.AlertDialogHelper.showLoading
import vn.edu.hust.ttkien0311.smartlockdoor.helper.EncryptedSharedPreferencesManager
import vn.edu.hust.ttkien0311.smartlockdoor.helper.ExceptionHelper.handleException
import vn.edu.hust.ttkien0311.smartlockdoor.helper.Helper.validateEmail
import vn.edu.hust.ttkien0311.smartlockdoor.helper.Helper.validatePassword
import vn.edu.hust.ttkien0311.smartlockdoor.network.AccountDto
import vn.edu.hust.ttkien0311.smartlockdoor.network.ServerApi

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private var phoneToken: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("SLD", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            // Get new FCM registration token
            phoneToken = task.result
            Log.d("SLD", "FCM registration token: $phoneToken")
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val emailInput = binding.email
        val emailLayout = binding.emailL
        val passwordInput = binding.password
        val passwordLayout = binding.passwordL

        emailInput.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                validateEmail(emailInput, emailLayout)
            }
        }

        passwordInput.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                validatePassword(passwordInput, passwordLayout)
            }
        }

        val buttonLogin = binding.buttonLogin
        val textSignupNow = binding.registerNow
        val textForgotPassword = binding.forgotPassword

        binding.toolbar.backButton.setOnClickListener {
            view.findNavController().popBackStack(R.id.welcomeFragment, false)
        }

        buttonLogin.setOnClickListener {
            val emailChecked = validateEmail(emailInput, emailLayout)
            val passwordChecked = validatePassword(passwordInput, passwordLayout)

            if (emailChecked && passwordChecked) {
                lifecycleScope.launch {
                    try {
                        val accountDto = AccountDto(
                            emailInput.text.toString().trim(),
                            passwordInput.text.toString().trim(),
                            "",
                            phoneToken
                        )

                        showLoading(requireActivity())
                        delay(600)

                        val response = ServerApi(requireActivity()).retrofitService.login(accountDto)

                        val sharedPreferencesManager = EncryptedSharedPreferencesManager(requireContext())
                        sharedPreferencesManager.saveAccessToken(response.accessToken)
                        sharedPreferencesManager.saveRefreshToken(response.refreshToken)
                        sharedPreferencesManager.saveRefreshTokenExpires(response.refreshTokenExpires)
                        sharedPreferencesManager.saveLoginStatus(true)

                        val account = ServerApi(requireActivity()).retrofitService.getAccountInfo()
                        hideLoading()

                        sharedPreferencesManager.saveAccountId(account.accountId)

                        val intent = Intent(activity, MainActivity::class.java)
                        startActivity(intent)
                        requireActivity().finish()
                    } catch (ex: Exception) {
                        hideLoading()
                        handleException(ex, requireActivity())
                    }
                }
            } else {
                Toast.makeText(activity, "Thông tin không hợp lệ", Toast.LENGTH_LONG).show()
            }
        }

        textSignupNow.setOnClickListener {
            view.findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        textForgotPassword.setOnClickListener {
            val action = LoginFragmentDirections.actionLoginFragmentToForgotPasswordFragment(
                emailInput.text.toString().trim()
            )
            view.findNavController().navigate(action)
        }
    }
}