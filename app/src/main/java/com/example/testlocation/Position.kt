package com.example.testlocation

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.provider.Settings
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng

import android.os.Bundle
import com.google.android.gms.common.ConnectionResult


// MainActivity needs the following interfaces:
//      GoogleApiClient.ConnectionCallbacks,
//      GoogleApiClient.OnConnectionFailedListener,
//      com.google.android.gms.location.LocationListener

// Manifest needs the following permissions:
//      <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
//      <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />


class Position (val main: MainActivity) {

    companion object {
        private val TAG = "Position"
    }

    private var mLatitudeTextView: TextView? = null
    private var mLongitudeTextView: TextView? = null
    private var mGoogleApiClient: GoogleApiClient? = null
    private var mLocation: Location? = null
    private var mLocationManager: LocationManager? = null

    private var mLocationRequest: LocationRequest? = null
    private val listener: com.google.android.gms.location.LocationListener? = null
    private val UPDATE_INTERVAL = (2 * 1000).toLong()  /* 10 secs */
    private val FASTEST_INTERVAL: Long = 2000 /* 2 sec */

    private var locationManager: LocationManager? = null

    private val isLocationEnabled: Boolean
        get() {
            locationManager = main.getSystemService(LOCATION_SERVICE) as LocationManager
            return locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager!!.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
            )
        }

    @SuppressLint("MissingPermission")
    protected fun startLocationUpdates() {
        // Create the location request
        mLocationRequest = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(UPDATE_INTERVAL)
            .setFastestInterval(FASTEST_INTERVAL)
        // Request location updates
        if (ActivityCompat.checkSelfPermission(
                main,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                main,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
            mGoogleApiClient,
            mLocationRequest, main
        )
        Log.d("reque", "--->>>>")
    }

    private fun checkLocation(): Boolean {
        if (!isLocationEnabled)
            showAlert()
        return isLocationEnabled
    }

    private fun showAlert() {
        val dialog = AlertDialog.Builder(main)
        dialog.setTitle("Enable Location")
            .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to " + "use this app")
            .setPositiveButton("Location Settings") { paramDialogInterface, paramInt ->
                val myIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                main.startActivity(myIntent)
            }
            .setNegativeButton("Cancel") { paramDialogInterface, paramInt -> }
        dialog.show()
    }


    fun onCreate() {

        mLatitudeTextView = main.findViewById(R.id.latitude_textview) as TextView
        mLongitudeTextView = main.findViewById(R.id.longitude_textview) as TextView

        mGoogleApiClient = GoogleApiClient.Builder(main)
            .addConnectionCallbacks(main)
            .addOnConnectionFailedListener(main)
            .addApi(LocationServices.API)
            .build()

        mLocationManager = main.getSystemService(LOCATION_SERVICE) as LocationManager

        Log.d("gggg", "uooo");
        checkLocation() //check whether location service is enable or not in your  phone
    }

    fun onConnected() {

        if (ActivityCompat.checkSelfPermission(
                main,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                main,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }

        startLocationUpdates()

        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient)

        if (mLocation == null) {
            startLocationUpdates()
        }
        if (mLocation != null) {

            Log.d("MLOCATION", mLocation.toString())

            // mLatitudeTextView.setText(String.valueOf(mLocation.getLatitude()));
            //mLongitudeTextView.setText(String.valueOf(mLocation.getLongitude()));
        } else {
            Toast.makeText(main, "Location not Detected", Toast.LENGTH_SHORT).show()
        }
    }

    fun onConnectionSuspended() {
        mGoogleApiClient!!.connect()
    }

    fun onConnectionFailed(connectionResult: ConnectionResult) {
        Log.i(TAG, "Connection failed. Error: " + connectionResult.getErrorCode())

    }

    fun onStart() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient!!.connect()
        }

    }

    fun onStop() {
        if (mGoogleApiClient!!.isConnected()) {
            mGoogleApiClient!!.disconnect()
        }
    }

    fun onLocationChanged(location: Location) {

        Log.d("LocationChanged", "OK")


        val msg = "Updated Location: " +
                java.lang.Double.toString(location.latitude) + "," +
                java.lang.Double.toString(location.longitude)
        mLatitudeTextView!!.text = location.latitude.toString()
        mLongitudeTextView!!.text = location.longitude.toString()
        Toast.makeText(main, msg, Toast.LENGTH_SHORT).show()
        // You can now create a LatLng Object for use with maps
        val latLng = LatLng(location.latitude, location.longitude)
    }

}





