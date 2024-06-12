package vn.edu.hust.ttkien0311.smartlockdoor.ui.welcome

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
import kotlinx.coroutines.launch
import vn.edu.hust.ttkien0311.smartlockdoor.R
import vn.edu.hust.ttkien0311.smartlockdoor.databinding.FragmentRegisterBinding
import vn.edu.hust.ttkien0311.smartlockdoor.helper.AlertDialogHelper.hideLoading
import vn.edu.hust.ttkien0311.smartlockdoor.helper.AlertDialogHelper.showLoading
import vn.edu.hust.ttkien0311.smartlockdoor.helper.ExceptionHelper.handleException
import vn.edu.hust.ttkien0311.smartlockdoor.helper.Helper.validateEmail
import vn.edu.hust.ttkien0311.smartlockdoor.helper.Helper.validatePassword
import vn.edu.hust.ttkien0311.smartlockdoor.helper.Helper.validateUsername
import vn.edu.hust.ttkien0311.smartlockdoor.network.AccountDto
import vn.edu.hust.ttkien0311.smartlockdoor.network.ServerApi

class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val emailInput = binding.email
        val emailLayout = binding.emailL
        val passwordInput = binding.password
        val passwordLayout = binding.passwordL
        val usernameInput = binding.username
        val usernameLayout = binding.usernameL

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

        usernameInput.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                validateUsername(usernameInput, usernameLayout)
            }
        }

        binding.toolbar.backButton.setOnClickListener {
            view.findNavController().popBackStack(R.id.welcomeFragment, false)
        }

        binding.buttonRegister.setOnClickListener {
            val emailChecked = validateEmail(emailInput, emailLayout)
            val passwordChecked = validatePassword(passwordInput, passwordLayout)
            val usernameChecked = validateUsername(usernameInput, usernameLayout)

            if (emailChecked && passwordChecked && usernameChecked) {
                lifecycleScope.launch {
                    val accountDto = AccountDto(
                        emailInput.text.toString().trim(),
                        passwordInput.text.toString().trim(),
                        usernameInput.text.toString().trim(),
                        ""
                    )
                    try {
                        showLoading(requireActivity())
                        val response = ServerApi(requireActivity()).retrofitService.register(accountDto)
                        Log.d("SLD", "Response: $response")

                        hideLoading()

                        val action =
                            RegisterFragmentDirections.actionRegisterFragmentToEmailNotifyFragment(
                                emailInput.text.toString().trim()
                            )

                        view.findNavController().navigate(action)
                    } catch (ex : Exception) {
                        hideLoading()
                        handleException(ex, requireActivity())
                    }
                }
            }
            else {
                Toast.makeText(activity, "Thông tin không hợp lệ", Toast.LENGTH_LONG).show()
            }
        }

        binding.loginNow.setOnClickListener {
            view.findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }
}