package vn.edu.hust.ttkien0311.smartlockdoor.ui.main.notification

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import vn.edu.hust.ttkien0311.smartlockdoor.network.Notification

class NotificationViewModel: ViewModel() {
    private val _notifications = MutableLiveData<MutableList<Notification>>()
    val notification: LiveData<MutableList<Notification>> = _notifications

    fun setListNotification(data: MutableList<Notification>) {
        _notifications.value = data
    }
}