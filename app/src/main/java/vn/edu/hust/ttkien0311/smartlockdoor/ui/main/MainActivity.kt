package vn.edu.hust.ttkien0311.smartlockdoor.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import vn.edu.hust.ttkien0311.smartlockdoor.R
import vn.edu.hust.ttkien0311.smartlockdoor.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
//            if (!task.isSuccessful) {
//                Log.w("SLD", "Fetching FCM registration token failed", task.exception)
//                return@OnCompleteListener
//            }
//            // Get new FCM registration token
//            val token = task.result
//            Log.d("SLD", "FCM registration token: $token")
//        })

        binding.bottomNavigationView.setOnItemSelectedListener {
            val navController = findNavController(this, R.id.nav_host)
            val currentFragment = navController.currentDestination?.id

            when (it.itemId) {
                R.id.nav_home -> {
                    if (currentFragment != R.id.homeFragment) {
                        navController.navigate(R.id.action_to_homeFragment)
                    }
                }

                R.id.nav_profile -> {
                    if (currentFragment != R.id.profileFragment) {
                        navController.navigate(R.id.action_to_profileFragment)
                    }
                }

                R.id.nav_member -> {
                    if (currentFragment != R.id.memberFragment) {
                        navController.navigate(R.id.action_to_memberFragment)
                    }
                }

                R.id.nav_notification -> {
                    if (currentFragment != R.id.notificationFragment) {
                        navController.navigate(R.id.action_to_notificationFragment)
                    }
                }

//                R.id.nav_setting -> {
//                    if (currentFragment != R.id.settingFragment) {
//                        navController.navigate(R.id.action_to_settingFragment)
//                    }
//                }
            }
            true
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        findNavController(R.id.nav_host).addOnDestinationChangedListener { controller, destination, arguments ->
            when (destination.id) {
                R.id.homeFragment, R.id.memberFragment, R.id.profileFragment, R.id.settingFragment, R.id.notificationFragment -> {
                    binding.bottomNavigationView.visibility = View.VISIBLE
                }

                else -> {
                    binding.bottomNavigationView.visibility = View.GONE
                }
            }
        }
    }
}
