package vn.edu.hust.ttkien0311.smartlockdoor.ui.main.profile

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import vn.edu.hust.ttkien0311.smartlockdoor.R
import vn.edu.hust.ttkien0311.smartlockdoor.databinding.FragmentChangePasswordBinding
import vn.edu.hust.ttkien0311.smartlockdoor.helper.AlertDialogHelper.hideLoading
import vn.edu.hust.ttkien0311.smartlockdoor.helper.AlertDialogHelper.showLoading
import vn.edu.hust.ttkien0311.smartlockdoor.helper.ExceptionHelper.handleException
import vn.edu.hust.ttkien0311.smartlockdoor.helper.Helper.comparePassword
import vn.edu.hust.ttkien0311.smartlockdoor.helper.Helper.validatePassword
import vn.edu.hust.ttkien0311.smartlockdoor.network.PasswordChange
import vn.edu.hust.ttkien0311.smartlockdoor.network.ServerApi

class ChangePasswordFragment : Fragment() {
    private lateinit var binding: FragmentChangePasswordBinding
    private var currentPasswordChecked = false
    private var newPassWordChecked = false
    private var compareNewPasswordChecked = false
    private lateinit var currentPasswordInput: TextInputEditText
    private lateinit var newPasswordInput: TextInputEditText
    private lateinit var confirmPasswordInput: TextInputEditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_change_password, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        currentPasswordInput = binding.currentPassword
        val currentPasswordLayout = binding.currentPasswordL
        newPasswordInput = binding.newPassword
        val newPasswordLayout = binding.newPasswordL
        confirmPasswordInput = binding.confirmPassword
        val confirmPasswordLayout = binding.confirmPasswordL
        val saveBtn = binding.saveButton


        currentPasswordInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                currentPasswordChecked =
                    validatePassword(currentPasswordInput, currentPasswordLayout)
                validateData(saveBtn)
            }

        })

        newPasswordInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (newPasswordInput.text.toString().trim()
                        .equals(currentPasswordInput.text.toString().trim())
                ) {
                    newPassWordChecked = false
                    newPasswordLayout.helperText = "Mật khẩu mới không được giống mật khẩu cũ."
                } else {
                    newPassWordChecked = validatePassword(newPasswordInput, newPasswordLayout)
                }
                validateData(saveBtn)
            }
        })

        confirmPasswordInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                compareNewPasswordChecked = comparePassword(
                    newPasswordInput.text.toString().trim(),
                    confirmPasswordInput.text.toString().trim(),
                    confirmPasswordLayout
                )
                validateData(saveBtn)
            }
        })
    }

    private fun validateData(btn: Button) {
        if (currentPasswordChecked && newPassWordChecked && compareNewPasswordChecked) {
            btn.isClickable = true
            btn.setBackgroundColor(resources.getColor(R.color.my_light_primary, null))
            btn.setOnClickListener {
                showLoading(requireActivity())
                lifecycleScope.launch {
                    try {
                        val passwordChange = PasswordChange(
                            currentPasswordInput.text.toString().trim(),
                            newPasswordInput.text.toString().trim(),
                            confirmPasswordInput.text.toString().trim()
                        )
                        val res =
                            ServerApi(requireActivity()).retrofitService.changePassword(
                                passwordChange
                            )
                        if (res == 1) {
                            hideLoading()
                            AlertDialog.Builder(requireActivity())
                                .setMessage("Thành công!")
                                .setPositiveButton("Ok") { dialog, _ ->
                                    dialog.dismiss()
                                    findNavController().popBackStack()
                                }
                                .create()
                                .show()
                        }
                    } catch (ex: Exception) {
                        hideLoading()
                        handleException(ex, requireActivity())
                    }
                }
            }
        } else {
            btn.isClickable = false
            btn.setBackgroundColor(resources.getColor(R.color.my_light_primary_disable, null))
            btn.setOnClickListener(null)
        }
    }
}