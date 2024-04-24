package vn.edu.hust.ttkien0311.smartlockdoor.network

import java.lang.Exception

sealed class AsyncResponse<out T> {
    data class Error(val exception: Exception) : AsyncResponse<Nothing>()
    data class Success<T>(val data : T) : AsyncResponse<T>()
}