package com.mstoyan.rocket.chattesttask.core

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.lang.Exception
import java.lang.IllegalStateException

class LocationTracker: LocationListener{

    companion object {
        // The minimum distance to change Updates in meters
        private val MIN_DISTANCE_CHANGE_FOR_UPDATES: Float = 1f // 10 meters

        // The minimum time between updates in milliseconds
        private val MIN_TIME_BW_UPDATES: Long = 1 // 1 minute
        private val MIN_TIME_BW_UPDATES_MILLIS: Long = MIN_TIME_BW_UPDATES * 60 * 1000
    }

    var locationManager: LocationManager? = null
    var lastLocation: Location? = null

    fun init(context: Context){
        try {
            locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

            @SuppressLint("MissingPermission")
            fun requestUpdates(provider: String){
                locationManager!!.requestLocationUpdates(provider, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES,
                    this)
                val location = locationManager!!.getLastKnownLocation(provider)
                onLocationChanged(location)
            }

            if (locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
                requestUpdates(LocationManager.NETWORK_PROVIDER)
            }

            if (locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                requestUpdates(LocationManager.GPS_PROVIDER)
            }
        } catch (e: Exception){

        }
    }

    fun permissionGranted(context: Context): Boolean{
        return ContextCompat.checkSelfPermission(context,android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context,android.Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED
    }

    fun shouldShowRationale(activity: Activity): Boolean{
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, android.Manifest.permission.ACCESS_COARSE_LOCATION)
            || ActivityCompat.shouldShowRequestPermissionRationale(activity, android.Manifest.permission.ACCESS_FINE_LOCATION)
    }

    fun stopTrackingLocation(){
        locationManager!!.removeUpdates(this)
    }

    override fun onLocationChanged(location: Location?) {
        if (lastLocation == null ||
            location!!.accuracy <= lastLocation!!.accuracy ||
            location.time.div(lastLocation!!.time) > MIN_TIME_BW_UPDATES_MILLIS){
            lastLocation = location
        }
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        //do nothing
    }

    override fun onProviderEnabled(provider: String?) {
        //do nothing
    }

    override fun onProviderDisabled(provider: String?) {
        //do nothing
    }
}