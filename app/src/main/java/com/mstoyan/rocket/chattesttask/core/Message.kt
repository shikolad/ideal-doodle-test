package com.mstoyan.rocket.chattesttask.core

import android.location.Location

class Message {
    var id: Int = 0
    var text: String = ""
    var msgType: Int = 0

    constructor(){}
    constructor(text: String, msgType: Int){
        this.text = text
        this.msgType = msgType
    }
    constructor(location: Location){
        text = "" + location.latitude + "," + location.longitude
        msgType = TYPE_GEO
    }

    companion object {
        const val TYPE_IMAGE: Int = 1
        const val TYPE_GEO: Int = 2
        const val TYPE_TEXT: Int = 3
    }
}