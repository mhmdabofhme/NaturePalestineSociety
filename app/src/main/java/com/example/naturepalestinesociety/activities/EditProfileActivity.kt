package com.example.naturepalestinesociety.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.viewModels
import com.example.naturepalestinesociety.R
import com.example.naturepalestinesociety.adapters.SpinnerAdapter
import com.example.naturepalestinesociety.databinding.ActivityEditProfileBinding
import com.example.naturepalestinesociety.helpers.*
import com.example.naturepalestinesociety.models.OldCountry
import com.example.naturepalestinesociety.models.country.Country
import com.example.naturepalestinesociety.models.profile.Profile
import com.example.naturepalestinesociety.utils.Status
import com.example.naturepalestinesociety.viewmodel.MainViewModel
import com.github.dhaval2404.imagepicker.ImagePicker
//import com.hbb20.CountryPickerView
//import com.hbb20.countrypicker.models.CPCountry
import java.io.File

class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditProfileBinding

    private val viewModel by viewModels<MainViewModel>()
    private var user: Profile? = null
    private var image: String? = null
    private var bio: String? = null
    private var email: String? = null
    private var name: String? = null
    private var country: String? = null
    private var countries = mutableListOf<Country>()
    private var countryId = 0
    private var adapter: SpinnerAdapter? = null

    private var imageFile: File? = null
    private var imageUri: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setFullScreen(window)
        setLightStatusBar(window,true)
        viewModel.init(this)


        binding.imgBack.setOnClickListener { finish() }
        binding.imgSave.setOnClickListener { finish() }
        getCountries()

        viewModel.getProfile().observe(this) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        image = it.data?.body()?.data?.photo
                        binding.imgUser.loadImage(
                            image.toString(),
                            R.drawable.ic_baseline_account_box_24
                        )
                        if (it.data?.body()?.data != null) {
                            user = it.data.body()?.data
                            name = user?.name
                            email = user?.email
                            bio = user?.bio
                            country = user?.country
                            for (item in countries) {
                                Log.d(TAG, "country: $country")
                                if (country == item.name) {
                                    binding.spinnerCountry.text = item.name
                                    countryId = item.id
                                }
                            }
                            binding.editName.setText(name)
                            binding.editEmail.setText(email)
                            binding.editAbout.setText(bio)
                        }
                    }
                    Status.ERROR -> {
                        Log.d(TAG, "onCreate: ${it.message}")
                        toastError(it.message)
                    }
                    Status.LOADING -> {

                    }
                }
            }
        }

        binding.imgUser.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(1024)
                .maxResultSize(
                    1080,
                    1080
                )
                .start()
        }

        binding.txtEditPicture.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(1024)
                .maxResultSize(
                    1080,
                    1080
                )
                .start()
        }



        binding.imgSave.setOnClickListener {

            var name_ar: String? = null
            var name_en: String? = null
            var bio_ar: String? = null
            var bio_en: String? = null
//
//            if (getLang() == "ar") {
//                name_ar = binding.editName.text.toString()
//                bio_ar = binding.editAbout.text.toString()
//            } else {
//                name_en = binding.editName.text.toString()
//                bio_en = binding.editAbout.text.toString()
//            }

            viewModel.updateUserProfile(
                name_ar = binding.editName.text.toString(),
//                name_ar = name_ar,
//                name_en = name_en,
                name_en = binding.editName.text.toString(),
                email = binding.editEmail.text.toString(),
                country_id = countryId.toString(),
                bio_ar = binding.editAbout.text.toString(),
                bio_en = binding.editAbout.text.toString(),
                photo = imageFile,

            ).observe(this) {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            toastDone(getString(R.string.updated))
                        }
                        Status.ERROR -> {
                            Log.d("TAG", "updateUserProfile: error ${it.message}")
                            toastError(it.message)
                        }
                        Status.LOADING -> {

                        }
                    }
                }
            }

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {

                imageUri = data?.data!!
                // Use Uri object instead of File to avoid storage permissions
                binding.imgUser.loadImage(imageUri.toString())
                Log.d(TAG, "onActivityResult:imageUri $imageUri")
                if (imageUri != null) {
                    imageFile = if (imageUri.toString().contains("content", true)) {
                        getRealPathFromURI(imageUri)?.let { File(it) }
                    } else {
                        File(imageUri!!.path ?: "")
                    }
                }
            }
            ImagePicker.RESULT_ERROR -> {
                Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            }
            else -> {
                Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getCountries() {
        viewModel.getCountries().observe(this) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        if (it.data?.body()?.data != null) {
                            countries.addAll(it.data.body()?.data!!)
                            var list = mutableListOf<String>()
                            for (country in countries) {
                                list.add(country.name)
                            }
                            binding.spinnerCountry.setItems(list)
                        }
                    }
                    Status.ERROR -> {
                        Log.d(TAG, "getCountries: ${it.message}")
                        toastError(it.message)
                    }
                    Status.LOADING -> {

                    }
                }
            }
        }
    }

}