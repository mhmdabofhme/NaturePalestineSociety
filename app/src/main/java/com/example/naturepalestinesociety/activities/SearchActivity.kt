package com.example.naturepalestinesociety.activities

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.naturepalestinesociety.R
import com.example.naturepalestinesociety.adapters.SearchUsersAdapter
import com.example.naturepalestinesociety.adapters.UsersAdapter
import com.example.naturepalestinesociety.databinding.ActivitySearchBinding
import com.example.naturepalestinesociety.helpers.PaginationScrollListener
import com.example.naturepalestinesociety.helpers.setFullScreen
import com.example.naturepalestinesociety.helpers.setLightStatusBar
import com.example.naturepalestinesociety.helpers.toastError
import com.example.naturepalestinesociety.models.User
import com.example.naturepalestinesociety.models.follow.Following
import com.example.naturepalestinesociety.models.post.Post
import com.example.naturepalestinesociety.models.profile.Profile
import com.example.naturepalestinesociety.models.search.Search
import com.example.naturepalestinesociety.models.search.SearchUser
import com.example.naturepalestinesociety.utils.Status
import com.example.naturepalestinesociety.viewmodel.MainViewModel

class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding

    private val viewModel by viewModels<MainViewModel>()

    private var posts = mutableListOf<Post>()


    private val pageStart: Int = 1
    private var isLoading: Boolean = false
    private var isLastPage: Boolean = false
    private var totalPages: Int = 0
    private var currentPage: Int = pageStart
    private var searchInput = ""

    private var listUsers = mutableListOf<User>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setFullScreen(window)
        setLightStatusBar(window, true)
        viewModel.init(this)

        getUsers()

        binding.imgBack.setOnClickListener { finish() }


        binding.editSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {

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
                    searchInput = s.toString()
                    searchUsers(searchInput)
                }
                else{
                    getUsers()
                }
            }
        })


        binding.recUsers.addOnScrollListener(object :
            PaginationScrollListener(binding.recUsers.layoutManager as LinearLayoutManager) {
            override fun loadMoreItems() {
                if (searchInput.isNotEmpty()) {
                    Log.d("TAG", "new:currentPage $currentPage")
                    binding.layoutProgress.loadingProgress.visibility = View.VISIBLE
                    isLoading = true
                    currentPage += 1
                    loadMore(currentPage, searchInput)
                    isLoading = false
                }
            }

            override fun getTotalPageCount(): Int {
                return totalPages
            }

            override fun isLastPage(): Boolean {
                return isLastPage
            }

            override fun isLoading(): Boolean {
                return isLoading
            }
        })


    }

    private fun getUsers() {
        listUsers.clear()
        viewModel.getUsers().observe(this) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        Log.d("TAG", "getUsers: ${it.data?.body()?.data?.size} ")
                        if (it.data?.body()?.data != null) {
                            listUsers.addAll(it.data.body()?.data!!)
                            binding.recUsers.adapter =
                                SearchUsersAdapter(this, listUsers)
                            if (listUsers.isEmpty()) {
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

    private fun searchUsers(name: String) {
        val data = Search(name)
        listUsers.clear()
        viewModel.searchUsers(data).observe(this) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        if (it.data?.body()?.data != null) {
                            totalPages = it.data.body()?.data!!.last_page
                            val user: List<User> = it.data?.body()?.data!!.items as List<User>
                            listUsers.addAll(user)
                            binding.recUsers.adapter =
                                SearchUsersAdapter(this, listUsers)
                            Log.d("TAG", "searchUsers: ${listUsers.size}")
                            if (listUsers.isEmpty()) {
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

    @SuppressLint("NotifyDataSetChanged")
    private fun loadMore(currentPage: Int, searchName: String) {
        Log.d("TAG", "loadMore: listUsers${listUsers.size}")
        val data = Search(searchName)
        listUsers.clear()
        viewModel.searchUsers(data = data, page = currentPage)
            .observe(this) {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            binding.layoutProgress.loadingProgress.visibility = View.GONE
                            val user: List<User> = it.data?.body()?.data!!.items as List<User>
                            listUsers.addAll(user)
                            binding.recUsers.adapter?.notifyDataSetChanged()

                            Log.d("TAG", "loadMore:currentPage ${currentPage}")
                        }
                        Status.ERROR -> {
                            binding.layoutProgress.loadingProgress.visibility = View.GONE
                        }
                        Status.LOADING -> {
                        }
                    }
                }
            }
    }

}