package com.example.naturepalestinesociety.helpers

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log
import android.widget.Toast
import androidx.annotation.RawRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.naturepalestinesociety.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.heatmaps.Gradient
import com.google.maps.android.heatmaps.HeatmapTileProvider
import org.json.JSONArray
import org.json.JSONException
import java.io.IOException
import java.util.*


fun Context.getInternetConnection(): Boolean {
    val connectivityManager =
        getSystemService(AppCompatActivity.CONNECTIVITY_SERVICE) as ConnectivityManager

    return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)!!.state == NetworkInfo.State.CONNECTED ||
            connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)!!.state == NetworkInfo.State.CONNECTED
}

fun Context.isLocationEnabled(): Boolean {
    val locationManager: LocationManager =
        getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
        LocationManager.NETWORK_PROVIDER
    )
}

fun Context.getAddress(lat: Double, lng: Double): String {
    val default = "No landmark identified"
    try {
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses: List<Address> = geocoder.getFromLocation(
            lat,
            lng,
            1
        )
        return if (addresses.isNotEmpty()) {
            Log.d(TAG, "getAddress address : ${addresses[0].getAddressLine(0)}")
            Log.d(TAG, "getAddress city : ${addresses[0].locality}")
            Log.d(TAG, "getAddress state  : ${addresses[0].adminArea}")
            Log.d(TAG, "getAddress country  : ${addresses[0].countryName}")
            Log.d(TAG, "getAddress postalCode : ${addresses[0].postalCode}")
            Log.d(TAG, "getAddress knownName : ${addresses[0].featureName}")
            val obj: Address = addresses[0]
            obj.getAddressLine(0)
        } else {
            default
        }
    } catch (e: IOException) {
        e.printStackTrace()
        Toast.makeText(
            this,
            "No internet connection Please connect to the internet",
            Toast.LENGTH_SHORT
        ).show()
        return default
    }
}

fun Context.bitmapDescriptorFromVector(vectorResId: Int): BitmapDescriptor? {
    return ContextCompat.getDrawable(this, vectorResId)?.run {
        setBounds(0, 0, intrinsicWidth, intrinsicHeight)
        val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
        draw(Canvas(bitmap))
        BitmapDescriptorFactory.fromBitmap(bitmap)
    }
}

fun Context.generateSmallIcon(drawable: Int): Bitmap {
    val height = 75
    val width = 75
    val bitmap = BitmapFactory.decodeResource(resources, drawable)
    return Bitmap.createScaledBitmap(bitmap, width, height, false)
}

@SuppressLint("PotentialBehaviorOverride")
fun Context.setUpCluster(mMap: GoogleMap, location: Location) {
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(51.503186, -0.126446), 10f))
    mMap.moveCamera(
        CameraUpdateFactory.newLatLngZoom(
            LatLng(location.latitude, location.longitude),
            10f
        )
    )
    val clusterManager = ClusterManager<MyItem>(this, mMap)
    mMap.setOnCameraIdleListener(clusterManager)
    mMap.setOnMarkerClickListener(clusterManager)
//        clusterManager.setOnClusterItemClickListener { item ->
//            addCircle(mMap, item)
//            return@setOnClusterItemClickListener false
//        }
    addItems(clusterManager)
}

fun addItems(clusterManager: ClusterManager<MyItem>) {
    var lat = 51.5145160
    var lng = -0.1270060

    for (i in 0..9) {
        val offset = i / 60.0
        lat += offset
        lng += offset
        val offsetItem =
            MyItem(lat, lng, "Title $i", "Snippet $i")
        clusterManager.addItem(offsetItem)
    }
}


fun Context.addHeatMap(mMap: GoogleMap, list: List<LatLng>) {
    var latLngs: List<LatLng?>? = null
    // Get the data: latitude/longitude positions of police stations.
    try {
        latLngs = list
//            latLngs = readItems(R.raw.police_stations)
    } catch (e: JSONException) {
        Toast.makeText(this, "Problem reading list of locations.", Toast.LENGTH_LONG)
            .show()
    }

    // Create a heat map tile provider, passing it the latlngs of the police stations.
    val provider = HeatmapTileProvider.Builder()
        .data(latLngs)
        .build()

    // Add a tile overlay to the map, using the heat map tile provider.
    val overlay = mMap.addTileOverlay(TileOverlayOptions().tileProvider(provider))

// Create the gradient.
    val colors = intArrayOf(
        Color.rgb(102, 225, 0),  // green
        Color.rgb(255, 0, 0) // red
    )
    val startPoints = floatArrayOf(0.2f, 1f)
    val gradient = Gradient(colors, startPoints)

// Create the tile provider.
    val provider2 = HeatmapTileProvider.Builder()
        .data(latLngs)
        .gradient(gradient)
        .build()


// Add the tile overlay to the map.
    val tileOverlay = mMap.addTileOverlay(
        TileOverlayOptions()
            .tileProvider(provider2)
    )
//        val data: List<WeightedLatLng> = ArrayList()
//        provider.setWeightedData(data)
//        overlay?.clearTileCache()
//        overlay?.remove()
}


@Throws(JSONException::class)
fun Context.readItems(@RawRes resource: Int): List<LatLng?> {
    val result: MutableList<LatLng?> = ArrayList()
    val inputStream = resources.openRawResource(resource)
    val json = Scanner(inputStream).useDelimiter("\\A").next()
    val array = JSONArray(json)
    for (i in 0 until array.length()) {
        val `object` = array.getJSONObject(i)
        val lat = `object`.getDouble("lat")
        val lng = `object`.getDouble("lng")
        result.add(LatLng(lat, lng))
    }
    return result
}


@SuppressLint("UseCompatLoadingForDrawables")
fun Context.resizeMarker(drawable: Int): Bitmap? {
    val bitmapDrawable = resources.getDrawable(drawable) as BitmapDrawable
    val bitmap = bitmapDrawable.bitmap
    // Change expectedWidth's value to your desired one.
    val expectedWidth = 60
    return Bitmap.createScaledBitmap(
        bitmap,
        expectedWidth,
        bitmap.height * expectedWidth / bitmap.width,
        false
    )
}

fun Context.addCircle(mMap: GoogleMap, location: LatLng) {
    var circle: Circle? = null

    circle?.remove()
    circle = mMap.addCircle(
        CircleOptions()
            .center(location)
            .radius(15.0)
            .strokeWidth(7F)
            .fillColor(ContextCompat.getColor(this, R.color.tr_blue))
            .strokeColor(ContextCompat.getColor(this, R.color.blue))
    )

//    mMap.moveCamera(CameraUpdateFactory.newLatLng(location))
    val cameraPosition = CameraPosition.Builder()
        .target(location)  // Sets the center of the map to Mountain View
        .zoom(19F) // Sets the zoom
        .build()              // Creates a CameraPosition from the builder
    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
}
