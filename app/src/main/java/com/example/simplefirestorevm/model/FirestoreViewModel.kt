package com.example.simplefirestorevm.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplefirestorevm.firestore.Sensordata
import com.example.simplefirestorevm.firestore.convertDateStringToTimestamp
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale

class FirestoreViewModel : ViewModel() {


    // Keep current user and collection up to date
    private val auth = Firebase.auth
    private lateinit var sensordataCollectionRef: CollectionReference
    private val authStateListener = FirebaseAuth.AuthStateListener { auth ->
        val uid = auth.currentUser?.uid ?: "no_user"
        sensordataCollectionRef =
            Firebase.firestore.collection("users")
                 .document(uid)
                 .collection("Sensordata")
    }
    init {
        auth.addAuthStateListener(authStateListener)

        val uid = auth.currentUser?.uid ?: "no_user"
        sensordataCollectionRef =
            Firebase.firestore.collection("users")
                .document(uid)
                .collection("Sensordata")

        subscribeToRealtimeUpdates()
    }

    fun writeDataToFirestore(sensordata: Sensordata) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                sensordataCollectionRef.add(sensordata).await()
                //getAllSensorData()
            }
            catch(e: Exception) { Log.i(">>>", "Error writing Data: $e") }
        }
    }


    // Daten als Ergebnis einer Abfrage aus der DB
    private var _sensordataList = MutableLiveData<List<Sensordata>>()
    val sensordataList: LiveData<List<Sensordata>>
        get() = _sensordataList

    fun getAllSensorData() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val querySnapshot = sensordataCollectionRef.get().await()
                val dataList = mutableListOf<Sensordata>()
                for(document in querySnapshot.documents) {
                    val data = document.toObject(Sensordata::class.java)
                    dataList.add(data!!)
                }
                withContext(Dispatchers.Main) {
                    _sensordataList.value = dataList
                }
            } catch(e: Exception) {
                Log.i(">>>", "Error retrieving data $e")
            }
        }
    }

    fun getSensorDataFilteredByLocation(location: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val querySnapshot =
                    sensordataCollectionRef
                        .whereEqualTo("location", location)
                        .get()
                        .await()
                val dataList = mutableListOf<Sensordata>()
                for(document in querySnapshot.documents) {
                    val data = document.toObject(Sensordata::class.java)
                    dataList.add(data!!)
                }
                withContext(Dispatchers.Main) {
                    _sensordataList.value = dataList
                }
            } catch(e: Exception) {
                Log.i(">>>", "Error retrieving data $e")
            }
        }
    }

    fun getSensorDataFilteredByTemperature(temperature: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val querySnapshot =
                    sensordataCollectionRef
                        .whereGreaterThanOrEqualTo("temperature", temperature)
                        .get()
                        .await()
                val dataList = mutableListOf<Sensordata>()
                for(document in querySnapshot.documents) {
                    val data = document.toObject(Sensordata::class.java)
                    dataList.add(data!!)
                }
                withContext(Dispatchers.Main) {
                    _sensordataList.value = dataList
                }
            } catch(e: Exception) {
                Log.i(">>>", "Error retrieving data $e")
            }
        }
    }

    fun getSensorDataFilteredByDate(date: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val startOfDay = convertDateStringToTimestamp(date + " 00:00")
                val endOfDay = convertDateStringToTimestamp(date + " 23:59")
                val querySnapshot =
                    sensordataCollectionRef
                        .whereGreaterThanOrEqualTo("timestamp", startOfDay)
                        .whereLessThanOrEqualTo("timestamp", endOfDay)
                        .get()
                        .await()
                val dataList = mutableListOf<Sensordata>()
                for(document in querySnapshot.documents) {
                    val data = document.toObject(Sensordata::class.java)
                    dataList.add(data!!)
                }
                withContext(Dispatchers.Main) {
                    _sensordataList.value = dataList
                }
            } catch(e: Exception) {
                Log.i(">>>", "Error retrieving data $e")
            }
        }
    }

    fun getSensorDataFilteredByHottestTopX(topX: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                 val querySnapshot =
                    sensordataCollectionRef
                        .orderBy("temperature", Query.Direction.DESCENDING)
                        .limit(topX.toLong())
                        .get()
                        .await()
                val dataList = mutableListOf<Sensordata>()
                for(document in querySnapshot.documents) {
                    val data = document.toObject(Sensordata::class.java)
                    dataList.add(data!!)
                }
                withContext(Dispatchers.Main) {
                    _sensordataList.value = dataList
                }
            } catch(e: Exception) {
                Log.i(">>>", "Error retrieving data $e")
            }
        }
    }

    fun deleteSensorData(sensordata: Sensordata) {
        CoroutineScope(Dispatchers.IO).launch {
            val query = sensordataCollectionRef
                .whereEqualTo("location", sensordata.location)
                .whereEqualTo("temperature", sensordata.temperature)
                .whereEqualTo("humidity", sensordata.humidity)
                .whereEqualTo("timestamp", sensordata.timestamp)
                .get()
                .await()
            if (query.documents.isNotEmpty()) {
                for (document in query) {
                    try {
                        sensordataCollectionRef.document(document.id).delete().await()
                    } catch (e: Exception) {
                        Log.i(">>>","Error deleting data $e")
                    }
                }
            } else {
                Log.i(">>>","No matching entry")
            }
        }
    }

    private fun subscribeToRealtimeUpdates() {
        sensordataCollectionRef.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            firebaseFirestoreException?.let {
                Log.i(">>>", "Realtime Update Error: $it")
                return@addSnapshotListener
            }
            querySnapshot?.let {
                val dataList = mutableListOf<Sensordata>()
                for(document in querySnapshot.documents) {
                    val data = document.toObject(Sensordata::class.java)
                    dataList.add(data!!)
                }
                _sensordataList.value = dataList

            }
        }
    }

}





