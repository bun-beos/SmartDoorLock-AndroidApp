package vn.edu.hust.ttkien0311.smartlockdoor.ui.main.profile

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.launch
import vn.edu.hust.ttkien0311.smartlockdoor.R
import vn.edu.hust.ttkien0311.smartlockdoor.databinding.FragmentChangeUsernameBinding
import vn.edu.hust.ttkien0311.smartlockdoor.helper.AlertDialogHelper.hideLoading
import vn.edu.hust.ttkien0311.smartlockdoor.helper.AlertDialogHelper.showLoading
import vn.edu.hust.ttkien0311.smartlockdoor.helper.ExceptionHelper.handleException
import vn.edu.hust.ttkien0311.smartlockdoor.helper.Helper.validateUsername
import vn.edu.hust.ttkien0311.smartlockdoor.network.ServerApi

class ChangeUsernameFragment : Fragment() {
    private lateinit var binding: FragmentChangeUsernameBinding
    private val args: ChangeUsernameFragmentArgs by navArgs()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_change_username, container, false)

        binding.username.setText(args.username)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        val usernameInput = binding.username
        val usernameLayout = binding.usernameL
        val saveBtn = binding.saveButton

        usernameInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (usernameInput.text.isNullOrEmpty() || usernameInput.text.toString()
                        .trim() == args.username
                ) {
                    saveBtn.isClickable = false
                    saveBtn.setBackgroundColor(
                        resources.getColor(
                            R.color.my_light_primary_disable,
                            null
                        )
                    )
                    usernameLayout.helperText = " "
                    saveBtn.setOnClickListener(null)
                } else {
                    saveBtn.isClickable = true
                    saveBtn.setBackgroundColor(
                        resources.getColor(
                            R.color.my_light_primary,
                            null
                        )
                    )
                    saveBtn.setOnClickListener {
                        if (validateUsername(usernameInput, usernameLayout)) {
                            lifecycleScope.launch {
                                showLoading(requireActivity())
                                try {
                                    val res =
                                        ServerApi(requireActivity()).retrofitService.changeUsername(
                                            usernameInput.text.toString().trim()
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
                    }
                }
            }
        })


    }
}