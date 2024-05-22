package vn.edu.hust.ttkien0311.smartlockdoor.ui.main.member

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
import androidx.core.graphics.drawable.toBitmap
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import kotlinx.coroutines.launch
import vn.edu.hust.ttkien0311.smartlockdoor.R
import vn.edu.hust.ttkien0311.smartlockdoor.databinding.FragmentMemberDetailBinding
import vn.edu.hust.ttkien0311.smartlockdoor.helper.AlertDialogHelper
import vn.edu.hust.ttkien0311.smartlockdoor.helper.AlertDialogHelper.hideLoading
import vn.edu.hust.ttkien0311.smartlockdoor.helper.AlertDialogHelper.showLoading
import vn.edu.hust.ttkien0311.smartlockdoor.helper.ExceptionHelper
import vn.edu.hust.ttkien0311.smartlockdoor.helper.ExceptionHelper.handleException
import vn.edu.hust.ttkien0311.smartlockdoor.helper.Helper
import vn.edu.hust.ttkien0311.smartlockdoor.network.MemberDto
import vn.edu.hust.ttkien0311.smartlockdoor.network.ServerApi

class MemberDetailFragment : Fragment() {
    private lateinit var binding: FragmentMemberDetailBinding
    private val viewModel: MemberViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_member_detail, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.toolbar.title.text = "Chi tiết"
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.editInfoButton.setOnClickListener {
            val action = MemberDetailFragmentDirections.actionMemberDetailFragmentToMemberEditFragment(State.EDIT.toString())
            findNavController().navigate(action)
        }

        binding.memberHistoryDetail.setOnClickListener {
            val action = MemberDetailFragmentDirections.actionMemberDetailFragmentToMemberHistoryFragment(viewModel.member.value!!.memberId)
            findNavController().navigate(action)
        }

        binding.deleteButton.setOnClickListener {
            val alertDialog =
                AlertDialog.Builder(requireActivity())
                    .setMessage("Xóa thành viên đồng nghĩa sẽ xóa toàn bộ lịch sử liên quan.\nBạn có chắc chắn muốn xóa thành viên không?")
                    .setPositiveButton(resources.getString(R.string.delete)) { dialog, _ ->
                        dialog.dismiss()
                        lifecycleScope.launch {
                            try {
                                showLoading(requireActivity())
                                ServerApi(requireActivity()).retrofitService.deleteMember(viewModel.member.value!!.memberId)
                            } catch (ex: Exception) {
                                Log.d("SLD", "$ex")
                            }
                            hideLoading()
                            findNavController().popBackStack()
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
}