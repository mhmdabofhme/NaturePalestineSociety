package com.example.naturepalestinesociety.activities.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.naturepalestinesociety.R
import com.example.naturepalestinesociety.activities.ChooseSpeciesActivity
import com.example.naturepalestinesociety.activities.CreateProjectActivity
import com.example.naturepalestinesociety.activities.MainActivity
import com.example.naturepalestinesociety.activities.MapsActivity
import com.example.naturepalestinesociety.databinding.FragmentAddBinding
import com.example.naturepalestinesociety.helpers.*
import com.example.naturepalestinesociety.models.post.CreatePost
import com.example.naturepalestinesociety.models.profile.Profile
import com.example.naturepalestinesociety.models.project.Project
import com.example.naturepalestinesociety.utils.Status
import com.example.naturepalestinesociety.viewmodel.MainViewModel
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import java.io.File


class AddFragment : Fragment(), OnMapReadyCallback,
    ConnectivityReceiver.ConnectivityReceiverListener {

    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!

    private lateinit var mMap: GoogleMap
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private val viewModel by viewModels<MainViewModel>()


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
    private lateinit var myLastLocation: Location

    private var marker: Marker? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddBinding.inflate(inflater, container, false)
        val root: View = binding.root
        viewModel.init(requireContext())

        val mapFragment = childFragmentManager
            .findFragmentById(R.id.mapLocation) as SupportMapFragment
        mapFragment.getMapAsync(this)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        isConnected = requireContext().getInternetConnection()


        getProfile()


        binding.bottomSheet.layoutAddLocation.setOnClickListener {
            val intent = Intent(requireContext(), MapsActivity::class.java)
            activityResultLauncher.launch(intent)
        }

        binding.bottomSheet.layoutCreateProject.setOnClickListener {
            val intent = Intent(requireContext(), CreateProjectActivity::class.java)
            activityResultLauncher.launch(intent)
        }

        binding.bottomSheet.layoutChooseSpecies.setOnClickListener {
            val intent = Intent(requireContext(), ChooseSpeciesActivity::class.java)
            activityResultLauncher.launch(intent)
        }


        val view = root.findViewById<ConstraintLayout>(R.id.bottomSheet)
        bottomSheetBehavior = BottomSheetBehavior.from(view)
        binding.layoutTop.setOnClickListener {
            binding.editObserve.requestFocus()
            requireContext().showSoftKeyboard(binding.editObserve)
        }

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


        binding.txtPost.setOnClickListener {
            if (isValid()) {
                val post = CreatePost(
                    title_ar = binding.editObserve.text.toString(),
                    title_en = binding.editObserve.text.toString(),
                    description_ar = binding.editObserve.text.toString(),
                    description_en = binding.editObserve.text.toString(),
                    lat = location?.latitude.toString(),
                    lang = location?.longitude.toString(),
                    project_id = project?.id.toString(),
                    images = imageFile,
                )
                addPost(post)
            }
        }

        return root
    }


    private fun getProfile() {
        viewModel.getProfile().observe(viewLifecycleOwner) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        user = it.data?.body()?.data
                        binding.txtUsername.text = user?.name
                        binding.imgUser.loadImage(user?.photo!!)
                    }
                    Status.ERROR -> {
                        requireContext().toastError(it.message)
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

                imageFile = if (imageUri.toString().contains("content", true)) {
                    requireContext().getRealPathFromURI(imageUri)?.let { File(it) }
                } else {
                    File(imageUri!!.path ?: "")
                }
            }
            ImagePicker.RESULT_ERROR -> {
                Toast.makeText(requireContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT)
                    .show()
            }

        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        if (requireContext().checkLocationPermissions()) {
            if (requireContext().isLocationEnabled()) {
                Log.d(ContentValues.TAG, "getLocation isConnected: $isConnected")
                if (this.location == null) {
                    val task = mFusedLocationClient.lastLocation
                    task.addOnSuccessListener { myLocation ->
                        if (myLocation != null) {
                            myLastLocation = myLocation

                            location =
                                LatLng(myLastLocation.latitude, myLastLocation.longitude)

                            val address =
                                requireContext().getAddress(
                                    location!!.latitude,
                                    location!!.longitude
                                )

                            binding.bottomSheet.txtAddLocation.text = address

                            marker?.remove()
                            marker = mMap.addMarker(
                                MarkerOptions()
                                    .position(location!!)
                            )

                            val cameraPosition = CameraPosition.Builder()
                                .target(location!!)
                                .zoom(10F)
                                .build()

                            if (this::mMap.isInitialized) {
                                mMap.animateCamera(
                                    CameraUpdateFactory.newCameraPosition(
                                        cameraPosition
                                    )
                                )
                            }
                        }
                    }.addOnFailureListener {
                        Toast.makeText(
                            requireContext(),
                            it.message.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Please turn on location", Toast.LENGTH_LONG)
                    .show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requireActivity().requestLocationPermissions()
        }
    }

    private fun getSelectedLocation(lat: Double, lng: Double): LatLng {
        val address = requireContext().getAddress(lat, lng)
        binding.txtLocation.text = address
        binding.bottomSheet.txtAddLocation.text = address
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
    }

    private fun addPost(post: CreatePost) {
        viewModel.addPost(
            post.title_ar,
            post.title_en,
            post.description_ar,
            post.description_en,
            post.lat,
            post.lang,
            post.project_id,
            post.images
        ).observe(viewLifecycleOwner) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        if (it.data?.body()?.status == true) {
                            Log.d(TAG, "addPost: ${it.data?.body()?.data}")
                            Log.d(TAG, "addPost: ${it.data?.body()?.message}")
                            Log.d(TAG, "addPost: ${it.data?.body()?.status}")
                            startActivity(Intent(requireContext(), MainActivity::class.java))
                        }
                    }
                    Status.ERROR -> {
                        Log.d(TAG, "ERROR addPost: ${it.message}")
                        requireContext().toastError(it.message)
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
            Toast.makeText(requireContext(), "Internet connected", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(requireContext(), "Unable to connect to the internet", Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun isValid(): Boolean {
        if (binding.editObserve.text.toString().isEmpty()) {
            requireContext().toastError(getString(R.string.post_details) + " " + getString(R.string.is_required))
        }
        if (project == null) {
            requireContext().toastError(getString(R.string.project_name) + " " + getString(R.string.is_required))
        } else if (imageFile == null) {
            requireContext().toastError(getString(R.string.image) + " " + getString(R.string.is_required))
        } else if (location == null) {
            requireContext().toastWarning(getString(R.string.getting_location))
            getLocation()
        }

        return project != null && imageFile != null && location != null

    }

    override fun onResume() {
        super.onResume()
        getLocation()
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

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            Permissions.permissionLocationId -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Log.d("TAG", "onRequestPermissionsResult: ")

                }
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}



