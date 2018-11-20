package com.mstoyan.rocket.chattesttask.core

class Message {
    var id: Int = 0
    var text: String = ""
    var msgType: Int = 0

    constructor(){}
    constructor(id: Int, text: String, msgType: Int){
        this.id = id
        this.text = text
        this.msgType = msgType
    }

    companion object {
        const val TYPE_IMAGE: Int = 1
        const val TYPE_GEO: Int = 2
        const val TYPE_TEXT: Int = 3
    }
}