package com.example.simplefirestorevm.firestore

import com.google.firebase.firestore.Query

enum class ConditionType {
    EQUAL_TO,
    GREATER_THAN_OR_EQUAL,
    LESS_THAN_OR_EQUAL,
    // Add more conditions as needed
}

data class FilterCondition(val field: String, val type: ConditionType, val value: Any)
data class OrderCondition(val field: String, val direction: Query.Direction)