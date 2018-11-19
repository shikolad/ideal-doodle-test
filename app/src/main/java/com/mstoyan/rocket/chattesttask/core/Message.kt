package com.mstoyan.rocket.chattesttask.core

data class Message (val id: Int, val text: String, val msgType: Int){
    companion object {
        const val TYPE_IMAGE: Int = 1
        const val TYPE_GEO: Int = 2
        const val TYPE_TEXT: Int = 3
    }
}