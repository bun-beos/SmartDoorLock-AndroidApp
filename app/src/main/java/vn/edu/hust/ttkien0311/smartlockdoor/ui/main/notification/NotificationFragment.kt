package vn.edu.hust.ttkien0311.smartlockdoor.ui.main.notification

import android.content.Context
import android.graphics.Color
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
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import vn.edu.hust.ttkien0311.smartlockdoor.R
import vn.edu.hust.ttkien0311.smartlockdoor.databinding.FragmentNotificationBinding
import vn.edu.hust.ttkien0311.smartlockdoor.databinding.NotificationItemBinding
import vn.edu.hust.ttkien0311.smartlockdoor.helper.AlertDialogHelper.hideLoading
import vn.edu.hust.ttkien0311.smartlockdoor.helper.AlertDialogHelper.showLoading
import vn.edu.hust.ttkien0311.smartlockdoor.helper.EncryptedSharedPreferencesManager
import vn.edu.hust.ttkien0311.smartlockdoor.helper.ExceptionHelper.handleException
import vn.edu.hust.ttkien0311.smartlockdoor.helper.Helper.formatDateTime
import vn.edu.hust.ttkien0311.smartlockdoor.helper.SwipeGesture
import vn.edu.hust.ttkien0311.smartlockdoor.network.Notification
import vn.edu.hust.ttkien0311.smartlockdoor.network.ServerApi

class NotificationFragment : Fragment() {
    private lateinit var binding: FragmentNotificationBinding
    private val viewModel: NotificationViewModel by activityViewModels()
    private var myAccountId = ""

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
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_notification, container, false)
        binding.lifecycleOwner = this

        val sharedPreferencesManager = EncryptedSharedPreferencesManager(requireActivity())
        myAccountId = sharedPreferencesManager.getAccountId()

        if (viewModel.notification.value.isNullOrEmpty()) {
            getListNotification()
        } else {
            val adapter =
                NotificationListAdapter(requireContext(), viewModel.notification.value!!) {
                    id -> onDeleteNotification(id)
                }

            val swipeGesture = object : SwipeGesture(requireContext()) {
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    if (direction == ItemTouchHelper.LEFT) {
                        adapter.deleteItem(viewHolder.absoluteAdapterPosition)
                    }
                }
            }

            val touchHelper = ItemTouchHelper(swipeGesture)
            touchHelper.attachToRecyclerView(binding.listNotification)

            binding.listNotification.adapter = adapter

            binding.listNotification.visibility = View.VISIBLE
            binding.emptyContent.visibility = View.GONE
        }

        binding.swipeRefreshLayout.setColorSchemeColors(
            resources.getColor(
                R.color.link_color,
                null
            )
        )
        binding.swipeRefreshLayout.setOnRefreshListener {
            getListNotification()
            hideLoading()
        }

        return binding.root
    }

    private fun getListNotification() {
        lifecycleScope.launch {
            try {
                showLoading(requireActivity())
                val res =
                    ServerApi(requireActivity()).retrofitService.FilterNotification(myAccountId)
                hideLoading()

                if (res.isNotEmpty()) {
                    for (item in res) {
                        item.createdDate += "+07:00"
                        item.createdDate =
                            formatDateTime(item.createdDate, "HH:mm - dd/MM/yyyy")
                    }
                    val newData = mutableListOf<Notification>()
                    for (item in res) {
                        newData.add(item)
                    }
                    viewModel.setListNotification(newData)

                    val adapter =
                        NotificationListAdapter(requireContext(), viewModel.notification.value!!) {
                            id -> onDeleteNotification(id)
                        }

                    val swipeGesture = object : SwipeGesture(requireContext()) {
                        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                            if (direction == ItemTouchHelper.LEFT) {
                                adapter.deleteItem(viewHolder.absoluteAdapterPosition)
                            }
                        }
                    }

                    val touchHelper = ItemTouchHelper(swipeGesture)
                    touchHelper.attachToRecyclerView(binding.listNotification)

                    binding.listNotification.adapter = adapter

                    binding.listNotification.visibility = View.VISIBLE
                    binding.emptyContent.visibility = View.GONE
                } else {
                    binding.listNotification.visibility = View.GONE
                    binding.emptyContent.visibility = View.VISIBLE
                }
            } catch (ex: Exception) {
                hideLoading()
                handleException(ex, requireActivity())

                binding.listNotification.visibility = View.GONE
                binding.emptyContent.visibility = View.VISIBLE

            }
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun onDeleteNotification(id: String) {
        lifecycleScope.launch {
            try {
                ServerApi(requireContext()).retrofitService.deleteNotification(id)
            } catch (ex: Exception) {
//                handleException(ex, requireContext())
                Log.e("SLD", "Error delete notification: $ex")
            }
        }
    }
}

class NotificationListAdapter(
    private val context: Context,
    private val data: MutableList<Notification>,
    private val onDeleteItem: (String) -> Unit
) :
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

    fun deleteItem(position: Int) {
        if (position in 0 until itemCount) {
            onDeleteItem(data[position].notifId)
            data.removeAt(position)
            notifyItemRemoved(position)
        }
    }
}