package com.example.mychatapp.model

import com.google.firebase.Timestamp

data class Message(
    val uid: String? = null,
    val name: String? = null,
    val photoUrl: String? = null,
    val text: String? = null,
    val imageUrl: String? = null,
    val sendTimestamp: Timestamp? = null,
)
