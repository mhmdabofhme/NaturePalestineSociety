package com.example.naturepalestinesociety.activities

import android.app.Activity
import android.content.Intent
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.naturepalestinesociety.R
import com.example.naturepalestinesociety.databinding.ActivityUpdatePostBinding
import com.example.naturepalestinesociety.helpers.*
import com.example.naturepalestinesociety.models.post.Post
import com.example.naturepalestinesociety.models.post.RemoveImage
import com.example.naturepalestinesociety.models.profile.Profile
import com.example.naturepalestinesociety.models.project.Project
import com.example.naturepalestinesociety.utils.Status
import com.example.naturepalestinesociety.viewmodel.MainViewModel
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import java.io.File

class UpdatePostActivity : AppCompatActivity(), OnMapReadyCallback,
    ConnectivityReceiver.ConnectivityReceiverListener {

    private lateinit var binding: ActivityUpdatePostBinding

    private lateinit var mMap: GoogleMap
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    private val viewModel by viewModels<MainViewModel>()


    private var post: Post? = null
    private var project: Project? = null
    private var user: Profile? = null
    private var projectName = ""
    private var specieName = ""
    private var lat: String? = null
    private var lng: String? = null
    private var imageUri: Uri? = null
    private var imageFile: File? = null
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var location: LatLng? = null
    private var isConnected = false
    private var postId = 0
    private var marker: Marker? = null




    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityUpdatePostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setFullScreen(window)
        setLightStatusBar(window, true)
        viewModel.init(this)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        isConnected = this.getInternetConnection()


        binding.bottomSheet.layoutAddLocation.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            activityResultLauncher.launch(intent)
        }

        binding.bottomSheet.layoutCreateProject.setOnClickListener {
            val intent = Intent(this, CreateProjectActivity::class.java)
            activityResultLauncher.launch(intent)
        }

        binding.bottomSheet.layoutChooseSpecies.setOnClickListener {
            val intent = Intent(this, ChooseSpeciesActivity::class.java)
            activityResultLauncher.launch(intent)
        }


        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapLocation) as SupportMapFragment
        mapFragment.getMapAsync(this)


//        val view = findViewById<ConstraintLayout>(R.id.bottomSheet)
//        bottomSheetBehavior = BottomSheetBehavior.from(view)
//        bottomSheetBehavior.addBottomSheetCallback(object :
//            BottomSheetBehavior.BottomSheetCallback() {
//            override fun onSlide(bottomSheet: View, slideOffset: Float) {
//
//            }
//
//            override fun onStateChanged(bottomSheet: View, newState: Int) {
//                when (newState) {
//
//                }
//            }
//        })

        binding.bottomSheet.layoutAddPhoto.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(1024)
                .maxResultSize(
                    1080,
                    1080
                )
                .start()
        }

        getProfile()

        binding.txtPost.setOnClickListener {
            if (isValid()) {
                updatePost(
                    title_ar = binding.editObserve.text.toString(),
                    title_en = binding.editObserve.text.toString(),
                    description_ar = binding.editObserve.text.toString(),
                    description_en = binding.editObserve.text.toString(),
                    lat = location?.latitude.toString(),
                    lang = location?.longitude.toString(),
                    project_id = project?.id.toString(),
                    post_id = postId.toString(),
                    images = imageFile,
                )
            }
        }

        binding.imgCancel.setOnClickListener {
            removeImage(post!!.media[post!!.media.size-1].id.toString())
        }

    }


    private fun getPost(postId: Int) {
        Log.d(TAG, "getPost: $postId")
        viewModel.getPost(postId).observe(this) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        if (it.data?.body()?.data != null) {
                            post = it.data.body()?.data!![0]
                            binding.apply {
                                editObserve.setText(post!!.title)
                                imgUser.loadImage(post!!.user.photo)
                                imgPost.loadImage(post!!.media[post!!.media.size-1].file_name)
                                lat = post!!.lat.toString()
                                lng = post!!.lang.toString()
                                getSelectedLocation(post!!.lat, post!!.lang)
                                project = post!!.project
                                projectName = project!!.name
                                txtUsername.text = post!!.user.name

                            }
                            binding.imgPost.setOnClickListener {
                                focusImage(post!!.media[post!!.media.size-1].file_name)
                            }
                        }
                    }
                    Status.ERROR -> {
                        Log.d(TAG, "getPost: ${it.message}")
                        this.toastError(it.message)
                    }
                    Status.LOADING -> {

                    }
                }
            }
        }
    }

    private fun removeImage(mediaId:String) {
        viewModel.removeImage(RemoveImage(mediaId)).observe(this) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        imageFile = null
                        imageUri=null
                        binding.imgPost.setImageDrawable(null)
                    }
                    Status.ERROR -> {
                        Log.d(TAG, "getProfile: ${it.message}")
                        this.toastError(it.message)
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
                        user = it.data?.body()?.data
                    }
                    Status.ERROR -> {
                        Log.d(TAG, "getProfile: ${it.message}")
                        this.toastError(it.message)
                    }
                    Status.LOADING -> {

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
                binding.imgPost.visibility = View.VISIBLE
                binding.imgPost.setImageURI(imageUri)

                if (imageUri.toString().contains("content", true)) {
                    imageFile = this.getRealPathFromURI(imageUri)?.let { File(it) }
                    Log.d(TAG, "onActivityResult:getRealPathFromURI $imageFile")
                } else {
                    imageFile = File(imageUri!!.path ?: "")
                    Log.d(TAG, "onActivityResult:else $imageFile")
                }
            }
            ImagePicker.RESULT_ERROR -> {
                Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }


    private fun getSelectedLocation(lat: Double, lng: Double): LatLng {
        val address = this.getAddress(lat, lng)
        binding.txtLocation.text = address

        location = LatLng(lat, lng)
        marker?.remove()
        marker = mMap.addMarker(
            MarkerOptions()
                .position(location!!)
        )
        val cameraPosition = CameraPosition.Builder()
            .target(location!!)
            .zoom(9F)
            .build()
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))


        binding.layoutLocation.visibility = View.VISIBLE

        return location!!
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        postId = intent.getIntExtra(Constants.POST_ID, 0)
        getPost(postId)

    }

    private var activityResultLauncher = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        Log.d("TAG", "onActivityResult: ")
        if (result.resultCode == Constants.RESULT_CODE_GET_LOCATION) {
            val intent = result.data
            if (intent != null) {
                lat = intent.getStringExtra(Constants.LAT)!!
                lng = intent.getStringExtra(Constants.LNG)!!
                getSelectedLocation(lat!!.toDouble(), lng!!.toDouble())
                val address =
                    this.getAddress(lat!!.toDouble(), lng!!.toDouble())
                binding.bottomSheet.txtAddLocation.text = address
                Log.d(TAG, "onCreateView:address $address")
            }
        } else if (result.resultCode == Constants.RESULT_CODE_GET_PROJECT) {
            val intent = result.data
            if (intent != null) {
                project = intent.getSerializableExtra(Constants.PROJECT) as Project
                binding.txtProject.visibility = View.VISIBLE
                binding.txtProject.text = project!!.name
            }
        } else if (result.resultCode == Constants.RESULT_CODE_GET_SPECIES) {
            val intent = result.data
            if (intent != null) {
                specieName = intent.getStringExtra(Constants.SPECIES_NAME)!!
                binding.txtSpecies.visibility = View.VISIBLE
                binding.txtSpecies.text = specieName
            }
        }

    }

    private fun updatePost(
        title_ar: String? = null,
        title_en: String? = null,
        description_ar: String? = null,
        description_en: String? = null,
        lat: String? = null,
        lang: String? = null,
        project_id: String? = null,
        post_id: String? = null,
        images: File? = null,
    ) {
        Log.d(TAG, "updatePost: in")
        viewModel.updatePost(
            title_ar,
            title_en,
            description_ar,
            description_en,
            lat,
            lang,
            project_id,
            post_id,
            images
        ).observe(this) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        if (it.data?.body()?.status == true) {
                            Log.d(TAG, "addPost: ${it.data?.body()?.data}")
                            Log.d(TAG, "addPost: ${it.data?.body()?.message}")
                            Log.d(TAG, "addPost: ${it.data?.body()?.status}")
                            startActivity(Intent(this, MainActivity::class.java))
                        }
                    }
                    Status.ERROR -> {
                        Log.d(TAG, "ERROR addPost: ${it.message}")
                        this.toastError(it.message)
                    }
                    Status.LOADING -> {

                    }
                }
            }
        }
    }


    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        this.isConnected = isConnected
        if (isConnected) {
            Toast.makeText(this, "Internet connected", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "Unable to connect to the internet", Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun isValid(): Boolean {
        if (project == null) {
            toastError(getString(R.string.project_name) + " " + getString(R.string.is_required))
        } else if (imageFile == null) {
            toastError(getString(R.string.image) + " " + getString(R.string.is_required))
        } else if (location == null) {
            toastError(getString(R.string.location) + " " + getString(R.string.is_required))
        }
        return project != null && imageFile != null && location != null
    }


}
