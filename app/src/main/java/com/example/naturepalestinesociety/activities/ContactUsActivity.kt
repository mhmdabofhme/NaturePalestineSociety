package com.example.naturepalestinesociety.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import com.example.naturepalestinesociety.R
import com.example.naturepalestinesociety.databinding.ActivityContactUsBinding
import com.example.naturepalestinesociety.helpers.*
import com.example.naturepalestinesociety.models.contacts.Contacts
import com.example.naturepalestinesociety.models.profile.Profile
import com.example.naturepalestinesociety.utils.Status
import com.example.naturepalestinesociety.viewmodel.MainViewModel

class ContactUsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityContactUsBinding
    private val viewModel by viewModels<MainViewModel>()
    private var user: Profile? = null

    private var name = ""
    private var email = ""
    private var message = ""
    private var phone: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactUsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setFullScreen(window)
        setLightStatusBar(window,true)
        viewModel.init(this)
        getProfile()

        binding.btnSend.setOnClickListener {
            if (isValid()) {
                val contact = Contacts(name, email, message, phone)
                createContact(contact)
            }else{
                val message = "${getString(R.string.message) } ${getString(R.string.is_required)}"
                toastError(message)
            }
        }

        binding.imgBack.setOnClickListener { finish() }

        binding.imgFacebook.setOnClickListener {
            openFacebook("https://www.facebook.com/mhmdabofhme")
        }

        binding.imgInstagram.setOnClickListener {
            openInstagram("https://www.instagram.com/mhmdabofhme/")
        }

        binding.imgTwitter.setOnClickListener {
            openTwitter("https://twitter.com/mhmdabofhme/")
        }

        binding.imgSnapchat.setOnClickListener {
            openSnapchat("https://www.snapchat.com/add/m7mdabofhme")
        }
    }

    private fun createContact(data: Contacts) {
        viewModel.createContact(data).observe(this) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        if (it.data?.body()?.status == true) {
                            startActivity(Intent(this, MainActivity::class.java))
                        }
                    }
                    Status.ERROR -> {
                        Log.d(TAG, "addProject: ${it.message}")
                        toastError(it.message)
                    }
                    Status.LOADING -> {

                    }
                }
            }
        }
    }

    private fun getProfile() {
        viewModel.getProfile().observe(this) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        if (it.data?.body()?.data != null) {
                            user = it.data.body()?.data
                            name = user!!.name
                            email = user!!.email
                        }
                    }
                    Status.ERROR -> {
                        Log.d(TAG, "getProfile: ${it.message}")
                        toastError(it.message)
                    }
                    Status.LOADING -> {

                    }
                }
            }
        }
    }

    private fun isValid(): Boolean {
        if (binding.editFullName.text.toString().isNotEmpty())
            name = binding.editFullName.text.toString()
        else if (binding.editEmail.text.toString().isNotEmpty())
            email = binding.editEmail.text.toString()
        else if (binding.editMobileNumber.text.toString().isNotEmpty())
            phone = binding.editMobileNumber.text.toString()
        else if (binding.editMessage.text.toString().isNotEmpty())
            message = binding.editMessage.text.toString()

        return message.isNotEmpty()
    }
}