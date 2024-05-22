package vn.edu.hust.ttkien0311.smartlockdoor.ui.main.member

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import vn.edu.hust.ttkien0311.smartlockdoor.network.Member

enum class State {ADD, EDIT}

class MemberViewModel : ViewModel() {
    private val _member = MutableLiveData<Member?>()
    val member: LiveData<Member?> = _member

    fun onMemberRowClicked(member: Member?) {
        _member.value = member
    }
}