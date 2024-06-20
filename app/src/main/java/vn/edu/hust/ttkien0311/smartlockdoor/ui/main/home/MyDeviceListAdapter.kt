package vn.edu.hust.ttkien0311.smartlockdoor.ui.main.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import vn.edu.hust.ttkien0311.smartlockdoor.databinding.MyDeviceItemBinding
import vn.edu.hust.ttkien0311.smartlockdoor.helper.EncryptedSharedPreferencesManager
import vn.edu.hust.ttkien0311.smartlockdoor.network.Device

class MyDeviceListAdapter(
    context: Context,
    private val data: MutableList<Device>,
    private val onDeviceSelected: (id: String) -> Unit
) :
    RecyclerView.Adapter<MyDeviceListAdapter.ItemViewHolder>() {

    private val sharedPreferencesManager = EncryptedSharedPreferencesManager(context)
    private var selectedId = sharedPreferencesManager.getSelectedDevice()

    inner class ItemViewHolder(private var binding: MyDeviceItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(device: Device) {
            binding.deviceName.text = device.deviceName
            binding.checkIcon.visibility =
                if (device.deviceId == selectedId) View.VISIBLE else View.GONE

            binding.cardView.setOnClickListener {
                sharedPreferencesManager.saveSelectedDevice(device.deviceId)
                selectedId = device.deviceId
                data.remove(device)
                data.add(0, device)

                notifyDataSetChanged()

                onDeviceSelected(device.deviceId)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(MyDeviceItemBinding.inflate(layoutInflater, parent, false))
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
    }
}

//class MyDeviceListAdapter(
//    context: Context,
//    private val onDeviceSelected: (device: Device) -> Unit
//) :
//    ListAdapter<Device, MyDeviceListAdapter.ItemViewHolder>(DiffCallback) {
//
//    private val sharedPreferencesManager = EncryptedSharedPreferencesManager(context)
//
//    inner class ItemViewHolder(private var binding: MyDeviceItemBinding) :
//        RecyclerView.ViewHolder(binding.root) {
//        fun bind(device: Device) {
//            binding.deviceName.text = device.deviceName
//            binding.checkIcon.visibility =
//                if (device.selected) View.VISIBLE else View.GONE
//
//            binding.cardView.setOnClickListener {
//                sharedPreferencesManager.saveSelectedDevice(device.deviceId)
//                onDeviceSelected(device)
//            }
//
//            binding.executePendingBindings()
//        }
//    }
//
//    companion object DiffCallback : DiffUtil.ItemCallback<Device>() {
//        override fun areItemsTheSame(oldItem: Device, newItem: Device): Boolean {
//            return oldItem.deviceId == newItem.deviceId
//        }
//
//        override fun areContentsTheSame(oldItem: Device, newItem: Device): Boolean {
//            return oldItem.deviceName == newItem.deviceName
//                    && oldItem.deviceState == newItem.deviceState
//                    && oldItem.selected == newItem.selected
//        }
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
//        val layoutInflater = LayoutInflater.from(parent.context)
//        return ItemViewHolder(MyDeviceItemBinding.inflate(layoutInflater, parent, false))
//    }
//
//    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
//        val item = getItem(position)
//        holder.bind(item)
//    }
//}
