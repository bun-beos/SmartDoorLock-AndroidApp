package vn.edu.hust.ttkien0311.smartlockdoor.ui.main.home

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import vn.edu.hust.ttkien0311.smartlockdoor.R
import vn.edu.hust.ttkien0311.smartlockdoor.databinding.FragmentHomeBinding
import vn.edu.hust.ttkien0311.smartlockdoor.helper.ExceptionHelper.handleException
import vn.edu.hust.ttkien0311.smartlockdoor.network.Date
import vn.edu.hust.ttkien0311.smartlockdoor.network.ServerApi
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HistoryViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            requireActivity().finish()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        var listMonth: List<Date>
        lifecycleScope.launch {
            try {
                val res = ServerApi(requireActivity()).retrofitService.getOldestTime()
                listMonth = createListMonth(res)
            } catch (ex: Exception) {
                listMonth = emptyList()
                handleException(ex, requireActivity())
            }
            binding.listMonth.adapter = MonthListAdapter(requireActivity(), listMonth, HistoryRowListener { image ->
                viewModel.onHistoryRowClicked(image)
                findNavController().navigate(R.id.action_homeFragment_to_historyDetailFragment)
            })
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.magnifyIcon.setOnClickListener {
            Toast.makeText(requireActivity(), "Coming soon", Toast.LENGTH_SHORT).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createListMonth(oldestTime: String): List<Date> {
        val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
        var oldestDate = OffsetDateTime.parse(oldestTime, formatter).toLocalDate()
        oldestDate = LocalDate.of(oldestDate.year, oldestDate.monthValue, 1)
        var currentDate = LocalDate.of(LocalDate.now().year, LocalDate.now().monthValue, 1)

        val listDate = mutableListOf<Date>()

        while (oldestDate.isBefore(currentDate)) {
            listDate.add(Date(currentDate))
            currentDate = currentDate.plusMonths(-1)
        }
        listDate.add(Date(oldestDate))

        return listDate
    }
}