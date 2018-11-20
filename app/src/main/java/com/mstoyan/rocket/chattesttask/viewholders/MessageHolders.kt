package com.mstoyan.rocket.chattesttask.viewholders

import android.graphics.drawable.Drawable
import android.net.Uri
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
    private val textView = v as AppCompatTextView

    override fun getType(): Int {
        return Message.TYPE_TEXT
    }

    override fun bind(msg: Message) {
        textView.text = msg.text
    }
}

class GeoHolder(v: View) : MessageHolder(v) {
    private val image = v as AppCompatImageView

    override fun getType(): Int {
        return Message.TYPE_GEO
    }

    override fun bind(msg: Message) {
        Glide.with(image.context).load(msg.text).into(image)
    }
}

class ImageHolder(v: View) : MessageHolder(v) {
    private val image = v as AppCompatImageView

    override fun getType(): Int {
        return Message.TYPE_IMAGE
    }

    override fun bind(msg: Message) {
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
                            (target as DrawableImageViewTarget).view.setImageResource(R.drawable.ic_launcher_background)
                            return true
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
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