package com.example.simplefirestorevm.firestore

import com.google.firebase.Timestamp

class Sensordata (
    var location: String = "",
    var temperature: Int = 0,
    var humidity: Int = 0,
    var timestamp: Timestamp = Timestamp.now()
) {
    override fun toString(): String {
        val datestring = convertTimestampToDateTimeString(timestamp)
        return "$location T: $temperature °C, H: $humidity %, $datestring"
    }
}
