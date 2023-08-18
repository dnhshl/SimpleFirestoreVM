package com.example.simplefirestorevm.model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.simplefirestorevm.firestore.Sensordata
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Locale

class FirestoreViewModel : ViewModel() {

    // Referenz auf die Firestore DB
    private val db = Firebase.firestore

    // Keep current user and collection up to date
    private val auth = Firebase.auth
    private lateinit var sensordataCollectionRef: CollectionReference
    private val authStateListener = FirebaseAuth.AuthStateListener { auth ->
        val uid = auth.currentUser?.uid ?: "no_user"
        sensordataCollectionRef =
            db.collection("users")
                 .document(uid)
                 .collection("Sensordata")
    }
    init {
        auth.addAuthStateListener(authStateListener)
    }

    fun writeDataToFirestore(sensordata: Sensordata) =
        CoroutineScope(Dispatchers.IO).launch {
            try { sensordataCollectionRef.add(sensordata).await() }
            catch(e: Exception) { Log.i(">>>", "Error writing Data: $e") }
        }
}





