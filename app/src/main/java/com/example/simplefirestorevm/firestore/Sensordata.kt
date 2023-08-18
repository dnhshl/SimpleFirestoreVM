package com.example.simplefirestorevm.firestore

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ServerTimestamp

class Sensordata (
    var location: String = "",
    var temperature: Int = 0,
    var humidity: Int = 0,
    var timestamp: Timestamp = Timestamp.now(),
    @ServerTimestamp
    var created_at: Timestamp? = null
) {
    override fun toString(): String {
        val datestring = convertTimestampToDateTimeString(timestamp)
        return "$location T: $temperature °C, H: $humidity %, $datestring"
    }
}
