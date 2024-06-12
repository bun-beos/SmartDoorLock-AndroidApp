package vn.edu.hust.ttkien0311.smartlockdoor.ui.main.home


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import vn.edu.hust.ttkien0311.smartlockdoor.network.Device
import vn.edu.hust.ttkien0311.smartlockdoor.network.MonthLabel
import vn.edu.hust.ttkien0311.smartlockdoor.network.Image

class HomeViewModel : ViewModel() {
    private val _devices = MutableLiveData<List<Device>>()
    val devices: LiveData<List<Device>> = _devices

    private val _dates = MutableLiveData<List<MonthLabel>>()
    val dates: LiveData<List<MonthLabel>> = _dates

    private val _image = MutableLiveData<Image>()
    val image: LiveData<Image> = _image

    fun setMyListDevice(listDevice: List<Device>) {
        _devices.value = listDevice
    }

    fun setListDate(listMonthLabel: List<MonthLabel>) {
        _dates.value = listMonthLabel
    }

    fun onHistoryRowClicked(image: Image) {
        _image.value = image
    }
}