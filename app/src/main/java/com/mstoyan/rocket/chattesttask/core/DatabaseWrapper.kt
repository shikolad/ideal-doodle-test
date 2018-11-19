package com.mstoyan.rocket.chattesttask.core

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class DatabaseWrapper {

    companion object {
        const val MESSAGES_KEY = "messages"
    }
    val database: DatabaseReference

    constructor(){
        database = FirebaseDatabase.getInstance().reference
    }

    fun saveMessage(message: Message){
        database.child(MESSAGES_KEY).push().setValue(message)
    }

    fun loadMessages(){

    }
}