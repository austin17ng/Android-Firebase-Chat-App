package com.example.mychatapp.model

import com.google.firebase.Timestamp

data class Room (
    val id: String? = null,
    val name: String? = null,
    val description: String? = null,
    val photoUrl: String? = null,
    val lastMessage: String? = null,
    val lastMessageTime: Timestamp? = null

)