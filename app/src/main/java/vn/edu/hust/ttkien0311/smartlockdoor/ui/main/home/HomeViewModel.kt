package vn.edu.hust.ttkien0311.smartlockdoor.ui.main.home


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import vn.edu.hust.ttkien0311.smartlockdoor.network.Device
import vn.edu.hust.ttkien0311.smartlockdoor.network.MonthLabel
import vn.edu.hust.ttkien0311.smartlockdoor.network.Image

class HomeViewModel : ViewModel() {
    private val _devices = MutableLiveData<MutableList<Device>>()
    val devices: LiveData<MutableList<Device>> = _devices

    private val _dates = MutableLiveData<List<MonthLabel>>()
    val dates: LiveData<List<MonthLabel>> = _dates

    private val _image = MutableLiveData<Image>()
    val image: LiveData<Image> = _image

    private val _historyMode = MutableLiveData<String>()
    val historyMode: LiveData<String> = _historyMode

    fun setMyListDevice(listDevice: MutableList<Device>) {
        _devices.value = listDevice
    }

    fun setListDate(listMonthLabel: List<MonthLabel>) {
        _dates.value = listMonthLabel
    }

    fun onHistoryRowClicked(image: Image) {
        _image.value = image
    }

    fun setMode(mode: String) {
        _historyMode.value = mode
    }
}