package com.mstoyan.rocket.chattesttask.core.auth

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.mstoyan.rocket.chattesttask.R

class UserAuth(activity: Activity) {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var firebaseUser: FirebaseUser? = null
    private var googleApiClient: GoogleApiClient? = null

    val uid: String
        get() = firebaseUser!!.uid

    fun getFirebaseUser(): FirebaseUser?{
        return firebaseUser
    }

    fun getSignInIntent(): Intent {
        return Auth.GoogleSignInApi.getSignInIntent(googleApiClient)
    }

    fun proceedUserAuth(data: Intent?, activity: Activity): Boolean{
        val result: GoogleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data)

        if (result.isSuccess){
            val signInAccount: GoogleSignInAccount = result.signInAccount!!
            val credential: AuthCredential = GoogleAuthProvider.getCredential(signInAccount.idToken, null)
            FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Toast.makeText(
                            activity, "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        firebaseUser = firebaseAuth.currentUser
                        Toast.makeText(activity, "Authenticated!",
                            Toast.LENGTH_SHORT).show()
                    }
                }
            Toast.makeText(activity, "Logined", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(activity, "Please login using google account", Toast.LENGTH_SHORT).show()
        }
        return result.isSuccess
    }

    init {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(activity.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleApiClient = GoogleApiClient.Builder(activity)
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .build()
        firebaseUser = firebaseAuth.currentUser
    }
}