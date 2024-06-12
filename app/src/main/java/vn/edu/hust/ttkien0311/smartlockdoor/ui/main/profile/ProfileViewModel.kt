package vn.edu.hust.ttkien0311.smartlockdoor.ui.main.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import vn.edu.hust.ttkien0311.smartlockdoor.network.Account

class ProfileViewModel : ViewModel() {
    private val _account = MutableLiveData<Account>()
    val account: LiveData<Account> = _account

    fun setAccount(data: Account) {
        _account.value = data
    }
}