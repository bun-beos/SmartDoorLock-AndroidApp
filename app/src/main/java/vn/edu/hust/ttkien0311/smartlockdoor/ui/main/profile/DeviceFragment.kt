package vn.edu.hust.ttkien0311.smartlockdoor.ui.main.profile

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch
import vn.edu.hust.ttkien0311.smartlockdoor.R
import vn.edu.hust.ttkien0311.smartlockdoor.databinding.DeviceItemBinding
import vn.edu.hust.ttkien0311.smartlockdoor.databinding.FragmentDeviceBinding
import vn.edu.hust.ttkien0311.smartlockdoor.helper.AlertDialogHelper.hideLoading
import vn.edu.hust.ttkien0311.smartlockdoor.helper.AlertDialogHelper.showLoading
import vn.edu.hust.ttkien0311.smartlockdoor.helper.EncryptedSharedPreferencesManager
import vn.edu.hust.ttkien0311.smartlockdoor.helper.ExceptionHelper.handleException
import vn.edu.hust.ttkien0311.smartlockdoor.network.Device
import vn.edu.hust.ttkien0311.smartlockdoor.network.MessageType
import vn.edu.hust.ttkien0311.smartlockdoor.network.MqttPublish
import vn.edu.hust.ttkien0311.smartlockdoor.network.ServerApi
import vn.edu.hust.ttkien0311.smartlockdoor.ui.main.home.HomeViewModel

class DeviceFragment : Fragment() {
    private lateinit var binding: FragmentDeviceBinding
    private val viewModel: ProfileViewModel by activityViewModels()
    private val homeViewModel: HomeViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_device, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.toolbar.title.text = "Thiết bị hiện có"

        binding.toolbar.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        val sharedPreferencesManager = EncryptedSharedPreferencesManager(requireActivity())
        val accountId = sharedPreferencesManager.getAccountId()
        val phoneToken = sharedPreferencesManager.getPhoneToken()

        binding.listDevice.adapter = DeviceListAdapter(requireContext(), accountId,
            { device ->
                updateDevice(requireActivity(), device, phoneToken)
            },
            { device ->
                onUpdateDeviceName(device)
            })

        if (viewModel.devices.value.isNullOrEmpty()) {
            getAllDevice(requireContext(), accountId)
        } else {
            binding.emptyContent.visibility = View.INVISIBLE
            binding.listDevice.visibility = View.VISIBLE
        }

        binding.swipeRefreshLayout.setColorSchemeColors(
            resources.getColor(
                R.color.link_color, null
            )
        )

        binding.swipeRefreshLayout.setOnRefreshListener {
            getAllDevice(requireContext(), accountId)
            hideLoading()
        }

        return binding.root
    }

    private fun getAllDevice(context: Context, id: String) {
        lifecycleScope.launch {
            try {
                showLoading(context)
                val res = ServerApi(context).retrofitService.getAllDevice(id)
                hideLoading()

                viewModel.setListDevice(res)

                if (res.isNotEmpty()) {
                    binding.emptyContent.visibility = View.INVISIBLE
                    binding.listDevice.visibility = View.VISIBLE
                } else {
                    binding.emptyContent.visibility = View.VISIBLE
                    binding.listDevice.visibility = View.GONE
                }
            } catch (ex: Exception) {
                hideLoading()
                handleException(ex, context)
            }
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun updateDevice(context: Context, device: Device, phoneToken:String) {
        lifecycleScope.launch {
            try {
//                showLoading(context)
                ServerApi(context).retrofitService.updateDevice(device)
                if (device.accountId.isNullOrEmpty()) {
                    ServerApi(context).retrofitService.publishSingle(
                        MqttPublish(
                            "SDL_${device.deviceId}",
                            "${MessageType.ACCOUNT_ID}:${MessageType.TOKEN}:"
                        )
                    )
                } else {
                    ServerApi(context).retrofitService.publishSingle(
                        MqttPublish(
                            "SDL_${device.deviceId}",
                            "${MessageType.ACCOUNT_ID}:${device.accountId}${MessageType.TOKEN}:$phoneToken"
                        )
                    )
                }
                var newList = mutableListOf<Device>()
                for (item in viewModel.devices.value!!) {
                    if (item.deviceId != device.deviceId) {
                        newList.add(item)
                    } else {
                        newList.add(device)
                    }
                }
                viewModel.setListDevice(newList)

                val currentDeviceId = EncryptedSharedPreferencesManager(context).getSelectedDevice()
                newList = mutableListOf()
                for (item in viewModel.devices.value!!) {
                    if (!item.accountId.isNullOrEmpty()) {
                        if (item.deviceId == currentDeviceId) {
                            newList.add(0, item)
                        } else {
                            newList.add(item)
                        }
                    }
                }
                homeViewModel.setMyListDevice(newList)
//                hideLoading()
            } catch (ex: Exception) {
//                hideLoading()
                handleException(ex, context)
            }
        }
    }

    private fun onUpdateDeviceName(device: Device) {
        val dialogLayout = layoutInflater.inflate(R.layout.edit_text_dialog, null)
        val editTextL = dialogLayout.findViewById<TextInputLayout>(R.id.editTextL)
        val editText = dialogLayout.findViewById<TextInputEditText>(R.id.editText)
        editText.setText(device.deviceName)
        val editTextDialog = AlertDialog.Builder(requireActivity()).setView(dialogLayout)
            .setTitle("Đổi tên thiết bị")
            .setPositiveButton(R.string.ok, null)
            .setNegativeButton(R.string.cancel) { dialog, which ->
                dialog.cancel()
            }
            .create()
        editTextDialog.show()

        editTextDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            if (editText.text.toString().trim().isEmpty()
                || editText.text.toString().trim().length > 50
            ) {
                editTextL.helperText = "Tên thiết bị không hợp lệ."
            } else if (editText.text.toString().trim() == device.deviceName) {
                editTextDialog.dismiss()
            } else {
                editTextL.helperText = " "
                lifecycleScope.launch {
                    try {
                        showLoading(requireContext())
                        ServerApi(requireContext()).retrofitService.publishSingle(
                            MqttPublish(
                                "SDL_${device.deviceId}",
                                "${MessageType.DEVICE_NAME}:${editText.text.toString().trim()}"
                            )
                        )
                        ServerApi(requireContext()).retrofitService.updateDevice(
                            Device(
                                device.deviceId,
                                device.accountId,
                                editText.text.toString().trim(),
                                device.deviceState,
                                device.createdDate
                            )
                        )

                        var newList = mutableListOf<Device>()
                        for (item in viewModel.devices.value!!) {
                            if (item.deviceId == device.deviceId) {
                                newList.add(
                                    Device(
                                        device.deviceId,
                                        device.accountId,
                                        editText.text.toString().trim(),
                                        device.deviceState,
                                        device.createdDate
                                    )
                                )
                            } else {
                                newList.add(item)
                            }
                        }
                        viewModel.setListDevice(newList)

                        newList = mutableListOf()
                        for (item in viewModel.devices.value!!) {
                            if (!item.accountId.isNullOrEmpty()) {
                                if (item.deviceId == device.deviceId) {
                                    newList.add(
                                        Device(
                                            device.deviceId,
                                            device.accountId,
                                            editText.text.toString().trim(),
                                            device.deviceState,
                                            device.createdDate
                                        )
                                    )
                                } else {
                                    newList.add(item)
                                }
                            }
                        }
                        homeViewModel.setMyListDevice(newList)
                        hideLoading()
                        editTextDialog.dismiss()
                    } catch (ex: Exception) {
                        hideLoading()
                        handleException(ex, requireContext())
                    }
                }
            }
        }

        val negativeButton = editTextDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
        negativeButton.setTextColor(resources.getColor(R.color.hint_text, null))
    }
}

class DeviceListAdapter(
    private val context: Context,
    private val accountId: String,
    private val onReloadData: (Device) -> Unit,
    private val onNameChange: (Device) -> Unit
) : ListAdapter<Device, DeviceListAdapter.ItemViewHolder>(DiffCallback) {
    inner class ItemViewHolder(private var binding: DeviceItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(device: Device, accountId: String) {
            binding.deviceName.text = device.deviceName
            if (device.accountId == accountId) {
                binding.checkIcon.visibility = View.VISIBLE
                binding.addButton.visibility = View.GONE
            } else {
                binding.checkIcon.visibility = View.GONE
                binding.addButton.visibility = View.VISIBLE
            }

            binding.deviceName.setOnClickListener {
                onNameChange(device)
            }

            binding.checkIcon.setOnClickListener {
                val currentDeviceId = EncryptedSharedPreferencesManager(context).getSelectedDevice()
                if (device.deviceId == currentDeviceId) {
                    EncryptedSharedPreferencesManager(context).saveSelectedDevice("")
                }
                onReloadData(
                    Device(
                        device.deviceId,
                        null,
                        device.deviceName,
                        device.deviceState,
                        device.createdDate
                    )
                )
            }

            binding.addButton.setOnClickListener {
                onReloadData(
                    Device(
                        device.deviceId,
                        accountId,
                        device.deviceName,
                        device.deviceState,
                        device.createdDate
                    )
                )
            }
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Device>() {
        override fun areItemsTheSame(oldItem: Device, newItem: Device): Boolean {
            return oldItem.deviceId == newItem.deviceId
        }

        override fun areContentsTheSame(oldItem: Device, newItem: Device): Boolean {
            return oldItem.deviceName == newItem.deviceName
                    && oldItem.accountId == newItem.accountId
                    && oldItem.deviceState == newItem.deviceState
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(DeviceItemBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, accountId)
    }
}