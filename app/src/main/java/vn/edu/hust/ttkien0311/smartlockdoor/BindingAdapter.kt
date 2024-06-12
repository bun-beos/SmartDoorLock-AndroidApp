package vn.edu.hust.ttkien0311.smartlockdoor

import android.widget.ImageView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import vn.edu.hust.ttkien0311.smartlockdoor.network.Device
import vn.edu.hust.ttkien0311.smartlockdoor.ui.main.home.MyDeviceListAdapter

@BindingAdapter("imageUrl")
fun bindImage(imgView: ImageView, imgUrl: String?) {
    if (!imgUrl.isNullOrEmpty()) {
        imgUrl.let {
            val imgUri = imgUrl.toUri().buildUpon().scheme("https").build()
            imgView.load(imgUri) {
                error(R.drawable.ic_account_circle)
            }
        }
    }
}

//@BindingAdapter("myListDevice")
//fun bindMyListDevice(recyclerView: RecyclerView, data: List<Device>?) {
//    val adapter = recyclerView.adapter as MyDeviceListAdapter
//    adapter.submitList(data)
//}