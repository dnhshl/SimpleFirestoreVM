package com.example.simplefirestorevm

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.simplefirestorevm.databinding.FragmentEnterDataBinding
import com.example.simplefirestorevm.firestore.Sensordata
import com.example.simplefirestorevm.firestore.convertDateStringToTimestamp
import com.example.simplefirestorevm.model.FirestoreViewModel
import com.example.simplefirestorevm.model.LoginViewModel


class EnterDataFragment : Fragment() {
    private var _binding: FragmentEnterDataBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val dbvm: FirestoreViewModel by activityViewModels()
    private val authvm: LoginViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEnterDataBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dbvm.getFilteredData()

        // observe user to display login status
        authvm.user.observe(viewLifecycleOwner) { user ->
            if (user == null){
                binding.tvLoginStatus.text = getString(R.string.loggedinNOK)
                binding.tvLoginStatus.setTextColor(Color.RED)

                dbvm.setSensordataCollectionRef("noname")
            } else {
                var statusmsg = getString(R.string.loggedin).format(user.email)
                if (!user.displayName.isNullOrEmpty()) statusmsg += " (${user.displayName})"
                binding.tvLoginStatus.text = statusmsg
                binding.tvLoginStatus.setTextColor(Color.GREEN)

                dbvm.setSensordataCollectionRef(user.uid)
            }

        }


        binding.btnAdd.setOnClickListener {
            val sRaum = binding.etRoom.text.toString()
            val sTemp = binding.etTemp.text.toString()
            val sHum = binding.etAir.text.toString()
            val sDate = binding.etDate.text.toString()

            if (sRaum.isEmpty() || sTemp.isEmpty() || sHum.isEmpty() || sDate.isEmpty()) {
                Log.i(">>>>", "Eingabefehler: Data Missing")
            } else {
                val sensordata = Sensordata(
                    location = sRaum,
                    temperature = sTemp.toInt(),
                    humidity = sHum.toInt(),
                    timestamp = convertDateStringToTimestamp(sDate)
                )
                dbvm.writeDataToFirestore(sensordata)
                Log.i(">>>>", "writing $sensordata")
            }
        }

        binding.btnDelete.setOnClickListener {
            val sRaum = binding.etRoom.text.toString()
            val sTemp = binding.etTemp.text.toString()
            val sHum = binding.etAir.text.toString()
            val sDate = binding.etDate.text.toString()

            val sensordata = Sensordata(
                location = sRaum,
                temperature = sTemp.toInt(),
                humidity = sHum.toInt(),
                timestamp = convertDateStringToTimestamp(sDate)
            )

            dbvm.deleteSensorData(sensordata)
        }

        dbvm.sensordataList.observe(viewLifecycleOwner) { sensordata ->
            val adapter = ArrayAdapter(requireContext(),
                android.R.layout.simple_list_item_1, sensordata)
            binding.listView.adapter = adapter
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}