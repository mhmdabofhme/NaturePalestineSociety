package com.example.naturepalestinesociety.helpers

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

class Permissions {

    companion object {
         const val permissionLocationId = 100
         const val permissionCameraId = 101
         const val permissionStorageId = 102
    }

}



 fun Context.checkLocationPermissions(): Boolean {
    if (ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
        ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        return true
    }
    return false
}

 fun Activity.requestLocationPermissions() {
    ActivityCompat.requestPermissions(
        this,
        arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        ),
        Permissions.permissionLocationId
    )
}


 fun Context.checkCameraPermissions(): Boolean {
    if (ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        return true
    }
    return false
}

 fun Activity.requestCameraPermissions() {
    ActivityCompat.requestPermissions(
        this,
        arrayOf(
            Manifest.permission.CAMERA
        ),
        Permissions.permissionCameraId
    )
}


fun Context.checkStoragePermissions(): Boolean {
    if (ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        return true
    }
    return false
}

fun Activity.requestStoragePermissions() {
    ActivityCompat.requestPermissions(
        this,
        arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE
        ),
        Permissions.permissionStorageId
    )
}


