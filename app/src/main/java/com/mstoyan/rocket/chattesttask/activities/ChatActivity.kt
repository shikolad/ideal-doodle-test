package com.mstoyan.rocket.chattesttask.activities

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.mstoyan.rocket.chattesttask.R
import com.mstoyan.rocket.chattesttask.adapters.MessageAdapter
import com.mstoyan.rocket.chattesttask.core.DatabaseWrapper
import com.mstoyan.rocket.chattesttask.core.Message
import com.mstoyan.rocket.chattesttask.core.auth.UserAuth
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.content_chat.*


class ChatActivity : AppCompatActivity() {

    companion object {
        private const val RC_SIGN_IN = 9001
        private const val RC_CAMERA = 9002
        private const val RC_GALLERY = 9003
    }

    private var userAuth: UserAuth? = null
    private var databaseWrapper: DatabaseWrapper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        setSupportActionBar(toolbar)

        fabPlus.setOnClickListener{
            if (fabAlbum.visibility == View.VISIBLE){
                fabAlbum.hide()
                fabCamera.hide()
                fabGeo.hide()
                fabMessage.hide()
            } else {
                fabAlbum.show()
                fabCamera.show()
                fabGeo.show()
                fabMessage.show()
            }
        }

        fabAlbum.setOnClickListener{
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"
            startActivityForResult(intent, RC_GALLERY)
        }

        fabCamera.setOnClickListener{
            val cameraIntent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, RC_CAMERA)
        }

        fabGeo.setOnClickListener{
            databaseWrapper!!.saveMessage(Message(0, "", Message.TYPE_GEO))
        }

        fabMessage.setOnClickListener {
            databaseWrapper!!.saveMessage(Message(0, "test", Message.TYPE_TEXT))
        }

        userAuth = UserAuth(this)
        if (userAuth!!.getFirebaseUser() == null){
            startActivityForResult(userAuth!!.getSignInIntent(), RC_SIGN_IN)
        } else {
            initDatabase()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (RC_SIGN_IN == requestCode){
        }
        when (requestCode){
            RC_SIGN_IN -> {
                if (userAuth!!.proceedUserAuth(data, this)){
                    initDatabase()
                } else {
                    finish()
                }
            }
            RC_CAMERA -> {
                val bmp = data!!.getExtras()!!.get("data") as Bitmap
                databaseWrapper!!.saveImage(bmp, userAuth!!.uid, this)
            }
            RC_GALLERY -> {
                if (RESULT_OK == resultCode){
                    val uri = data!!.data
                    if (uri != null)
                        databaseWrapper!!.saveImage(uri, userAuth!!.uid, this)
                }
            }
        }
    }

    private fun initDatabase() {
        databaseWrapper = DatabaseWrapper()
        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true
        messageList.layoutManager = layoutManager
        messageList.adapter = MessageAdapter(databaseWrapper!!.databaseReference.child(DatabaseWrapper.MESSAGES_KEY))
    }
}
