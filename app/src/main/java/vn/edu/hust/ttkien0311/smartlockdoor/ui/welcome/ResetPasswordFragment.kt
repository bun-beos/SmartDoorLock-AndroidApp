package vn.edu.hust.ttkien0311.smartlockdoor.ui.welcome

import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import vn.edu.hust.ttkien0311.smartlockdoor.R
import vn.edu.hust.ttkien0311.smartlockdoor.databinding.FragmentResetPasswordBinding
import vn.edu.hust.ttkien0311.smartlockdoor.helper.AlertDialogHelper.hideLoading
import vn.edu.hust.ttkien0311.smartlockdoor.helper.AlertDialogHelper.showLoading
import vn.edu.hust.ttkien0311.smartlockdoor.helper.ExceptionHelper.handleException
import vn.edu.hust.ttkien0311.smartlockdoor.helper.Helper.comparePassword
import vn.edu.hust.ttkien0311.smartlockdoor.helper.Helper.validatePassword
import vn.edu.hust.ttkien0311.smartlockdoor.helper.Helper.validatePasswordToken
import vn.edu.hust.ttkien0311.smartlockdoor.network.PasswordReset
import vn.edu.hust.ttkien0311.smartlockdoor.network.ServerApi

class ResetPasswordFragment : Fragment() {
    private lateinit var binding: FragmentResetPasswordBinding
    private val args: ResetPasswordFragmentArgs by navArgs()

    private lateinit var timer : CountDownTimer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_reset_password, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        timer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000

                binding.countdown.visibility = View.VISIBLE
                binding.countdown.text = "(${seconds}s)"

                binding.resendTextView.isClickable = false
                binding.resendTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.hint_text))
            }

            override fun onFinish() {
                binding.countdown.visibility = View.INVISIBLE

                binding.resendTextView.isClickable = true
                binding.resendTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.link_color))
            }
        }

        timer.start()

        val passwordTokenInput = binding.passwordCode
        val passwordTokenLayout = binding.passwordCodeL
        val newPasswordInput = binding.newPassword
        val newPasswordLayout = binding.newPasswordL
        val confirmPasswordInput = binding.confirmPassword
        val confirmPasswordLayout = binding.confirmPasswordL

        passwordTokenInput.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                validatePasswordToken(passwordTokenInput, passwordTokenLayout)
            }
        }

        newPasswordInput.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                validatePassword(newPasswordInput, newPasswordLayout)
            }
        }

        confirmPasswordInput.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                comparePassword(
                    newPasswordInput.text.toString().trim(),
                    confirmPasswordInput.text.toString().trim(),
                    confirmPasswordLayout
                )
            }
        }

        binding.toolbar.backButton.setOnClickListener {
            view.findNavController().popBackStack(R.id.loginFragment, false)
        }

        binding.sendButton.setOnClickListener {
            val tokenChecked = validatePasswordToken(passwordTokenInput, passwordTokenLayout)
            val passwordChecked = validatePassword(newPasswordInput, newPasswordLayout)
            val compareChecked = comparePassword(
                newPasswordInput.text.toString().trim(),
                confirmPasswordInput.text.toString().trim(),
                confirmPasswordLayout
            )

            if (tokenChecked && passwordChecked && compareChecked) {
                lifecycleScope.launch {
                    try {
                        val passwordReset = PasswordReset(
                            passwordTokenInput.text.toString().trim(),
                            newPasswordInput.text.toString().trim(),
                            confirmPasswordInput.text.toString().trim()
                        )

                        showLoading(requireActivity())
                        delay(1000)

                        val result = ServerApi(requireActivity()).retrofitService.resetPassword(
                            passwordReset
                        )

                        hideLoading()
                        if (result != 0) {
                            view.findNavController()
                                .navigate(R.id.action_resetPasswordFragment_to_loginFragment)
                        } else {
                            Toast.makeText(
                                activity,
                                "Có lỗi xảy ra, vui lòng thử lại sau",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } catch (ex: Exception) {
                        hideLoading()
                        handleException(ex, requireActivity())
                    }
                }
            } else {
                Toast.makeText(activity, "Thông tin không hợp lệ", Toast.LENGTH_LONG).show()
            }
        }

        binding.resendTextView.setOnClickListener {
            lifecycleScope.launch {
                try {
                    showLoading(requireActivity())
                    val result = ServerApi(requireActivity()).retrofitService.forgotPassword(args.email)

                    hideLoading()
                    timer.start()
                    if (result != 0) {
                        Toast.makeText(activity, "Thành công", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(
                            activity,
                            "Có lỗi xảy ra, vui lòng thử lại sau",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } catch (ex: Exception) {
                    hideLoading()
                    handleException(ex, requireActivity())
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        timer.cancel()
    }
}