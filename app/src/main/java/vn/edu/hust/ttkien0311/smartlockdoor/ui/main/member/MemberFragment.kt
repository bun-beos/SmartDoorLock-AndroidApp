package vn.edu.hust.ttkien0311.smartlockdoor.ui.main.member

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import vn.edu.hust.ttkien0311.smartlockdoor.R
import vn.edu.hust.ttkien0311.smartlockdoor.databinding.FragmentMemberBinding
import vn.edu.hust.ttkien0311.smartlockdoor.helper.AlertDialogHelper.hideLoading
import vn.edu.hust.ttkien0311.smartlockdoor.helper.AlertDialogHelper.showLoading
import vn.edu.hust.ttkien0311.smartlockdoor.helper.EncryptedSharedPreferencesManager
import vn.edu.hust.ttkien0311.smartlockdoor.helper.ExceptionHelper.handleException
import vn.edu.hust.ttkien0311.smartlockdoor.helper.Helper.formatDateTime
import vn.edu.hust.ttkien0311.smartlockdoor.network.ServerApi

class MemberFragment : Fragment() {
    private lateinit var binding: FragmentMemberBinding
    private val viewModel: MemberViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            requireActivity().finish()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_member, container, false)
        binding.lifecycleOwner = this
        val sharedPreferencesManager = EncryptedSharedPreferencesManager(requireActivity())
        val currentDeviceId = sharedPreferencesManager.getSelectedDevice()

        if (currentDeviceId.isNotEmpty()) {
            if (viewModel.members.value == null) {
                getListMember(currentDeviceId)
            } else {
                binding.listMember.adapter =
                    MemberAdapter(viewModel.members.value!!, MemberItemListener { member ->
                        viewModel.onMemberRowClicked(member)
                        val action = MemberFragmentDirections.actionMemberFragmentToMemberDetailFragment(currentDeviceId)
                        findNavController().navigate(action)
                    })
                binding.listMember.visibility = View.VISIBLE
                binding.emptyContent.visibility = View.GONE
            }
        }

        binding.magnifyIcon.setOnClickListener {
            Toast.makeText(requireActivity(), "Coming soon", Toast.LENGTH_SHORT).show()
        }

        binding.addCircleIcon.setOnClickListener {
            viewModel.onMemberRowClicked(null)
            val action =
                MemberFragmentDirections.actionMemberFragmentToMemberEditFragment(
                    State.ADD.toString(),
                    currentDeviceId
                )
            findNavController().navigate(action)
        }

        binding.swipeRefreshLayout.setColorSchemeColors(
            resources.getColor(
                R.color.link_color,
                null
            )
        )
        binding.swipeRefreshLayout.setOnRefreshListener {
            getListMember(currentDeviceId)
            hideLoading()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    private fun getListMember(deviceId:String) {
        lifecycleScope.launch {
            try {
                showLoading(requireActivity())
                val res = ServerApi(requireActivity()).retrofitService.getAllByDeviceMember(
                    deviceId
                )
                hideLoading()

                if (res.isNotEmpty()) {
                    for (member in res) {
                        member.createdDate =
                            formatDateTime(member.createdDate, "HH:mm - dd/MM/yyyy")
                        member.dateOfBirth = formatDateTime(member.dateOfBirth, "dd/MM/yyyy")
                    }
                    viewModel.setListMember(res)

                    binding.listMember.adapter =
                        MemberAdapter(viewModel.members.value!!, MemberItemListener { member ->
                            viewModel.onMemberRowClicked(member)
                            val action = MemberFragmentDirections.actionMemberFragmentToMemberDetailFragment(deviceId)
                            findNavController().navigate(action)
                        })
                    binding.listMember.visibility = View.VISIBLE
                    binding.emptyContent.visibility = View.GONE
                }
            } catch (ex: Exception) {
                hideLoading()
                handleException(ex, requireActivity())

                binding.listMember.visibility = View.GONE
                binding.emptyContent.visibility = View.VISIBLE
            }
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }
}