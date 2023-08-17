package com.example.simplefirestorevm.model

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Locale

class FirestoreViewModel : ViewModel() {

    private val db = Firebase.firestore

    fun writeDataToFirestore(raum: String, temp: String, hum: String, dateTimeString: String) {

        val dateTimeFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
        val dateTime = dateTimeFormat.parse(dateTimeString)
        val timestamp = dateTime?.let { Timestamp(it) }

        val data = hashMapOf(
            "location" to raum,
            "temperature" to temp.toInt(),
            "humidity" to hum.toInt(),
            "timestamp" to timestamp,
        )

        db.collection("Sensordata")
            .add(data)
            .addOnSuccessListener { document ->
                Log.i(">>>", "$data added with id: ${document.id}")
            }
            .addOnFailureListener { e ->
                Log.w(">>>", "Error adding document", e)
            }
    }

}