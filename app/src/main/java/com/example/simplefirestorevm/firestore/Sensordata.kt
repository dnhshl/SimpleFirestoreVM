package com.example.simplefirestorevm.firestore

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue

class Sensordata (
    var location: String = "",
    var temperature: Int = 0,
    var humidity: Int = 0,
    var timestamp: Timestamp = Timestamp.now(),
    var created_at: FieldValue = FieldValue.serverTimestamp()
) {
    override fun toString(): String {
        val datestring = convertTimestampToDateTimeString(timestamp)
        return "$location T: $temperature °C, H: $humidity %, $datestring"
    }
}
