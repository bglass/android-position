package com.example.testlocation

import android.location.Location
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient

class MainActivity :
    AppCompatActivity(),
    // involve Position
    GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener,
    com.google.android.gms.location.LocationListener {

    val position = Position(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // try: https://medium.com/androiddevelopers/no-more-findviewbyid-457457644885

        position.onCreate()
    }

    // involve Position
    override fun onConnected(p0: Bundle?) = position.onConnected()
    override fun onConnectionSuspended(i: Int) = position.onConnectionSuspended()
    override fun onConnectionFailed(connectionResult: ConnectionResult) = position.onConnectionFailed(connectionResult)
    override fun onLocationChanged(location: Location) = position.onLocationChanged(location)
    override fun onStart() {super.onStart(); position.onStart()}
    override fun onStop() {super.onStop(); position.onStop()}
}