package vn.edu.hust.ttkien0311.smartlockdoor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import vn.edu.hust.ttkien0311.smartlockdoor.helper.EncryptedSharedPreferencesManager

class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val sharedPreferencesManager = EncryptedSharedPreferencesManager(this)
        if (sharedPreferencesManager.getLoginStatus()) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

//        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
//            if (!task.isSuccessful) {
//                // Handle error
//                return@OnCompleteListener
//            }
//
//            // Get new FCM registration token
////            val token = task.result
////            Log.d("SLD", token)
//        })
    }
}