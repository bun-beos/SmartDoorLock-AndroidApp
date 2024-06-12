package vn.edu.hust.ttkien0311.smartlockdoor.ui.main.home

import android.app.AlertDialog
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
import kotlinx.coroutines.launch
import vn.edu.hust.ttkien0311.smartlockdoor.R
import vn.edu.hust.ttkien0311.smartlockdoor.databinding.FragmentHistoryDetailBinding
import vn.edu.hust.ttkien0311.smartlockdoor.helper.AlertDialogHelper.hideLoading
import vn.edu.hust.ttkien0311.smartlockdoor.helper.AlertDialogHelper.showLoading
import vn.edu.hust.ttkien0311.smartlockdoor.network.ServerApi

class HistoryDetailFragment : Fragment() {
    private lateinit var binding: FragmentHistoryDetailBinding
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
            DataBindingUtil.inflate(inflater, R.layout.fragment_history_detail, container, false)
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

        binding.deleteButton.setOnClickListener {
            val alertDialog =
                AlertDialog.Builder(requireActivity())
                    .setMessage("Bạn có chắc chắn muốn xóa lịch sử này không?")
                    .setPositiveButton(resources.getString(R.string.delete)) { dialog, _ ->
                        dialog.dismiss()
                        lifecycleScope.launch {
                            try {
                                showLoading(requireActivity())
                                ServerApi(requireActivity()).retrofitService.deleteImage(viewModel.image.value!!.imageId)
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