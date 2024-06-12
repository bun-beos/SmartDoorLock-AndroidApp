package vn.edu.hust.ttkien0311.smartlockdoor.ui.main.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import vn.edu.hust.ttkien0311.smartlockdoor.databinding.MyDeviceItemBinding
import vn.edu.hust.ttkien0311.smartlockdoor.network.Device

//class MyDeviceListAdapter(private val context: Context) :
//    ListAdapter<Device, MyDeviceListAdapter.ItemViewHolder>(DiffCallback) {
//    class ItemViewHolder(private var binding: MyDeviceItemBinding) :
//        RecyclerView.ViewHolder(binding.root) {
//        fun bind(device: Device) {
//            binding.deviceName.text = device.deviceName
//            binding.cardView.setOnClickListener {
//                binding.checkIcon.visibility = View.VISIBLE
//            }
//        }
//    }
//
//    companion object DiffCallback : DiffUtil.ItemCallback<Device>() {
//        override fun areItemsTheSame(oldItem: Device, newItem: Device): Boolean {
//            return oldItem.deviceId == newItem.deviceId
//        }
//
//        override fun areContentsTheSame(oldItem: Device, newItem: Device): Boolean {
//            return oldItem.deviceName == newItem.deviceName && oldItem.deviceState == newItem.deviceState
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
//
class MyDeviceListAdapter(private val context: Context, private val data: List<Device>) :
    RecyclerView.Adapter<MyDeviceListAdapter.ItemViewHolder>() {
    class ItemViewHolder(private var binding: MyDeviceItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(device: Device) {
            binding.deviceName.text = device.deviceName
            binding.cardView.setOnClickListener {
                binding.checkIcon.visibility = View.VISIBLE
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