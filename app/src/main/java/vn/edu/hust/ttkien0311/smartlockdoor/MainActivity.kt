package vn.edu.hust.ttkien0311.smartlockdoor


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import vn.edu.hust.ttkien0311.smartlockdoor.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

//                R.id.nav_setting -> {
//                    if (currentFragment != R.id.settingFragment) {
//                        navController.navigate(R.id.action_to_settingFragment)
//                    }
//                }

                R.id.nav_member -> {
                    if (currentFragment != R.id.memberFragment) {
                        navController.navigate(R.id.action_to_memberFragment)
                    }
                }
            }
            true
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        findNavController(R.id.nav_host).addOnDestinationChangedListener { controller, destination, arguments ->
            when (destination.id) {
                R.id.homeFragment, R.id.memberFragment, R.id.profileFragment, R.id.settingFragment -> {
                    binding.bottomNavigationView.visibility = View.VISIBLE
                }

                else -> {
                    binding.bottomNavigationView.visibility = View.GONE
                }
            }
        }
    }
}
