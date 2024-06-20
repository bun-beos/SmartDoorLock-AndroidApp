package vn.edu.hust.ttkien0311.smartlockdoor.ui.main.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.edu.hust.ttkien0311.smartlockdoor.databinding.HistoryItemBinding
import vn.edu.hust.ttkien0311.smartlockdoor.network.Image

class HistoryListAdapter(
    private val data: List<Image>,
    private val mode: String,
    private val clickListener: HistoryRowListener
) :
    RecyclerView.Adapter<HistoryListAdapter.ItemViewHolder>() {
    class ItemViewHolder(private val binding: HistoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(image: Image, mode: String, clickListener: HistoryRowListener) {
            if (mode == HistoryMode.LIST.toString()) {
                binding.cardViewList.visibility = View.VISIBLE
                binding.cardViewGrid.visibility = View.GONE
            } else {
                binding.cardViewList.visibility = View.GONE
                binding.cardViewGrid.visibility = View.VISIBLE
            }
            binding.image = image
            binding.clickListener = clickListener
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(
            HistoryItemBinding.inflate(layoutInflater, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item, mode, clickListener)
    }
}

class HistoryRowListener(val clickListener: (image: Image) -> Unit) {
    fun onClick(image: Image) = clickListener(image)
}