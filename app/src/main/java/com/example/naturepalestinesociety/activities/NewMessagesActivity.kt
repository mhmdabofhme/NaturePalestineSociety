package com.example.naturepalestinesociety.activities

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.naturepalestinesociety.adapters.UsersAdapter
import com.example.naturepalestinesociety.databinding.ActivityNewMessagesBinding
import com.example.naturepalestinesociety.helpers.setFullScreen
import com.example.naturepalestinesociety.helpers.setLightStatusBar
import com.example.naturepalestinesociety.helpers.toastError
import com.example.naturepalestinesociety.models.follow.Following
import com.example.naturepalestinesociety.utils.Status
import com.example.naturepalestinesociety.viewmodel.MainViewModel


class NewMessagesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewMessagesBinding
    private val viewModel by viewModels<MainViewModel>()
    private var listFollowing = mutableListOf<Following>()
    private var listFollowingFiltered = mutableListOf<Following>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewMessagesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel.init(this)
        setFullScreen(window)
        setLightStatusBar(window, true)

        binding.imgBack.setOnClickListener {
            finish()
        }


        getFollowing()


        binding.editSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (s.isNotEmpty()) {
                    filterFollowing(s.toString())
                }
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {

            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                if (s.isNotEmpty()) {
                    filterFollowing(s.toString())
                } else {
                    getFollowing()
                }
            }
        })

    }

    private fun getFollowing() {
        listFollowing.clear()
        viewModel.getFollowing().observe(this) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        if (it.data?.body()?.data != null) {
                            listFollowing.addAll(it.data.body()?.data!!)
                            binding.recUsers.adapter =
                                UsersAdapter(this, listFollowing)
                            if (listFollowing.isEmpty()) {
                                binding.imgNoData.visibility = View.VISIBLE
                                binding.recUsers.visibility = View.GONE
                            } else {
                                binding.recUsers.visibility = View.VISIBLE
                                binding.imgNoData.visibility = View.GONE
                            }
                        }
                    }
                    Status.ERROR -> {
                        Log.d("TAG", "getFollowing: ${it.message}")
                        toastError(it.message)
                    }
                    Status.LOADING -> {

                    }
                }
            }
        }
    }

    private fun filterFollowing(searchedName: String) {
        listFollowingFiltered.clear()
        for (following in listFollowing) {
            if (following.targets.name.contains(searchedName, ignoreCase = true)
                || following.targets.name.contains(searchedName, ignoreCase = true)
            ) {
                listFollowingFiltered.add(following)
            }
        }

        binding.recUsers.adapter = UsersAdapter(this, listFollowingFiltered/*, profile*/)
        if (listFollowingFiltered.isEmpty()) {
            binding.imgNoData.visibility = View.VISIBLE
            binding.recUsers.visibility = View.GONE
        } else {
            binding.recUsers.visibility = View.VISIBLE
            binding.imgNoData.visibility = View.GONE
        }
    }


}