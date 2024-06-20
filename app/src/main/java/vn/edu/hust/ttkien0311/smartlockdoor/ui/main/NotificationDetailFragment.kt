package vn.edu.hust.ttkien0311.smartlockdoor.ui.main

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import vn.edu.hust.ttkien0311.smartlockdoor.R
import vn.edu.hust.ttkien0311.smartlockdoor.databinding.FragmentNotificationDetailBinding
import vn.edu.hust.ttkien0311.smartlockdoor.helper.AlertDialogHelper.hideLoading
import vn.edu.hust.ttkien0311.smartlockdoor.helper.AlertDialogHelper.showLoading
import vn.edu.hust.ttkien0311.smartlockdoor.helper.ExceptionHelper.handleException
import vn.edu.hust.ttkien0311.smartlockdoor.helper.Helper.formatDateTime
import vn.edu.hust.ttkien0311.smartlockdoor.network.ServerApi
import vn.edu.hust.ttkien0311.smartlockdoor.ui.welcome.WelcomeActivity

class NotificationDetailFragment : Fragment() {
    private lateinit var binding: FragmentNotificationDetailBinding
    private var notifId: String? = null
    private var doorState: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            val intent = Intent(activity, WelcomeActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        notifId = arguments?.getString("NotifId")
        doorState = arguments?.getString("DoorState")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_notification_detail,
            container,
            false
        )

        binding.toolbar.backButton.setOnClickListener {
            val intent = Intent(activity, WelcomeActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        binding.toolbar.title.text = "Từ thông báo"

        if (notifId != null) {
            lifecycleScope.launch {
                try {
                    showLoading(requireActivity())
                    val res = ServerApi(requireActivity()).retrofitService.getByNotifId(notifId!!)
                    hideLoading()

                    if (res != null) {
                        res.createdDate = formatDateTime(res.createdDate, "HH:mm - dd/MM/yyyy")
                        binding.image = res
                        Log.d("SLD", "$res")
                    }
                } catch (ex: Exception) {
                    hideLoading()
                    handleException(ex, requireActivity())
                }
            }
        }

        if (doorState == "CLOSE") {
            binding.doorStateIcon.setImageResource(R.drawable.ic_door_closed)
            binding.doorStateName.text = "Đóng"
            binding.doorStateName.setTextColor(Color.parseColor("#F65555"))
        } else {
            binding.doorStateIcon.setImageResource(R.drawable.ic_door_open)
            binding.doorStateName.text = "Mở"
            binding.doorStateName.setTextColor(Color.parseColor("#29DF00"))
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.cardView.setOnClickListener {
            if (doorState == "CLOSE") {
                doorState = "OPEN"
                binding.doorStateIcon.setImageResource(R.drawable.ic_door_open)
                binding.doorStateName.text = "Mở"
                binding.doorStateName.setTextColor(Color.parseColor("#29DF00"))
            } else {
                doorState = "CLOSE"
                binding.doorStateIcon.setImageResource(R.drawable.ic_door_closed)
                binding.doorStateName.text = "Đóng"
                binding.doorStateName.setTextColor(Color.parseColor("#F65555"))
            }
        }
    }
}