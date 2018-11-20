package com.mstoyan.rocket.chattesttask.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.FirebaseStorage
import com.mstoyan.rocket.chattesttask.R
import com.mstoyan.rocket.chattesttask.core.Message
import com.mstoyan.rocket.chattesttask.viewholders.GeoHolder
import com.mstoyan.rocket.chattesttask.viewholders.ImageHolder
import com.mstoyan.rocket.chattesttask.viewholders.MessageHolder
import com.mstoyan.rocket.chattesttask.viewholders.TextHolder

class MessageAdapter(
    ref: DatabaseReference?
) : FirebaseRecyclerAdapter<Message, MessageHolder>(Message::class.java, 0, MessageHolder::class.java, ref) {

    override fun getItemViewType(position: Int): Int {
        return getItem(position).msgType
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageHolder {
        val context = parent.context
        val layoutInflater = LayoutInflater.from(context)
        return when (viewType){
            Message.TYPE_TEXT -> {
                TextHolder(layoutInflater.inflate(R.layout.list_item_text, parent, false))
            }
            Message.TYPE_GEO -> {
                GeoHolder(layoutInflater.inflate(R.layout.list_item_image, parent, false))
            }
            Message.TYPE_IMAGE -> {
                ImageHolder(layoutInflater.inflate(R.layout.list_item_image, parent, false))
            }
            else -> {
                TextHolder(layoutInflater.inflate(R.layout.list_item_text, parent, false))
            }
        }
    }

    override fun populateViewHolder(p0: MessageHolder?, p1: Message?, p2: Int) {
        p0!!.bind(p1!!)
    }
}