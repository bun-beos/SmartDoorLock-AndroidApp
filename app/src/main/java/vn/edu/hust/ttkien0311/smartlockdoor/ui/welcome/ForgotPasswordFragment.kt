package vn.edu.hust.ttkien0311.smartlockdoor.ui.welcome

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.launch
import vn.edu.hust.ttkien0311.smartlockdoor.R
import vn.edu.hust.ttkien0311.smartlockdoor.databinding.FragmentForgotPasswordBinding
import vn.edu.hust.ttkien0311.smartlockdoor.helper.AlertDialogHelper.hideLoading
import vn.edu.hust.ttkien0311.smartlockdoor.helper.AlertDialogHelper.showLoading
import vn.edu.hust.ttkien0311.smartlockdoor.helper.ExceptionHelper.handleException
import vn.edu.hust.ttkien0311.smartlockdoor.helper.ValidateHelper.validateEmail
import vn.edu.hust.ttkien0311.smartlockdoor.network.ServerApi


class ForgotPasswordFragment : Fragment() {
    private lateinit var binding: FragmentForgotPasswordBinding
    private val args: ForgotPasswordFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_forgot_password, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val emailInput = binding.email
        val emailLayout = binding.emailL

        emailInput.setText(args.email)

        emailInput.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                validateEmail(emailInput, emailLayout)
            }
        }

        binding.toolbar.backButton.setOnClickListener {
            view.findNavController().popBackStack()
        }

        binding.sendButton.setOnClickListener {
            if (validateEmail(emailInput, emailLayout)) {
                lifecycleScope.launch {
                    try {
                        activity?.let { showLoading(it) }

                        val result = ServerApi.retrofitService.forgotPassword(
                            emailInput.text.toString().trim()
                        )

                        hideLoading()
                        if (result != 0) {
                            val action = ForgotPasswordFragmentDirections.actionForgotPasswordFragmentToResetPasswordFragment(emailInput.text.toString().trim())
                            view.findNavController().navigate(action)
                        } else {
                            Toast.makeText(
                                activity,
                                "Có lỗi xảy ra, vui lòng thử lại sau",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } catch (ex: Exception) {
                        hideLoading()
                        activity?.let { handleException(ex, it) }
                    }
                }
            } else {
                Toast.makeText(activity, "Thông tin không hợp lệ", Toast.LENGTH_LONG).show()
            }
        }
    }
}