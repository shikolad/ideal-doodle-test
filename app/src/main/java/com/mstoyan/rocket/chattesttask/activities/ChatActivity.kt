package com.mstoyan.rocket.chattesttask.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import com.mstoyan.rocket.chattesttask.R
import com.mstoyan.rocket.chattesttask.core.DatabaseWrapper
import com.mstoyan.rocket.chattesttask.core.Message
import com.mstoyan.rocket.chattesttask.core.auth.UserAuth
import kotlinx.android.synthetic.main.activity_chat.*

class ChatActivity : AppCompatActivity() {

    private var userAuth: UserAuth? = null
    private val RC_SIGN_IN = 9001
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

        }

        fabCamera.setOnClickListener{

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
            databaseWrapper = DatabaseWrapper()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (RC_SIGN_IN == requestCode){
            if (userAuth!!.proceedUserAuth(data, this)){
                databaseWrapper = DatabaseWrapper()
            } else {
                finish()
            }
        }
    }
}
