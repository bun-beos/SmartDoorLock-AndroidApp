package vn.edu.hust.ttkien0311.smartlockdoor.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import vn.edu.hust.ttkien0311.smartlockdoor.R
import vn.edu.hust.ttkien0311.smartlockdoor.databinding.FragmentDoorBinding

class DoorFragment : Fragment() {
    private lateinit var binding: FragmentDoorBinding
    private lateinit var imageModeBtn: ImageButton
    private lateinit var videoModeBtn: ImageButton
    private lateinit var captureImageBtn: ImageButton
    private lateinit var doorStateBtn: ImageButton
    private var door = "CLOSE"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_door, container, false)
        imageModeBtn = binding.imageModeButton
        videoModeBtn = binding.videoModeButton
        captureImageBtn = binding.captureImageButton
        doorStateBtn = binding.doorStateButton

        imageModeBtn.isSelected = true
        videoModeBtn.isSelected = !imageModeBtn.isSelected

        if (door == "CLOSE") {
            doorStateBtn.setImageResource(R.drawable.ic_door_closed)
            binding.doorStateText.text = "Đang đóng"
        } else {
            doorStateBtn.setImageResource(R.drawable.ic_door_open)
            binding.doorStateText.text = "Đang mở"
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        imageModeBtn.setOnClickListener {
            imageModeBtn.isSelected = true
            videoModeBtn.isSelected = !imageModeBtn.isSelected
            captureImageBtn.isEnabled = true
            binding.captureImageText.isEnabled = true
        }

        videoModeBtn.setOnClickListener {
            videoModeBtn.isSelected = true
            imageModeBtn.isSelected = !videoModeBtn.isSelected
            captureImageBtn.isEnabled = false
            binding.captureImageText.isEnabled = false
        }

        captureImageBtn.setOnClickListener {
            Toast.makeText(requireActivity(), "Capture", Toast.LENGTH_SHORT).show()
        }

        doorStateBtn.setOnClickListener {
            if (door == "CLOSE") {
                door = "OPEN"
                doorStateBtn.setImageResource(R.drawable.ic_door_open)
                binding.doorStateText.text = "Đang mở"
            } else {
                door = "CLOSE"
                doorStateBtn.setImageResource(R.drawable.ic_door_closed)
                binding.doorStateText.text = "Đang đóng"
            }
        }
    }
}