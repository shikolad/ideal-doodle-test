package com.mstoyan.rocket.chattesttask.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.mstoyan.rocket.chattesttask.R
import com.mstoyan.rocket.chattesttask.adapters.MessageAdapter
import com.mstoyan.rocket.chattesttask.core.DatabaseWrapper
import com.mstoyan.rocket.chattesttask.core.LocationTracker
import com.mstoyan.rocket.chattesttask.core.Message
import com.mstoyan.rocket.chattesttask.core.auth.UserAuth
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.content_chat.*


class ChatActivity : AppCompatActivity() {

    companion object {
        private const val RC_SIGN_IN = 9001
        private const val RC_CAMERA = 9002
        private const val RC_GALLERY = 9003

        private const val PERMISSION_LOCATION = 10000
    }

    private var userAuth: UserAuth? = null
    private var databaseWrapper: DatabaseWrapper? = null
    private var messagesLayoutManager: LinearLayoutManager? = null
    private val locationTracker = LocationTracker()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val clickListener = View.OnClickListener{
            if (fabAlbum.visibility == View.VISIBLE){
                hideInterface()
            } else {
                fabAlbum.show()
                fabCamera.show()
                fabGeo.show()
                fabMessage.show()
                album_tip.visibility = View.VISIBLE
                cam_tip.visibility = View.VISIBLE
                geo_tip.visibility = View.VISIBLE
                msg_tip.visibility = View.VISIBLE
                transparent.startAnimation(getAnimation(0f, 1f))
            }
        }

        fabPlus.setOnClickListener(clickListener)

        transparent.setOnClickListener(clickListener)

        fabAlbum.setOnClickListener{
            hideInterface()
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"
            startActivityForResult(intent, RC_GALLERY)
        }

        fabCamera.setOnClickListener{
            hideInterface()
            val cameraIntent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, RC_CAMERA)
        }

        btnSend.setOnClickListener{
            val message = textInput.text.toString()
            if (message.isNotEmpty()) {
                databaseWrapper!!.saveMessage(Message(message, Message.TYPE_TEXT))
                hideTextInput()
            }
        }

        fabGeo.setOnClickListener{
            sendGeo()
        }

        fabMessage.setOnClickListener {
            hideInterface()
            btnSend.visibility = View.VISIBLE
            textInput.visibility = View.VISIBLE
            textInput.requestFocus()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
            fabPlus.hide()
        }

        textInput.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                btnSend.setTextColor(
                    ContextCompat.getColor(this@ChatActivity,
                        if (s.toString().isEmpty())
                            R.color.light_gray
                        else R.color.colorAccent)
                )
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //do nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //do nothing
            }

        })

        userAuth = UserAuth(this)
        if (userAuth!!.getFirebaseUser() == null){
            startActivityForResult(userAuth!!.getSignInIntent(), RC_SIGN_IN)
        } else {
            initDatabase()
        }
    }

    private fun sendGeo() {
        hideInterface()
        if (locationTracker.permissionGranted(this)) {
            val location = locationTracker.lastLocation
            if (location == null) {
                Toast.makeText(this, "No location received yet! Please try later!", Toast.LENGTH_SHORT).show()
            } else {
                databaseWrapper!!.saveLocation(location)
            }
        } else {
            if (locationTracker.shouldShowRationale(this)) {
                Toast.makeText(
                    this, "Please grant location permission in settings",
                    Toast.LENGTH_SHORT
                ).show()
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ), PERMISSION_LOCATION
                )
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ), PERMISSION_LOCATION
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (locationTracker.permissionGranted(this))
            locationTracker.init(this)
    }

    override fun onPause() {
        super.onPause()
        if (locationTracker.permissionGranted(this))
            locationTracker.stopTrackingLocation()
    }

    private fun hideTextInput() {
        val imm = getSystemService(
            Context.INPUT_METHOD_SERVICE
        ) as InputMethodManager
        imm.hideSoftInputFromWindow(textInput.windowToken, 0)
        textInput.visibility = View.INVISIBLE
        btnSend.visibility = View.INVISIBLE
        fabPlus.show()
    }

    private fun hideInterface() {
        fabAlbum.hide()
        fabCamera.hide()
        fabGeo.hide()
        fabMessage.hide()
        album_tip.visibility = View.INVISIBLE
        cam_tip.visibility = View.INVISIBLE
        geo_tip.visibility = View.INVISIBLE
        msg_tip.visibility = View.INVISIBLE
        transparent.startAnimation(getAnimation(1f, 0f))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode){
            RC_SIGN_IN -> {
                if (userAuth!!.proceedUserAuth(data, this)){
                    initDatabase()
                } else {
                    finish()
                }
            }
            RC_CAMERA -> {
                if (RESULT_OK == resultCode) {
                    val bmp = data!!.getExtras()!!.get("data") as Bitmap
                    databaseWrapper!!.saveImage(bmp, userAuth!!.uid, this)
                }
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
        messagesLayoutManager = LinearLayoutManager(this)
        messagesLayoutManager!!.stackFromEnd = true
        messageList.layoutManager = messagesLayoutManager
        messageList.adapter = MessageAdapter(databaseWrapper!!.databaseReference.child(DatabaseWrapper.MESSAGES_KEY))
    }

    private fun getAnimation(start: Float, end: Float): Animation{
        transparent.animation?.cancel()

        val result = AlphaAnimation(start, end)
        result.duration = 300
        result.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                if (start == 0f) transparent.visibility = View.VISIBLE
            }

            override fun onAnimationEnd(animation: Animation) {
                if (start == 1f) transparent.visibility = View.INVISIBLE
            }

            override fun onAnimationRepeat(animation: Animation) {

            }
        })
        return result
    }

    override fun onBackPressed() {
        if (textInput.isFocused){
            textInput.setText("")
            hideTextInput()
        } else {
            super.onBackPressed()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode){
            PERMISSION_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED ){
                    locationTracker.init(this)
                    sendGeo()
                } else {
                    Toast.makeText(
                        this, "Please grant location permission in settings",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}
