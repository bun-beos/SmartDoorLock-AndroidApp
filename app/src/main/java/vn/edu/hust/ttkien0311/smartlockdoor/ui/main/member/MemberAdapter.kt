package vn.edu.hust.ttkien0311.smartlockdoor.ui.main.member

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.edu.hust.ttkien0311.smartlockdoor.databinding.MemberItemBinding
import vn.edu.hust.ttkien0311.smartlockdoor.network.Member

class MemberAdapter(private val data: List<Member>, private val clickListener: MemberItemListener) :
    RecyclerView.Adapter<MemberAdapter.ItemViewHolder>() {
    class ItemViewHolder(private val binding: MemberItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(member: Member, clickListener: MemberItemListener) {
            binding.member = member
            binding.clickListener = clickListener
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(MemberItemBinding.inflate(layoutInflater, parent, false))
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item, clickListener)
    }
}

class MemberItemListener(private val clickListener: (member: Member) -> Unit) {
    fun onClick(member: Member) = clickListener(member)
}