package vn.edu.hust.ttkien0311.smartlockdoor.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import vn.edu.hust.ttkien0311.smartlockdoor.R
import vn.edu.hust.ttkien0311.smartlockdoor.databinding.FragmentEmailNotifyBinding

class EmailNotifyFragment : Fragment() {
    private lateinit var binding: FragmentEmailNotifyBinding
    private val args : EmailNotifyFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_email_notify, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val email = args.email

        binding.emailAddress.text = email

        binding.backButton.setOnClickListener {
            view.findNavController().popBackStack()
        }

        binding.buttonLogin.setOnClickListener {
            view.findNavController().navigate(R.id.action_emailNotifyFragment_to_loginFragment)
        }
    }
}