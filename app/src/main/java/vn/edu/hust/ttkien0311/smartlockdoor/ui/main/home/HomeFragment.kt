package vn.edu.hust.ttkien0311.smartlockdoor.ui.main.home

import android.content.Context
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
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import vn.edu.hust.ttkien0311.smartlockdoor.R
import vn.edu.hust.ttkien0311.smartlockdoor.databinding.FragmentHomeBinding
import vn.edu.hust.ttkien0311.smartlockdoor.helper.AlertDialogHelper.hideLoading
import vn.edu.hust.ttkien0311.smartlockdoor.helper.AlertDialogHelper.showLoading
import vn.edu.hust.ttkien0311.smartlockdoor.helper.EncryptedSharedPreferencesManager
import vn.edu.hust.ttkien0311.smartlockdoor.helper.ExceptionHelper.handleException
import vn.edu.hust.ttkien0311.smartlockdoor.helper.Helper
import vn.edu.hust.ttkien0311.smartlockdoor.network.Device
import vn.edu.hust.ttkien0311.smartlockdoor.network.MessageType
import vn.edu.hust.ttkien0311.smartlockdoor.network.MonthLabel
import vn.edu.hust.ttkien0311.smartlockdoor.network.MqttPublish
import vn.edu.hust.ttkien0311.smartlockdoor.network.ServerApi
import vn.edu.hust.ttkien0311.smartlockdoor.ui.main.member.MemberViewModel
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

enum class HistoryMode { LIST, GRID }

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by activityViewModels()
    private val memberViewModel: MemberViewModel by activityViewModels()
    private var cardMenuState: Boolean = false

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
        binding.viewModel = viewModel

        val sharedPreferencesManager = EncryptedSharedPreferencesManager(requireContext())
        val myAccountId = sharedPreferencesManager.getAccountId()
        val currentDeviceId = sharedPreferencesManager.getSelectedDevice()
        val phoneToken = sharedPreferencesManager.getPhoneToken()

        if (viewModel.historyMode.value == null) {
            viewModel.setMode(HistoryMode.LIST.toString())
        }

//        binding.listDevice.adapter =
//            MyDeviceListAdapter(
//                requireActivity()
//            ) { device ->
//                val newList = mutableListOf<Device>()
//                for (item in viewModel.devices.value!!) {
//                    item.selected = (item.deviceId == device.deviceId)
//                    if (item.selected) {
//                        newList.add(0, item)
//                    } else {
//                        newList.add(item)
//                    }
//                }
//                viewModel.setMyListDevice(newList)
////                binding.listDevice.scrollToPosition(0)
//
//                getListLabel(device.deviceId)
//            }

        if (viewModel.devices.value.isNullOrEmpty()) {
            getMyListDevice(requireContext(), myAccountId, currentDeviceId, phoneToken)
        } else {
            binding.listDevice.adapter =
                MyDeviceListAdapter(requireActivity(), viewModel.devices.value!!) { id ->
                    getListLabel(id)
                    getListMember(id)
                    binding.listDevice.scrollToPosition(0)
                }

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
                        viewModel.historyMode.value!!,
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
            getMyListDevice(
                requireContext(),
                myAccountId,
                sharedPreferencesManager.getSelectedDevice(),
                phoneToken
            )
            hideLoading()
        }

        binding.addIcon.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_deviceFragment)
        }

        binding.dehazeIcon.setOnClickListener {
            cardMenuState = !cardMenuState
            if (cardMenuState) {
                binding.cardViewMenu.visibility = View.VISIBLE
                if (viewModel.historyMode.value == HistoryMode.GRID.toString()) {
                    binding.listView.visibility = View.VISIBLE
                    binding.gridView.visibility = View.GONE
                } else {
                    binding.listView.visibility = View.GONE
                    binding.gridView.visibility = View.VISIBLE
                }
            } else {
                binding.cardViewMenu.visibility = View.GONE
            }
        }

        binding.listView.setOnClickListener {
            cardMenuState = false
            viewModel.setMode(HistoryMode.LIST.toString())
            binding.listView.visibility = View.GONE
            binding.gridView.visibility = View.VISIBLE
            binding.cardViewMenu.visibility = View.GONE

            if (!viewModel.dates.value.isNullOrEmpty()) {
                binding.listMonth.adapter = MonthListAdapter(
                    requireActivity(),
                    sharedPreferencesManager.getSelectedDevice(),
                    viewModel.dates.value!!,
                    viewModel.historyMode.value!!,
                    HistoryRowListener { image ->
                        viewModel.onHistoryRowClicked(image)
                        findNavController().navigate(R.id.action_homeFragment_to_historyDetailFragment)
                    })
            }
        }

        binding.gridView.setOnClickListener {
            cardMenuState = false
            viewModel.setMode(HistoryMode.GRID.toString())
            binding.listView.visibility = View.VISIBLE
            binding.gridView.visibility = View.GONE
            binding.cardViewMenu.visibility = View.GONE

            if (!viewModel.dates.value.isNullOrEmpty()) {
                binding.listMonth.adapter = MonthListAdapter(
                    requireActivity(),
                    sharedPreferencesManager.getSelectedDevice(),
                    viewModel.dates.value!!,
                    viewModel.historyMode.value!!,
                    HistoryRowListener { image ->
                        viewModel.onHistoryRowClicked(image)
                        findNavController().navigate(R.id.action_homeFragment_to_historyDetailFragment)
                    })
            }
        }

        binding.search.setOnClickListener {
            Toast.makeText(requireContext(), "Coming soon", Toast.LENGTH_SHORT).show()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.listMonth.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    binding.swipeRefreshLayout.isEnabled = !recyclerView.canScrollVertically(-1)
                }
            }
        })
    }

    private fun getMyListDevice(
        context: Context,
        accountId: String,
        deviceId: String,
        phoneToken: String
    ) {
        lifecycleScope.launch {
            try {
                showLoading(context)
                val listDevice = ServerApi(context).retrofitService.getDeviceByAccount(accountId)
                if (deviceId.isNotEmpty()) {
                    getListLabel(deviceId)
                }
                hideLoading()

                val newList = mutableListOf<Device>()
                for (item in listDevice) {
                    if (item.deviceId == deviceId) {
                        item.selected = true
                        newList.add(0, item)
                    } else {
                        item.selected = false
                        newList.add(item)
                    }
                }
                viewModel.setMyListDevice(newList)

                binding.listDevice.adapter =
                    MyDeviceListAdapter(context, viewModel.devices.value!!) { id ->
                        getListLabel(id)
                        binding.listDevice.scrollToPosition(0)
                    }

                if (listDevice.isNotEmpty()) {
                    binding.listDevice.visibility = View.VISIBLE
                    binding.emptyDevice.visibility = View.GONE
                } else {
                    binding.listDevice.visibility = View.GONE
                    binding.emptyDevice.visibility = View.VISIBLE
                }
            } catch (ex: Exception) {
                hideLoading()
                handleException(ex, context)
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
                val listDate = createListMonth(res)
                if (listDate.isEmpty()) {
                    binding.listMonth.visibility = View.GONE
                    binding.emptyContent.visibility = View.VISIBLE
                } else {
                    viewModel.setListDate(listDate)
                    binding.listMonth.adapter = MonthListAdapter(
                        requireActivity(),
                        deviceId,
                        viewModel.dates.value!!,
                        viewModel.historyMode.value!!,
                        HistoryRowListener { image ->
                            viewModel.onHistoryRowClicked(image)
                            findNavController().navigate(R.id.action_homeFragment_to_historyDetailFragment)
                        })
                    binding.listMonth.visibility = View.VISIBLE
                    binding.emptyContent.visibility = View.GONE
                }
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

        while (oldestDate.isBefore(currentDate) || oldestDate.isEqual(currentDate)) {
            listMonthLabel.add(MonthLabel(currentDate))
            currentDate = currentDate.plusMonths(-1)
        }

        return listMonthLabel
    }

    private fun getListMember(deviceId: String) {
        lifecycleScope.launch {
            try {
                val res = ServerApi(requireContext()).retrofitService.getAllMemberByDevice(deviceId)
                for (member in res) {
                    member.createdDate =
                        Helper.formatDateTime(member.createdDate, "HH:mm - dd/MM/yyyy")
                    member.dateOfBirth = Helper.formatDateTime(member.dateOfBirth, "dd/MM/yyyy")
                }
                memberViewModel.setListMember(res)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }
}