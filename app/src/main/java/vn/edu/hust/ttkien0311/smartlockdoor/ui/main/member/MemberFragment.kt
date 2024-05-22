package vn.edu.hust.ttkien0311.smartlockdoor.ui.main.member

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import vn.edu.hust.ttkien0311.smartlockdoor.R
import vn.edu.hust.ttkien0311.smartlockdoor.databinding.FragmentMemberBinding
import vn.edu.hust.ttkien0311.smartlockdoor.helper.ExceptionHelper.handleException
import vn.edu.hust.ttkien0311.smartlockdoor.helper.Helper
import vn.edu.hust.ttkien0311.smartlockdoor.helper.Helper.formatDateTime
import vn.edu.hust.ttkien0311.smartlockdoor.network.Member
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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =  DataBindingUtil.inflate(inflater, R.layout.fragment_member, container, false)

        var listMember: List<Member>
        lifecycleScope.launch {
            try {
                val res = ServerApi(requireActivity()).retrofitService.getAllMember()
                for (member in res) {
                    member.createdDate = formatDateTime(member.createdDate, "dd/MM/yyyy  HH:mm:ss")
                    member.modifiedDate = formatDateTime(member.modifiedDate, "dd/MM/yyyy  HH:mm:ss")
                    member.dateOfBirth = formatDateTime(member.dateOfBirth, "dd/MM/yyyy")
                }
                listMember = res
            } catch (ex: Exception) {
                listMember = emptyList()
                handleException(ex, requireActivity())
            }
            binding.listMember.adapter = MemberAdapter(listMember, MemberItemListener { member ->
                viewModel.onMemberRowClicked(member)
                findNavController().navigate(R.id.action_memberFragment_to_memberDetailFragment)
            })
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.magnifyIcon.setOnClickListener {
            Toast.makeText(requireActivity(), "Coming soon", Toast.LENGTH_SHORT).show()
        }

        binding.addCircleIcon.setOnClickListener {
            viewModel.onMemberRowClicked(null)
            val action = MemberFragmentDirections.actionMemberFragmentToMemberEditFragment(State.ADD.toString())
            findNavController().navigate(action)
        }
    }
}