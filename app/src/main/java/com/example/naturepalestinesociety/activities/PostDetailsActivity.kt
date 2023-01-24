package com.example.naturepalestinesociety.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.naturepalestinesociety.R
import com.example.naturepalestinesociety.databinding.ActivityPostDetailsBinding
import com.example.naturepalestinesociety.helpers.*
import com.example.naturepalestinesociety.models.post.Post
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.util.*

class PostDetailsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityPostDetailsBinding
    private lateinit var mMap: GoogleMap

    private lateinit var data: Post
    private var lat = 0.0
    private var lng = 0.0

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setFullScreen(window)
        setLightStatusBar(window, true)
        binding.imgBack.setOnClickListener { finish() }

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        data = intent.getSerializableExtra(Constants.POST) as Post

        binding.txtUsername.text = data.user.name
        binding.txtPostTitle.text = data.title
        binding.imgUser.loadImage(data.user.photo)
        binding.imgPost.loadImage(data.media[data.media.size-1].file_name)
        binding.txtProject.text = data.project.name
        binding.txtSpecies.text = data.project.name
        binding.txtAddress.text = getAddress(data.lat, data.lang)


        binding.imgPost.setOnClickListener {
            focusImage(data.media[data.media.size-1].file_name)
        }

        binding.txtPostTitle.text = data.title
        if (data.user.feature == 1) {
            binding.txtVerified.visibility = View.VISIBLE
        } else {
            binding.txtVerified.visibility = View.GONE
        }

        lat = data.lat
        lng = data.lang

        val postDate = data.created_at.apiFormat()


        binding.txtDate.text = postDate.toTimeFormated()

        binding.txtTimeAgo.text = findDifference(
            postDate.toDateTimeFormated(),
            Calendar.getInstance().time.toDateTimeFormated()
        ) + " " + getString(R.string.ago)


        binding.imgUser.setOnClickListener {
            val id = SharedPreferencesApp.getInstance(this)
                .getText(Constants.USER_ID, "0").toString()
            if (data.user.id.toString() != id) {
                val intent = Intent(this, ProfileActivity::class.java)
                intent.putExtra(Constants.USER, data.user)
                startActivity(intent)
            } else {
                startMain()
            }

        }


    }


    private fun startMain() {
        var intent = Intent(this, MainActivity::class.java)
        intent.putExtra(Constants.START_HOME, "Profile")
        startActivity(intent)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.setAllGesturesEnabled(false)
        val userId = SharedPreferencesApp.getInstance(this)
            .getText(Constants.USER_ID, "0").toString()

        if (data.user.id.toString() == userId) {
            mMap.uiSettings.setAllGesturesEnabled(true)
        }

        if (lat != 0.0 && lng != 0.0) {
            val location = LatLng(lat, lng)
            mMap.addMarker(
                MarkerOptions()
                    .position(location)
            )
            val cameraPosition = CameraPosition.Builder()
                .target(location)
                .zoom(9F)
                .build()
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        }
    }

}