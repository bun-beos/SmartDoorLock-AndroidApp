package vn.edu.hust.ttkien0311.smartlockdoor.ui.main.home


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import vn.edu.hust.ttkien0311.smartlockdoor.network.Image

class HistoryViewModel : ViewModel() {
    private val _image = MutableLiveData<Image>()
    val image: LiveData<Image> = _image

    fun onHistoryRowClicked(image: Image) {
        _image.value = image
    }
}