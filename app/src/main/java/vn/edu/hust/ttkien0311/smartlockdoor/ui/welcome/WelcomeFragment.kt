package vn.edu.hust.ttkien0311.smartlockdoor.ui.welcome

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import vn.edu.hust.ttkien0311.smartlockdoor.R
import vn.edu.hust.ttkien0311.smartlockdoor.databinding.FragmentWelcomeBinding

class WelcomeFragment : Fragment() {
    private lateinit var binding : FragmentWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_welcome, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.register.setOnClickListener {
            view.findNavController().navigate(R.id.action_welcomeFragment_to_registerFragment)
        }

        binding.login.setOnClickListener {
            view.findNavController().navigate(R.id.action_welcomeFragment_to_loginFragment)
        }
    }
}