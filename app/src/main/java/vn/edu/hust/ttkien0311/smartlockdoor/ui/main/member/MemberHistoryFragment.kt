package vn.edu.hust.ttkien0311.smartlockdoor.ui.main.member

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import vn.edu.hust.ttkien0311.smartlockdoor.R
import vn.edu.hust.ttkien0311.smartlockdoor.databinding.FragmentMemberHistoryBinding
import vn.edu.hust.ttkien0311.smartlockdoor.helper.AlertDialogHelper.hideLoading
import vn.edu.hust.ttkien0311.smartlockdoor.helper.AlertDialogHelper.showLoading
import vn.edu.hust.ttkien0311.smartlockdoor.helper.ExceptionHelper
import vn.edu.hust.ttkien0311.smartlockdoor.helper.Helper.formatDateTime
import vn.edu.hust.ttkien0311.smartlockdoor.network.ServerApi
import vn.edu.hust.ttkien0311.smartlockdoor.ui.main.home.HistoryListAdapter
import vn.edu.hust.ttkien0311.smartlockdoor.ui.main.home.HistoryMode
import vn.edu.hust.ttkien0311.smartlockdoor.ui.main.home.HistoryRowListener
import vn.edu.hust.ttkien0311.smartlockdoor.ui.main.home.HomeViewModel

class MemberHistoryFragment : Fragment() {
    private lateinit var binding: FragmentMemberHistoryBinding
    private val args: MemberHistoryFragmentArgs by navArgs()
    private val viewModel: HomeViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_member_history, container, false)
        binding.toolbar.title.text = resources.getString(R.string.history)

        lifecycleScope.launch {
            showLoading(requireActivity())
            try {
                val res = ServerApi(requireActivity()).retrofitService.filterImage(
                    args.deviceId,
                    args.memberId,
                    null,
                    null
                )
                hideLoading()
                if (res.isNotEmpty()) {
                    binding.emptyContent.visibility = View.GONE
                    for (image in res) {
                        image.createdDate = formatDateTime(image.createdDate, "HH:mm - dd/MM/yyyy")
                    }
                    binding.listMemberHistory.adapter =
                        HistoryListAdapter(res, HistoryMode.LIST.toString(), HistoryRowListener { image ->
                            viewModel.onHistoryRowClicked(image)
                            findNavController().navigate(R.id.action_memberHistoryFragment_to_historyDetailFragment)
                        })
                } else {
                    binding.emptyContent.visibility = View.VISIBLE
                }
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