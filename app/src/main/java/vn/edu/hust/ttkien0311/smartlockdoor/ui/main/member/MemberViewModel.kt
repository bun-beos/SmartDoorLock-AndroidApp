package vn.edu.hust.ttkien0311.smartlockdoor.ui.main.member

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import vn.edu.hust.ttkien0311.smartlockdoor.network.Member

enum class State { ADD, EDIT }

class MemberViewModel : ViewModel() {
    private val _members = MutableLiveData<List<Member>>()
    val members: LiveData<List<Member>> = _members

    private val _member = MutableLiveData<Member?>()
    val member: LiveData<Member?> = _member

    fun setListMember(data: List<Member>) {
        _members.value = data
    }

    fun onMemberRowClicked(member: Member?) {
        _member.value = member
    }
}