package com.mstoyan.rocket.chattesttask.viewholders

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import com.mstoyan.rocket.chattesttask.core.Message

abstract class MessageHolder(v: View) : RecyclerView.ViewHolder(v) {
    abstract fun bind()
    abstract fun getType(): Int
}

class TextHolder(v: View) : MessageHolder(v) {
    override fun getType(): Int {
        return Message.TYPE_TEXT
    }

    override fun bind() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

class GeoHolder(v: View) : MessageHolder(v) {
    override fun getType(): Int {
        return Message.TYPE_GEO
    }

    override fun bind() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

class ImageHolder(v: View) : MessageHolder(v) {
    override fun getType(): Int {
        return Message.TYPE_IMAGE
    }

    override fun bind() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}