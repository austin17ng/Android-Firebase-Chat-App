package com.example.mychatapp

import android.app.Application
import com.example.mychatapp.model.User

class ChatApplication : Application() {
    var me: User? = null
        get() = field
        set(value) {
            field = value
        }
}