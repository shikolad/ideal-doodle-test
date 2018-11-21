package com.mstoyan.rocket.chattesttask.viewholders

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.DrawableImageViewTarget
import com.bumptech.glide.request.target.Target
import com.google.firebase.storage.FirebaseStorage
import com.mstoyan.rocket.chattesttask.R
import com.mstoyan.rocket.chattesttask.core.Message
import java.net.URLDecoder

abstract class MessageHolder(v: View) : RecyclerView.ViewHolder(v) {
    abstract fun bind(msg: Message)
    abstract fun getType(): Int
}

class TextHolder(v: View) : MessageHolder(v) {
    private val textView = v.findViewById<AppCompatTextView>(R.id.content)

    override fun getType(): Int {
        return Message.TYPE_TEXT
    }

    override fun bind(msg: Message) {
        textView.text = msg.text
    }
}

class GeoHolder(v: View) : MessageHolder(v) {
    private val image = v.findViewById<AppCompatImageView>(R.id.content)

    override fun getType(): Int {
        return Message.TYPE_GEO
    }

    override fun bind(msg: Message) {
        image.setImageResource(R.drawable.album_drawable)
        if (msg.text.isNotEmpty()) {
            val url =
                "https://maps.googleapis.com/maps/api/staticmap?center=${msg.text}&zoom=12&size=400x400&key=AIzaSyD0ZhCWCD3KHUERYtLz3xMxVfkDDweeL9E"
            Glide.with(image.context).load(url).into(image)
        }
    }
}

class ImageHolder(v: View) : MessageHolder(v) {
    private val image = v.findViewById<AppCompatImageView>(R.id.content)

    override fun getType(): Int {
        return Message.TYPE_IMAGE
    }

    override fun bind(msg: Message) {
        image.setImageResource(R.drawable.album_drawable)
        if (msg.text.isNotEmpty()) {
            FirebaseStorage.getInstance().getReferenceFromUrl(URLDecoder.decode(msg.text, "UTF-8")).downloadUrl.addOnSuccessListener {
                Glide.with(image.context)
                    .load(it)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            return true
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
//                            itemView.forceLayout()
                            return false
                        }
                    })
                    .into(image)
            }.addOnFailureListener{
                Log.d("Nope", it.toString())
            }
        }
    }
}