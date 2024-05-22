package vn.edu.hust.ttkien0311.smartlockdoor.ui.main.member

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.launch
import vn.edu.hust.ttkien0311.smartlockdoor.R
import vn.edu.hust.ttkien0311.smartlockdoor.databinding.FragmentMemberHistoryBinding
import vn.edu.hust.ttkien0311.smartlockdoor.helper.AlertDialogHelper
import vn.edu.hust.ttkien0311.smartlockdoor.helper.AlertDialogHelper.hideLoading
import vn.edu.hust.ttkien0311.smartlockdoor.helper.AlertDialogHelper.showLoading
import vn.edu.hust.ttkien0311.smartlockdoor.helper.ExceptionHelper
import vn.edu.hust.ttkien0311.smartlockdoor.helper.Helper
import vn.edu.hust.ttkien0311.smartlockdoor.helper.Helper.formatDateTime
import vn.edu.hust.ttkien0311.smartlockdoor.network.ServerApi
import vn.edu.hust.ttkien0311.smartlockdoor.ui.main.home.HistoryListAdapter
import vn.edu.hust.ttkien0311.smartlockdoor.ui.main.home.HistoryRowListener
import vn.edu.hust.ttkien0311.smartlockdoor.ui.main.home.HistoryViewModel

class MemberHistoryFragment : Fragment() {
    private lateinit var binding: FragmentMemberHistoryBinding
    private val args: MemberHistoryFragmentArgs by navArgs()
    private val viewModel: HistoryViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_member_history, container, false)
        binding.toolbar.title.text = "Lịch sử"
        val memberId = args.memberId

        lifecycleScope.launch {
            showLoading(requireActivity())
            try {
                val res = ServerApi(requireActivity()).retrofitService.filterImage(
                    memberId,
                    null,
                    null
                )
                hideLoading()
                for (image in res) {
                    image.createdDate = formatDateTime(image.createdDate, "dd/MM/yyyy HH:mm:ss")
                }
                binding.listMemberHistory.adapter = HistoryListAdapter(res, HistoryRowListener { image ->
                    viewModel.onHistoryRowClicked(image)
                    findNavController().navigate(R.id.action_memberHistoryFragment_to_historyDetailFragment)
                })
            } catch (ex: Exception) {
                hideLoading()
                ExceptionHelper.handleException(ex, requireActivity())
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}