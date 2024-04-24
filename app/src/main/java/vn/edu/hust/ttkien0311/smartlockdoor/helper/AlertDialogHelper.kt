package vn.edu.hust.ttkien0311.smartlockdoor.helper

import android.app.AlertDialog
import android.content.Context
import vn.edu.hust.ttkien0311.smartlockdoor.R

object AlertDialogHelper {
    private var alertDialog : AlertDialog? = null

    fun showAlertDialog(context : Context, title: String, message: String) {
        val builder = AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Ok") {
                    dialog, _ -> dialog.dismiss()
            }

        alertDialog = builder.create()
        alertDialog?.show()
    }

    fun showLoading(context: Context) {
        val builder = AlertDialog.Builder(context, R.style.TransparentDialogTheme)
            .setView(R.layout.loading)
            .setCancelable(false)

        alertDialog = builder.create()
        alertDialog?.show()
        alertDialog?.window?.setLayout(200, 200)
    }

    fun hideLoading() {
        alertDialog?.cancel()
    }
}