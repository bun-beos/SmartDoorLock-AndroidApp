package vn.edu.hust.ttkien0311.smartlockdoor.ui.main.home

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import vn.edu.hust.ttkien0311.smartlockdoor.databinding.MonthLabelItemBinding
import vn.edu.hust.ttkien0311.smartlockdoor.helper.AlertDialogHelper.hideLoading
import vn.edu.hust.ttkien0311.smartlockdoor.helper.AlertDialogHelper.showLoading
import vn.edu.hust.ttkien0311.smartlockdoor.helper.ExceptionHelper.handleException
import vn.edu.hust.ttkien0311.smartlockdoor.helper.Helper.formatDateTime
import vn.edu.hust.ttkien0311.smartlockdoor.network.Date
import vn.edu.hust.ttkien0311.smartlockdoor.network.ServerApi

class MonthListAdapter(
    private val context: Context,
    private val data: List<Date>,
    private val clickListener: HistoryRowListener
) :
    RecyclerView.Adapter<MonthListAdapter.ItemViewHolder>() {

    class ItemViewHolder(
        private val binding: MonthLabelItemBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(context: Context, dateValue: Date, clickListener: HistoryRowListener) {
            binding.date = dateValue.date
            binding.linearLayout2.setOnClickListener {
                if (!dateValue.isExpanded) {
                    showLoading(context)

                    CoroutineScope(Dispatchers.Main).launch {
                        try {
                            val res = ServerApi(context).retrofitService.filterImage(
                                null,
                                dateValue.date,
                                dateValue.date.plusMonths(1).plusDays(-1)
                            )
                            hideLoading()
                            for (image in res) {
                                image.createdDate = formatDateTime(image.createdDate, "dd/MM/yyyy HH:mm:ss")
                            }
                            binding.historyList.adapter = HistoryListAdapter(res, clickListener)
                            binding.arrowDown.rotation = 180f
                        } catch (ex: Exception) {
                            hideLoading()
                            handleException(ex, context)
                        }
                    }

                    binding.historyList.visibility = View.VISIBLE
                } else {
                    binding.arrowDown.rotation = 0f
                    binding.historyList.visibility = View.GONE
                }
                dateValue.isExpanded = !dateValue.isExpanded
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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = data[position]
        holder.bind(context, item, clickListener)
    }
}

