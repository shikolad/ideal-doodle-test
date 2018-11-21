package com.mstoyan.rocket.chattesttask.core

import android.app.Activity
import android.graphics.Bitmap
import android.location.Location
import android.net.Uri
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import org.joda.time.DateTime
import java.io.ByteArrayOutputStream


class DatabaseWrapper {

    companion object {
        const val MESSAGES_KEY = "messages"
    }

    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference

    val databaseReference
        get() = database

    class OnImageCompleteListener(private val key: String, private val database: DatabaseReference):
        OnCompleteListener<UploadTask.TaskSnapshot>{

        override fun onComplete(task: Task<UploadTask.TaskSnapshot>) {
            if (task.isSuccessful) {
                val message = Message(task.result!!.metadata!!.reference!!.toString(),
                    Message.TYPE_IMAGE)
                database.child(MESSAGES_KEY).child(key)
                    .setValue(message)
            } else {
                TODO("add error handling")
            }
        }
    }

    fun saveMessage(message: Message){
        database.child(MESSAGES_KEY).push().setValue(message)
    }

    fun saveImage(uri: Uri, uid: String, activity: Activity){
        val tempMessage = Message("",Message.TYPE_IMAGE)
        database.child(MESSAGES_KEY).push()
            .setValue(tempMessage
            ) { databaseError, databaseReference ->
                if (databaseError == null) {
                    val key = databaseReference.key
                    val storageReference = FirebaseStorage.getInstance()
                        .getReference(uid)
                        .child(key!!)
                        .child(uri.lastPathSegment!!)

                    storageReference.putFile(uri).addOnCompleteListener(activity, OnImageCompleteListener(key, database))
                } else {
                    TODO("add error handling")
                }
            }
    }

    fun saveImage(bmp: Bitmap, uid: String, activity: Activity){
        val tempMessage = Message("",Message.TYPE_IMAGE)
        database.child(MESSAGES_KEY).push()
            .setValue(tempMessage
            ) { databaseError, databaseReference ->
                if (databaseError == null) {
                    val key = databaseReference.key
                    val fileName = uid + DateTime.now().millis
                    val storageReference = FirebaseStorage.getInstance()
                        .getReference(uid)
                        .child(key!!)
                        .child(fileName)

                    val baos = ByteArrayOutputStream()
                    bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                    val data = baos.toByteArray()


                    storageReference.putBytes(data).addOnCompleteListener(activity, OnImageCompleteListener(key, database))
                } else {
                    TODO("add error handling")
                }
            }
    }

    fun saveLocation(location: Location){
        val tempMessage = Message(location)
        database.child(MESSAGES_KEY).push()
            .setValue(tempMessage)
    }
}