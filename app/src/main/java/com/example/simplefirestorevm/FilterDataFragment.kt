package com.example.simplefirestorevm

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.simplefirestorevm.databinding.FragmentFilterDataBinding
import com.example.simplefirestorevm.firestore.ConditionType
import com.example.simplefirestorevm.firestore.FilterCondition
import com.example.simplefirestorevm.firestore.OrderCondition
import com.example.simplefirestorevm.firestore.convertDateStringToTimestamp
import com.example.simplefirestorevm.model.FirestoreViewModel
import com.google.firebase.firestore.Query

class FilterDataFragment : Fragment() {
    private var _binding: FragmentFilterDataBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val dbvm: FirestoreViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFilterDataBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonFilterDate.setOnClickListener {
            val date = binding.editDate.text.toString()
            val startOfDay = convertDateStringToTimestamp(date + " 00:00")
            val endOfDay = convertDateStringToTimestamp(date + " 23:59")
            val filters = listOf(
                FilterCondition("timestamp", ConditionType.GREATER_THAN_OR_EQUAL, startOfDay),
                FilterCondition("timestamp", ConditionType.LESS_THAN_OR_EQUAL, endOfDay)
            )
            dbvm.getFilteredData(filters = filters)
        }


        binding.buttonFilterLoc.setOnClickListener {
            val location = binding.editLoc.text.toString()
            val filters = listOf(
                FilterCondition("location", ConditionType.EQUAL_TO, location)
            )
            dbvm.getFilteredData(filters = filters)
        }

        binding.buttonFilterTemp.setOnClickListener {
            val temperature = binding.editTemp.text.toString().toInt()
            val filters = listOf(
              FilterCondition("temperature", ConditionType.GREATER_THAN_OR_EQUAL, temperature)
            )
            dbvm.getFilteredData(filters = filters)
        }

        binding.buttonFilterHottest.setOnClickListener {
            val topX = binding.editHottest.text.toString().toInt()
            val orders = listOf(
                OrderCondition("temperature", Query.Direction.DESCENDING)
            )
            dbvm.getFilteredData(orders = orders, limit = topX)
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