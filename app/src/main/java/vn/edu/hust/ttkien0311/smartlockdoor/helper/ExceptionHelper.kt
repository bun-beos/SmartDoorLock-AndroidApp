package vn.edu.hust.ttkien0311.smartlockdoor.helper

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.HttpException
import vn.edu.hust.ttkien0311.smartlockdoor.helper.AlertDialogHelper.showAlertDialog
import vn.edu.hust.ttkien0311.smartlockdoor.network.ErrorResponse
import java.net.SocketException
import java.util.regex.Pattern
import kotlin.Exception

object ExceptionHelper {
    private fun decodeUnicodeEscape(input: String): String {
        val pattern = Pattern.compile("\\\\u([0-9a-fA-F]{4})")
        val matcher = pattern.matcher(input)
        val buffer = StringBuffer(input.length)
        while (matcher.find()) {
            val hex = matcher.group(1)
            val codePoint = Integer.parseInt(hex!!, 16)
            matcher.appendReplacement(buffer, String(Character.toChars(codePoint)))
        }
        matcher.appendTail(buffer)
        return buffer.toString()
    }

    fun handleException(ex: Exception, context: Context) {
        Log.d("SLD", "Exception: $ex")
        ex.printStackTrace()
        when (ex) {
            is SocketException -> {
                showAlertDialog(
                    context,
                    "Lỗi kết nối",
                    "Không thể kết nối đến hệ thống. Vui lòng kiểm tra kết nối mạng hoặc thử lại sau."
                )
            }

            is HttpException -> {
                if (ex.code() != 401) {
                    try {
                        val msg =
                            decodeUnicodeEscape(ex.response()?.errorBody()?.string()!!)
                        val adapter: JsonAdapter<ErrorResponse> =
                            Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                                .adapter(ErrorResponse::class.java)
                        val jsonMsg = adapter.fromJson(msg)

                        if (ex.code() >= 500) {

                            Toast.makeText(
                                context,
                                "${jsonMsg?.userMessage}",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            showAlertDialog(context, null, jsonMsg!!.userMessage)
                        }
                        Log.d("SLD", "Dev message: ${jsonMsg?.devMessage}")
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }
                }
            }

            is IllegalStateException -> {
            }

            else -> {
                Toast.makeText(
                    context,
                    "Có lỗi xảy ra, vui lòng thử lại sau",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}