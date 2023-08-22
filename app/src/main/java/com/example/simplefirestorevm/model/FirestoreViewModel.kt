package com.example.simplefirestorevm.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplefirestorevm.firestore.ConditionType
import com.example.simplefirestorevm.firestore.FilterCondition
import com.example.simplefirestorevm.firestore.OrderCondition
import com.example.simplefirestorevm.firestore.Sensordata
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FirestoreViewModel : ViewModel() {


    private lateinit var sensordataCollectionRef: CollectionReference

    fun setSensordataCollectionRef(uid: String) {
        sensordataCollectionRef =
            Firebase.firestore.collection("users")
                .document(uid)
                .collection("Sensordata")

        subscribeToRealtimeUpdates()
    }

    fun writeDataToFirestore(sensordata: Sensordata) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                sensordataCollectionRef.add(sensordata).await()
            }
            catch(e: Exception) { Log.i(">>>", "Error writing Data: $e") }
        }
    }


    // Daten als Ergebnis einer Abfrage aus der DB
    private var _sensordataList = MutableLiveData<List<Sensordata>>()
    val sensordataList: LiveData<List<Sensordata>>
        get() = _sensordataList

    fun getFilteredData(filters: List<FilterCondition> = listOf(),
                        orders: List<OrderCondition> = listOf(),
                        limit: Int? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                var query: Query = sensordataCollectionRef
                // Apply filters to the query if provided
                filters.forEach { filter ->
                    query = when (filter.type) {
                        ConditionType.EQUAL_TO -> query.whereEqualTo(filter.field, filter.value)
                        ConditionType.GREATER_THAN_OR_EQUAL -> query.whereGreaterThanOrEqualTo(filter.field, filter.value)
                        ConditionType.LESS_THAN_OR_EQUAL -> query.whereLessThanOrEqualTo(filter.field, filter.value)
                    }
                }
                // Apply Order Conditions if provided
                orders.forEach { order ->
                    query = query.orderBy(order.field, order.direction)
                }
                // Apply limit if provided
                limit?.let {
                    query = query.limit(it.toLong())
                }
                // Do the query
                val querySnapshot = query.get().await()
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
        viewModelScope.launch(Dispatchers.IO) {
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