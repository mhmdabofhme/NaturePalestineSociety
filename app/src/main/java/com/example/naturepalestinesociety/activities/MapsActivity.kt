package com.example.naturepalestinesociety.activities

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.naturepalestinesociety.R
import com.example.naturepalestinesociety.databinding.ActivityMapsBinding
import com.example.naturepalestinesociety.helpers.*
import com.example.naturepalestinesociety.helpers.isLocationEnabled
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener,
    ConnectivityReceiver.ConnectivityReceiverListener, LocationSource.OnLocationChangedListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    private var lat = ""
    private var lng = ""

    private var myLocation: LatLng? = null
    private lateinit var myLastLocation: Location
    private var locationManager: LocationManager? = null
    private var isConnected = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setFullScreen(window)
        setLightStatusBar(window, true)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?
        isConnected = getInternetConnection()


        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        binding.imgLocation.setOnClickListener {
            getLocation()
            if (myLocation != null) {
                mMap.addMarker(
                    MarkerOptions()
                        .position(myLocation!!)
                )
            }
        }

        binding.btnSave.setOnClickListener {
            myLocation = mMap.cameraPosition.target
            lat = myLocation!!.latitude.toString()
            lng = myLocation!!.longitude.toString()
            SharedPreferencesApp.getInstance(this).saveLatitude(lat)
            SharedPreferencesApp.getInstance(this).saveLongitude(lng)

            val intent = Intent()
            intent.putExtra(Constants.LAT, lat)
            intent.putExtra(Constants.LNG, lng)
            setResult(Constants.RESULT_CODE_GET_LOCATION, intent)
            super.onBackPressed()
        }


    }

    private var locationListenerGPS: android.location.LocationListener =
        android.location.LocationListener { location ->
            val msg = ("" + location.latitude + "," + location.longitude)
            Toast.makeText(this@MapsActivity, msg, Toast.LENGTH_LONG).show()
            Log.d(ContentValues.TAG, "onLocationChanged:msg $msg")
            Log.d(ContentValues.TAG, "onLocationChanged:accuracy ${location.accuracy}")
            myLocation = LatLng(location.latitude, location.longitude)

            val cameraPosition = CameraPosition.Builder()
                .target(myLocation!!)
                .zoom(10f)
                .build()
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        }


    @SuppressLint("MissingPermission", "PotentialBehaviorOverride")
    private fun getLocation() {
        Log.d(TAG, "getLocation: ")
        if (checkLocationPermissions()) {
            if (isLocationEnabled()) {
                Log.d(TAG, "getLocation isConnected: $isConnected")
                if (isConnected) {
                    val task = mFusedLocationClient.lastLocation
                    task.addOnSuccessListener { location ->
                        if (location != null) {
                            myLastLocation = location

                            myLocation = LatLng(myLastLocation.latitude, myLastLocation.longitude)

                            val cameraPosition = CameraPosition.Builder()
                                .target(myLocation!!)
                                .zoom(10F)
                                .build()
                            mMap.animateCamera(
                                CameraUpdateFactory.newCameraPosition(
                                    cameraPosition
                                )
                            )
                            mMap.setOnMarkerClickListener(this)
                        }
                    }.addOnFailureListener {
                        Toast.makeText(
                            applicationContext,
                            it.message.toString(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                } else {
                    locationManager?.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        2000L,
                        10F,
                        locationListenerGPS
                    )
                }
            } else {
                Toast.makeText(this, "Please turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestLocationPermissions()
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        getLocation()
        if (myLocation != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation!!))
            val cameraPosition = CameraPosition.Builder()
                .target(myLocation!!)
                .zoom(10F)
                .build()
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
            getLocation()
        }

    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            Permissions.permissionLocationId -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    getLocation()
                }
            }
        }
    }

    override fun onMarkerClick(p0: Marker): Boolean {
        addCircle(mMap, p0.position)
        val cameraPosition = CameraPosition.Builder()
            .target(myLocation!!)
            .zoom(15f)
            .build()
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        return true
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        this.isConnected = isConnected
        if (isConnected) {
            Toast.makeText(this, "Internet connected", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "Unable to connect to the internet", Toast.LENGTH_LONG).show()
        }
    }

    override fun onLocationChanged(p0: Location) {
        myLocation = LatLng(p0.latitude, p0.longitude)
        val cameraPosition = CameraPosition.Builder()
            .target(myLocation!!)
            .zoom(10F)
            .build()
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

}

