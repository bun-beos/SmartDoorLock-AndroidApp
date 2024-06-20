package vn.edu.hust.ttkien0311.smartlockdoor.ui.main.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import vn.edu.hust.ttkien0311.smartlockdoor.network.Account
import vn.edu.hust.ttkien0311.smartlockdoor.network.Device

class ProfileViewModel : ViewModel() {
    private val _account = MutableLiveData<Account>()
    val account: LiveData<Account> = _account

    private val _devices = MutableLiveData<List<Device>>()
    val devices: LiveData<List<Device>> = _devices

    fun setAccount(data: Account) {
        _account.value = data
    }

    fun setListDevice(devices: List<Device>) {
        _devices.value = devices
    }
}