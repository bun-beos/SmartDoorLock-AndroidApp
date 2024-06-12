package vn.edu.hust.ttkien0311.smartlockdoor.ui.main.profile

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.graphics.drawable.toBitmap
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import kotlinx.coroutines.launch
import vn.edu.hust.ttkien0311.smartlockdoor.R
import vn.edu.hust.ttkien0311.smartlockdoor.ui.welcome.WelcomeActivity
import vn.edu.hust.ttkien0311.smartlockdoor.databinding.FragmentProfileBinding
import vn.edu.hust.ttkien0311.smartlockdoor.helper.AlertDialogHelper.hideLoading
import vn.edu.hust.ttkien0311.smartlockdoor.helper.AlertDialogHelper.showLoading
import vn.edu.hust.ttkien0311.smartlockdoor.helper.EncryptedSharedPreferencesManager
import vn.edu.hust.ttkien0311.smartlockdoor.helper.ExceptionHelper.handleException
import vn.edu.hust.ttkien0311.smartlockdoor.helper.Helper.bitmapToBase64
import vn.edu.hust.ttkien0311.smartlockdoor.network.ServerApi

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private val viewModel: ProfileViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            requireActivity().finish()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        if (viewModel.account.value == null) {
            getAccount()
        }

        binding.swipeRefreshLayout.setColorSchemeColors(
            resources.getColor(
                R.color.link_color,
                null
            )
        )
        binding.swipeRefreshLayout.setOnRefreshListener {
            getAccount()
            hideLoading()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (viewModel.account.value == null) {
            binding.logOut.visibility = View.INVISIBLE
        } else {
            binding.logOut.visibility = View.VISIBLE
        }

        binding.userImage.setOnClickListener {
            ImagePicker.with(this)
                .crop()                                    //Crop image(Optional), Check Customization for more option
                .compress(1024)                    //Final image size will be less than 1 MB(Optional)
                .maxResultSize(
                    1080,
                    1080
                )    //Final image resolution will be less than 1080 x 1080(Optional)
                .start()
        }

        binding.changeUsername.setOnClickListener {
            val action =
                ProfileFragmentDirections.actionProfileFragmentToChangeUsernameFragment(binding.usename.text.toString())
            view.findNavController().navigate(action)
        }

        binding.changePassword.setOnClickListener {
            view.findNavController().navigate(R.id.action_profileFragment_to_changePasswordFragment)
        }

        binding.checkDoor.setOnClickListener {
            view.findNavController().navigate(R.id.action_profileFragment_to_doorFragment)
        }

        binding.logOut.setOnClickListener {
            val alertDialog =
                AlertDialog.Builder(requireActivity()).setMessage("Bạn chắc chắn muốn đăng xuất?")
                    .setPositiveButton(resources.getString(R.string.log_out)) { dialog, _ ->
                        dialog.dismiss()
                        val sharedPreferencesManager =
                            EncryptedSharedPreferencesManager(requireActivity())
                        lifecycleScope.launch {
                            try {
                                showLoading(requireActivity())
                                ServerApi(requireActivity()).retrofitService.logOut(
                                    sharedPreferencesManager.getRefreshToken()
                                )
                            } catch (ex: Exception) {
                                Log.d("SLD", "$ex")
                            }
                            hideLoading()
                            logOut(sharedPreferencesManager)
                        }
                    }.setNegativeButton(resources.getString(R.string.cancel)) { dialog, _ ->
                        dialog.cancel()
                    }.create()
            alertDialog.show()

            val positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            positiveButton.setTextColor(resources.getColor(R.color.error, null))

            val negativeButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            negativeButton.setTextColor(resources.getColor(R.color.hint_text, null))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                val uri = data?.data!!
                binding.userImage.setImageURI(uri)

                val imgBitmap = binding.userImage.drawable.toBitmap()
                val imgBase64 = bitmapToBase64(imgBitmap)

                lifecycleScope.launch {
                    try {
                        showLoading(requireActivity())
                        val res =
                            ServerApi(requireActivity()).retrofitService.changeUserImage(imgBase64)
                        if (res == 1) {
                            hideLoading()
                            AlertDialog.Builder(requireActivity())
                                .setMessage("Thành công!")
                                .setPositiveButton("Ok") { dialog, _ ->
                                    dialog.dismiss()
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

            ImagePicker.RESULT_ERROR -> {
                Toast.makeText(requireActivity(), ImagePicker.getError(data), Toast.LENGTH_SHORT)
                    .show()
            }

            else -> {
                Toast.makeText(requireActivity(), "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun logOut(sharedPreferencesManager: EncryptedSharedPreferencesManager) {
        Log.d("SLD", "Log out from profile")
        sharedPreferencesManager.saveAccessToken("")
        sharedPreferencesManager.saveRefreshToken("")
        sharedPreferencesManager.saveRefreshTokenExpires("")
        sharedPreferencesManager.saveAccountId("")
        sharedPreferencesManager.saveSelectedDevice("")
        sharedPreferencesManager.saveLoginStatus(false)

        val intent = Intent(requireActivity(), WelcomeActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun getAccount() {
        lifecycleScope.launch {
            try {
                showLoading(requireActivity())
                val response = ServerApi(requireActivity()).retrofitService.getAccountInfo()
                hideLoading()

                viewModel.setAccount(response)
                binding.logOut.visibility = View.VISIBLE
            } catch (ex: Exception) {
                hideLoading()
                handleException(ex, requireActivity())
                binding.logOut.visibility = View.GONE
            }
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }
}