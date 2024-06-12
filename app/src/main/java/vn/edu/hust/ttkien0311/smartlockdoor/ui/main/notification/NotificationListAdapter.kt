package vn.edu.hust.ttkien0311.smartlockdoor.ui.main.notification

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import vn.edu.hust.ttkien0311.smartlockdoor.databinding.NotificationItemBinding
import vn.edu.hust.ttkien0311.smartlockdoor.network.Notification
import vn.edu.hust.ttkien0311.smartlockdoor.network.ServerApi

class NotificationListAdapter(private val context: Context, private val data: List<Notification>) :
    RecyclerView.Adapter<NotificationListAdapter.ItemViewHolder>() {
    class ItemViewHolder(val binding: NotificationItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(context: Context, notification: Notification) {
            binding.notification = notification
            if (notification.state == 1) {
                binding.createdDate.textSize = 15f
                binding.createdDate.setTextColor(Color.parseColor("#6F6F6F"))
                binding.notifBody.setTextColor(Color.parseColor("#404040"))
                binding.notifState.visibility = View.INVISIBLE
            } else {
                binding.createdDate.textSize = 17f
                binding.createdDate.setTextColor(Color.parseColor("#000000"))
                binding.notifState.visibility = View.VISIBLE
            }
            binding.cardView.setOnClickListener {
                notification.state = 1
                binding.createdDate.textSize = 15f
                binding.createdDate.setTextColor(Color.parseColor("#6F6F6F"))
                binding.notifBody.setTextColor(Color.parseColor("#404040"))
                binding.notifState.visibility = View.INVISIBLE
                CoroutineScope(Dispatchers.Default).launch {
                    try {
                        ServerApi(context).retrofitService.updateNotification(notification.notifId)
                    } catch (ex: Exception) {
                        Log.d("SLD", "Error update notification: $ex")
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(NotificationItemBinding.inflate(layoutInflater, parent, false))
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = data[position]
        holder.bind(context, item)
    }
}