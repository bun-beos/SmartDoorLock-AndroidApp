package vn.edu.hust.ttkien0311.smartlockdoor.ui.main.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import vn.edu.hust.ttkien0311.smartlockdoor.R
import vn.edu.hust.ttkien0311.smartlockdoor.databinding.FragmentHomeBinding
import vn.edu.hust.ttkien0311.smartlockdoor.helper.AlertDialogHelper.hideLoading
import vn.edu.hust.ttkien0311.smartlockdoor.helper.AlertDialogHelper.showLoading
import vn.edu.hust.ttkien0311.smartlockdoor.helper.EncryptedSharedPreferencesManager
import vn.edu.hust.ttkien0311.smartlockdoor.helper.ExceptionHelper.handleException
import vn.edu.hust.ttkien0311.smartlockdoor.network.MonthLabel
import vn.edu.hust.ttkien0311.smartlockdoor.network.ServerApi
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by activityViewModels()

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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        binding.lifecycleOwner = this

        val sharedPreferencesManager = EncryptedSharedPreferencesManager(requireContext())
        val myAccountId = sharedPreferencesManager.getAccountId()
        val currentDeviceId = sharedPreferencesManager.getSelectedDevice()

        if (viewModel.devices.value == null) {
            getMyListDevice(myAccountId, currentDeviceId)
        } else {
            binding.listDevice.adapter =
                MyDeviceListAdapter(requireActivity(), viewModel.devices.value!!)
            binding.listDevice.visibility = View.VISIBLE
            binding.emptyDevice.visibility = View.GONE
            if (currentDeviceId.isNotEmpty()) {
                if (viewModel.dates.value == null) {
                    getListLabel(currentDeviceId)
                } else {
                    binding.listMonth.adapter = MonthListAdapter(
                        requireActivity(),
                        currentDeviceId,
                        viewModel.dates.value!!,
                        HistoryRowListener { image ->
                            viewModel.onHistoryRowClicked(image)
                            findNavController().navigate(R.id.action_homeFragment_to_historyDetailFragment)
                        })
                    binding.listMonth.visibility = View.VISIBLE
                    binding.emptyContent.visibility = View.GONE
                }
            }
        }

        binding.swipeRefreshLayout.setColorSchemeColors(
            resources.getColor(
                R.color.link_color,
                null
            )
        )

        binding.swipeRefreshLayout.setOnRefreshListener {
            getMyListDevice(myAccountId, currentDeviceId)
            hideLoading()
        }


        return binding.root
    }

    private fun getMyListDevice(accountId: String, deviceId: String) {
        lifecycleScope.launch {
            try {
                showLoading(requireActivity())
                val listDevice = ServerApi(requireActivity()).retrofitService.getDevice(accountId)
                if (deviceId.isNotEmpty()) {
                    getListLabel(deviceId)
                }
                hideLoading()
                if (listDevice.isNotEmpty()) {
                    viewModel.setMyListDevice(listDevice)
                    binding.listDevice.adapter =
                        MyDeviceListAdapter(requireActivity(), viewModel.devices.value!!)

                    binding.listDevice.visibility = View.VISIBLE
                    binding.emptyDevice.visibility = View.GONE
                } else {
                    binding.listDevice.visibility = View.GONE
                    binding.emptyDevice.visibility = View.VISIBLE
                }
            } catch (ex: Exception) {
                hideLoading()
                handleException(ex, requireActivity())

                binding.listDevice.visibility = View.GONE
                binding.emptyDevice.visibility = View.VISIBLE
            }
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun getListLabel(deviceId: String) {
        lifecycleScope.launch {
            try {
//                showLoading(requireActivity())
                val res =
                    ServerApi(requireActivity()).retrofitService.getOldestTime(deviceId)
//                hideLoading()

                viewModel.setListDate(createListMonth(res))
                binding.listMonth.adapter = MonthListAdapter(
                    requireActivity(),
                    deviceId,
                    viewModel.dates.value!!,
                    HistoryRowListener { image ->
                        viewModel.onHistoryRowClicked(image)
                        findNavController().navigate(R.id.action_homeFragment_to_historyDetailFragment)
                    })
                binding.listMonth.visibility = View.VISIBLE
                binding.emptyContent.visibility = View.GONE
            } catch (ex: Exception) {
//                hideLoading()
                handleException(ex, requireActivity())

                binding.listMonth.visibility = View.GONE
                binding.emptyContent.visibility = View.VISIBLE
            }
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun createListMonth(oldestTime: String): List<MonthLabel> {
        val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
        var oldestDate = OffsetDateTime.parse(oldestTime, formatter).toLocalDate()
        oldestDate = LocalDate.of(oldestDate.year, oldestDate.monthValue, 1)
        var currentDate = LocalDate.of(LocalDate.now().year, LocalDate.now().monthValue, 1)

        val listMonthLabel = mutableListOf<MonthLabel>()

        while (oldestDate.isBefore(currentDate)) {
            listMonthLabel.add(MonthLabel(currentDate))
            currentDate = currentDate.plusMonths(-1)
        }
        listMonthLabel.add(MonthLabel(oldestDate))

        return listMonthLabel
    }
}