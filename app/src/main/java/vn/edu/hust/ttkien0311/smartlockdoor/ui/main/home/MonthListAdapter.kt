package vn.edu.hust.ttkien0311.smartlockdoor.ui.main.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import vn.edu.hust.ttkien0311.smartlockdoor.databinding.MonthLabelItemBinding
import vn.edu.hust.ttkien0311.smartlockdoor.helper.ExceptionHelper.handleException
import vn.edu.hust.ttkien0311.smartlockdoor.helper.Helper.formatDateTime
import vn.edu.hust.ttkien0311.smartlockdoor.network.MonthLabel
import vn.edu.hust.ttkien0311.smartlockdoor.network.ServerApi

class MonthListAdapter(
    private val context: Context,
    private val deviceId: String,
    private val data: List<MonthLabel>,
    private val mode: String,
    private val clickListener: HistoryRowListener
) :
    RecyclerView.Adapter<MonthListAdapter.ItemViewHolder>() {

    class ItemViewHolder(
        private val binding: MonthLabelItemBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            context: Context,
            deviceId: String,
            monthLabelValue: MonthLabel,
            mode: String,
            clickListener: HistoryRowListener
        ) {
            binding.date = monthLabelValue.date

            if (monthLabelValue.isExpanded && monthLabelValue.images.isNotEmpty()) {
                if (mode == HistoryMode.LIST.toString()) {
                    binding.historyList.layoutManager = LinearLayoutManager(context)
                } else {
                    binding.historyList.layoutManager = GridLayoutManager(context, 2)
                }

                binding.historyList.adapter =
                    HistoryListAdapter(monthLabelValue.images, mode, clickListener)

                binding.arrowDown.rotation = 180f
                binding.historyList.visibility = View.VISIBLE
                binding.emptyContent.visibility = View.GONE
            } else {
                binding.arrowDown.rotation = 0f

                binding.historyList.visibility = View.GONE
                binding.emptyContent.visibility = View.GONE
            }

            binding.linearLayout2.setOnClickListener {
                if (!monthLabelValue.isExpanded) {
                    CoroutineScope(Dispatchers.Main).launch {
                        try {
                            val res = ServerApi(context).retrofitService.filterImage(
                                deviceId,
                                null,
                                monthLabelValue.date,
                                monthLabelValue.date.plusMonths(1).plusDays(-1)
                            )

                            if (res.isNotEmpty()) {
                                for (image in res) {
                                    image.createdDate =
                                        formatDateTime(image.createdDate, "HH:mm - dd/MM/yyyy")
                                }
                                monthLabelValue.images = res

                                if (mode == HistoryMode.LIST.toString()) {
                                    binding.historyList.layoutManager = LinearLayoutManager(context)
                                } else {
                                    binding.historyList.layoutManager =
                                        GridLayoutManager(context, 2)
                                }

                                binding.historyList.adapter =
                                    HistoryListAdapter(monthLabelValue.images, mode, clickListener)
                                binding.arrowDown.rotation = 180f

                                binding.historyList.visibility = View.VISIBLE
                                binding.emptyContent.visibility = View.GONE
                            } else {
                                binding.historyList.visibility = View.GONE
                                binding.emptyContent.visibility = View.VISIBLE
                            }
                        } catch (ex: Exception) {
//                            hideLoading()
                            handleException(ex, context)

                            binding.arrowDown.rotation = 180f
                            binding.historyList.visibility = View.GONE
                            binding.emptyContent.visibility = View.VISIBLE
                        }
                    }
                } else {
                    binding.arrowDown.rotation = 0f
                    binding.historyList.visibility = View.GONE
                    binding.emptyContent.visibility = View.GONE
                }
                monthLabelValue.isExpanded = !monthLabelValue.isExpanded
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(
            MonthLabelItemBinding.inflate(layoutInflater, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = data[position]
        holder.bind(context, deviceId, item, mode, clickListener)
    }
}

