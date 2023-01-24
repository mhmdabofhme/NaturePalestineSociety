package com.example.naturepalestinesociety.activities

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.naturepalestinesociety.R
import com.example.naturepalestinesociety.activities.fragments.AddFragment
import com.example.naturepalestinesociety.activities.fragments.HomeFragment
import com.example.naturepalestinesociety.activities.fragments.MessagesFragment
import com.example.naturepalestinesociety.activities.fragments.ProfileFragment
import com.example.naturepalestinesociety.databinding.ActivityMainBinding
import com.example.naturepalestinesociety.helpers.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setFullScreen(window)
        setLightStatusBar(window, true)

        this.RTL()

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navView.setupWithNavController(navController)


        startProfileFragment()
        startMessagesFragment()

    }


    private fun startProfileFragment() {
        if (intent.getStringExtra(Constants.START_HOME) != null) {
            val navController = findNavController(R.id.nav_host_fragment_activity_main)
            val graphInflater = navController.navInflater
            val navGraph = graphInflater.inflate(R.navigation.mobile_navigation)
            val destination = R.id.navigation_profile
            navGraph.setStartDestination(destination)
            navController.graph = navGraph
        }
    }

    private fun startMessagesFragment() {
        if (intent.getSerializableExtra(Constants.NOTIFICATION) != null) {
            val navController = findNavController(R.id.nav_host_fragment_activity_main)
            val graphInflater = navController.navInflater
            val navGraph = graphInflater.inflate(R.navigation.mobile_navigation)
            val destination = R.id.navigation_messages
            navGraph.setStartDestination(destination)
            navController.graph = navGraph
        }
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setMessage(getString(R.string.close_app))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.yes),
                DialogInterface.OnClickListener { dialog, id -> super.onBackPressed() })
            .setNegativeButton(getString(R.string.no), null)
            .show()
    }
}