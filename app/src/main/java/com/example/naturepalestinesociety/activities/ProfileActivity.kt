package com.example.naturepalestinesociety.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import com.example.naturepalestinesociety.R
import com.example.naturepalestinesociety.adapters.PostsAdapter
import com.example.naturepalestinesociety.databinding.ActivityProfileBinding
import com.example.naturepalestinesociety.helpers.*
import com.example.naturepalestinesociety.models.User
import com.example.naturepalestinesociety.models.follow.Follow
import com.example.naturepalestinesociety.models.post.Post
import com.example.naturepalestinesociety.models.profile.Profile
import com.example.naturepalestinesociety.models.project.Project
import com.example.naturepalestinesociety.utils.Status
import com.example.naturepalestinesociety.viewmodel.MainViewModel

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private var following = false

    private val viewModel by viewModels<MainViewModel>()
    private var profile: Profile? = null
    private var projects = mutableListOf<Project>()
    private var posts = mutableListOf<Post>()
    private var user: User? = null
    private var countryId = ""
    private var countryName = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setFullScreen(window)
        setLightStatusBar(window, true)
        viewModel.init(this)

        user = intent.getSerializableExtra(Constants.USER) as User


        if (user != null) {
            countryName = user?.country.toString()
            binding.txtCountry.text = countryName

            binding.apply {
                imgUser.loadImage(user?.photo!!)
                txtUsername.text = user?.name
                txtAboutInfo.text = user?.bio
                if (user?.bio == null) {
                    txtAbout.visibility = View.INVISIBLE
                } else {
                    txtAbout.visibility = View.VISIBLE
                }
            }

            binding.btnMessage.setOnClickListener {
                val intent = Intent(this, ChatActivity::class.java)
                intent.putExtra(Constants.USER, user)
                startActivity(intent)
            }


            getCountries(countryId)
            getProfile(user?.id!!)
            getFollowing(user?.id!!)

        }


        binding.spinnerProjects.setOnSpinnerItemSelectedListener<String> { oldIndex, oldItem, newIndex, newItem ->
            if (newItem == getString(R.string.all)) {
                binding.recPosts.adapter = PostsAdapter(this, posts)
            } else {
                val list = mutableListOf<Post>()
                for (post in posts) {
                    if (newItem == post.project.name) {
                        list.add(post)
                    }
                }
                binding.recPosts.adapter = PostsAdapter(this, list)
            }
        }


        binding.btnFollow.setOnClickListener {
            val follow = Follow(user?.id.toString())
            if (!following) {
                createFollowing(follow)
            } else {
                unfollow(follow)
            }
        }

        binding.imgBack.setOnClickListener { finish() }

    }

    private fun getCountries(countryId: String) {
        viewModel.getCountries().observe(this) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        if (it.data?.body()?.data != null) {
                            val countries = it.data.body()?.data!!
                            for (country in countries) {
                                if (country.id.toString() == countryId) {
                                    countryName = country.name
                                }
                            }

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

    private fun createFollowing(follow: Follow) {
        viewModel.createFollowing(follow).observe(this) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        Log.d(TAG, "createFollowing: ${it.data?.body()?.data}")

                        if (it.data?.body()?.data != null) {
                            following = true
                            binding.btnFollow.isSelected = true
                            binding.btnFollow.text = getString(R.string.following)
                            binding.btnMessage.visibility = View.VISIBLE
                        }
                    }
                    Status.ERROR -> {
                        Log.d(TAG, "createFollowing: ${it.message}")
                        toastError(it.message)
                    }
                    Status.LOADING -> {

                    }
                }
            }
        }
    }


    private fun unfollow(follow: Follow) {
        viewModel.unfollow(follow).observe(this) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        Log.d(TAG, "unfollow: ${it.data?.body()?.data}")
                        if (it.data?.body()?.data != null) {
                            following = false
                            binding.btnFollow.isSelected = false
                            binding.btnFollow.text = getString(R.string.follow)
                            binding.btnMessage.visibility = View.GONE
                        }
                    }
                    Status.ERROR -> {
                        Log.d(TAG, "unfollow: ${it.message}")
                        toastError(it.message)
                    }
                    Status.LOADING -> {

                    }
                }
            }
        }
    }

    private fun getFollowing(id: Int) {
        viewModel.getFollowing().observe(this) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        if (it.data?.body()?.data != null) {
                            Log.d(TAG, "getFollowers: ")
                            for (user in it.data?.body()?.data!!) {
                                Log.d(TAG, "getFollowers: ${user.targets.id}")
                                Log.d(TAG, "getFollowers:userId ${id}")
                                if (user.targets.id == id) {
                                    Log.d(TAG, "getFollowers:following ${user.targets.name}")
                                    following = true
                                    binding.btnFollow.isSelected = following
                                    binding.btnFollow.text = getString(R.string.following)
                                    binding.btnMessage.visibility = View.VISIBLE
                                }
                            }

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


    private fun getProfile(id: Int) {
        posts.clear()
        viewModel.getProfile(id).observe(this) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        if (it.data?.body()?.data != null) {
                            profile = it.data.body()?.data
                            posts.addAll(profile!!.posts)
                            binding.txtObservations.text = posts.size.toString()
                            binding.recPosts.adapter = PostsAdapter(this, posts)
                            if(posts.isEmpty()){
                                binding.imgNoData.visibility = View.VISIBLE
                                binding.layoutPosts.visibility = View.GONE
                            }else{
                                binding.layoutPosts.visibility = View.VISIBLE
                                binding.imgNoData.visibility = View.GONE
                            }
                            getProjects()
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


    private fun getProjects() {
        for (post in posts) {
            projects.add(post.project)
        }
        val list = mutableListOf(getString(R.string.all))
        for (project in projects.distinct()) {
            list.add(project.name)
        }
        Log.d("TAG", "getProjects: ${list[0]}")
        binding.spinnerProjects.setItems(list)

    }
}