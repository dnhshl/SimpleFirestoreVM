package com.example.simplefirestorevm

import android.R
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import com.example.simplefirestorevm.databinding.FragmentEnterDataBinding
import com.example.simplefirestorevm.databinding.FragmentFilterDataBinding
import com.example.simplefirestorevm.databinding.FragmentLoginstatusBinding
import com.example.simplefirestorevm.model.FirestoreViewModel

class FilterDataFragment : Fragment() {
    private var _binding: FragmentFilterDataBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val dbvm: FirestoreViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFilterDataBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonFilterDate.setOnClickListener {
            val date = binding.editDate.text.toString()
            dbvm.getSensorDataFilteredByDate(date)
        }


        binding.buttonFilterLoc.setOnClickListener {
            val location = binding.editLoc.text.toString()
            dbvm.getSensorDataFilteredByLocation(location)
        }

        binding.buttonFilterTemp.setOnClickListener {
            val temperature = binding.editTemp.text.toString().toInt()
            dbvm.getSensorDataFilteredByTemperature(temperature)
        }

        binding.buttonFilterHottest.setOnClickListener {
            val topX = binding.editHottest.text.toString().toInt()
            dbvm.getSensorDataFilteredByHottestTopX(topX)
        }

        dbvm.sensordataList.observe(viewLifecycleOwner) { sensordata ->
            val adapter = ArrayAdapter(requireContext(),
                R.layout.simple_list_item_1, sensordata)
            binding.listViewData.adapter = adapter
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}