package vn.edu.hust.ttkien0311.smartlockdoor.ui.welcome

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import vn.edu.hust.ttkien0311.smartlockdoor.R
import vn.edu.hust.ttkien0311.smartlockdoor.helper.EncryptedSharedPreferencesManager
import vn.edu.hust.ttkien0311.smartlockdoor.ui.main.MainActivity
import vn.edu.hust.ttkien0311.smartlockdoor.ui.main.NotificationDetailFragment

class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val sharedPreferencesManager = EncryptedSharedPreferencesManager(this)
//        sharedPreferencesManager.saveSelectedDevice("51b5f47b-b447-4f43-a59a-3b5473a8ef5e")
//        sharedPreferencesManager.saveAccessToken("")
//        sharedPreferencesManager.saveRefreshToken("")
//        sharedPreferencesManager.saveRefreshTokenExpires("")
//        sharedPreferencesManager.saveAccountId("")
//        sharedPreferencesManager.saveSelectedDevice("")
//        sharedPreferencesManager.saveLoginStatus(false)
        val fragmentToLoad = intent.getStringExtra("fragmentToLoad")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        if (fragmentToLoad == "NotificationDetailFragment") {
            val fragment = NotificationDetailFragment()
            val bundle = Bundle()
            bundle.putString("NotifId", intent.getStringExtra("NotifId"))
            bundle.putString("DoorState", intent.getStringExtra("DoorState"))
            fragment.arguments = bundle

            supportFragmentManager.beginTransaction()
                .replace(R.id.nav_host, fragment)
                .commit()
        } else if (sharedPreferencesManager.getLoginStatus()) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}