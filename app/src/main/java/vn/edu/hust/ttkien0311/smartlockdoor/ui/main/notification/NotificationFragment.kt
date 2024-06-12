package vn.edu.hust.ttkien0311.smartlockdoor.ui.main.notification

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import vn.edu.hust.ttkien0311.smartlockdoor.R
import vn.edu.hust.ttkien0311.smartlockdoor.databinding.FragmentNotificationBinding
import vn.edu.hust.ttkien0311.smartlockdoor.helper.AlertDialogHelper.hideLoading
import vn.edu.hust.ttkien0311.smartlockdoor.helper.AlertDialogHelper.showLoading
import vn.edu.hust.ttkien0311.smartlockdoor.helper.EncryptedSharedPreferencesManager
import vn.edu.hust.ttkien0311.smartlockdoor.helper.ExceptionHelper.handleException
import vn.edu.hust.ttkien0311.smartlockdoor.helper.Helper.formatDateTime
import vn.edu.hust.ttkien0311.smartlockdoor.network.ServerApi

class NotificationFragment : Fragment() {
    private lateinit var binding: FragmentNotificationBinding
    private val viewModel: NotificationViewModel by activityViewModels()
    private var myAccountId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            requireActivity().finish()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_notification, container, false)
        binding.lifecycleOwner = this

        val sharedPreferencesManager = EncryptedSharedPreferencesManager(requireActivity())
        myAccountId = sharedPreferencesManager.getAccountId()

        if (viewModel.notification.value == null) {
            getListNotification()
        } else {
            binding.listNotification.adapter =
                NotificationListAdapter(requireActivity(), viewModel.notification.value!!)
            binding.listNotification.visibility = View.VISIBLE
            binding.emptyContent.visibility = View.GONE
        }

        binding.swipeRefreshLayout.setColorSchemeColors(
            resources.getColor(
                R.color.link_color,
                null
            )
        )
        binding.swipeRefreshLayout.setOnRefreshListener {
            getListNotification()
            hideLoading()
        }

        return binding.root
    }

    private fun getListNotification() {
        lifecycleScope.launch {
            try {
                showLoading(requireActivity())
                val res =
                    ServerApi(requireActivity()).retrofitService.FilterNotification(myAccountId)
                hideLoading()

                if (res.isNotEmpty()) {
                    for (item in res) {
                        item.createdDate += "+07:00"
                        item.createdDate =
                            formatDateTime(item.createdDate, "HH:mm - dd/MM/yyyy")
                    }
                    viewModel.setListNotification(res)

                    binding.listNotification.adapter =
                        NotificationListAdapter(requireActivity(), viewModel.notification.value!!)

                    binding.listNotification.visibility = View.VISIBLE
                    binding.emptyContent.visibility = View.GONE
                } else {
                    binding.listNotification.visibility = View.GONE
                    binding.emptyContent.visibility = View.VISIBLE
                }
            } catch (ex: Exception) {
                hideLoading()
                handleException(ex, requireActivity())

                binding.listNotification.visibility = View.GONE
                binding.emptyContent.visibility = View.VISIBLE

            }
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }
}