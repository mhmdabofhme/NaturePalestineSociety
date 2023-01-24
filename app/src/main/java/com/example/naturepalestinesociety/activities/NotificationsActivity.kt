package com.example.naturepalestinesociety.activities

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.naturepalestinesociety.R
import com.example.naturepalestinesociety.adapters.NotificationsAdapter
import com.example.naturepalestinesociety.adapters.SearchUsersAdapter
import com.example.naturepalestinesociety.adapters.UsersAdapter
import com.example.naturepalestinesociety.databinding.ActivityNotificationsBinding
import com.example.naturepalestinesociety.helpers.*
import com.example.naturepalestinesociety.models.User
import com.example.naturepalestinesociety.models.notifications.Notification
import com.example.naturepalestinesociety.models.post.Post
import com.example.naturepalestinesociety.models.search.Search
import com.example.naturepalestinesociety.utils.Status
import com.example.naturepalestinesociety.viewmodel.MainViewModel

class NotificationsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationsBinding
    private val viewModel by viewModels<MainViewModel>()

    private val pageStart: Int = 1
    private var isLoading: Boolean = false
    private var isLastPage: Boolean = false
    private var totalPages: Int = 1
    private var currentPage: Int = pageStart

    private var notifications = mutableListOf<Notification>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNotificationsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setFullScreen(window)
        setLightStatusBar(window, true)
        viewModel.init(this)

        binding.imgBack.setOnClickListener { finish() }
        getNotifications()

        binding.recNotifications.addOnScrollListener(object :
            PaginationScrollListener(binding.recNotifications.layoutManager as LinearLayoutManager) {
            override fun loadMoreItems() {
                Log.d("TAG", "new:currentPage $currentPage")
                binding.layoutProgress.loadingProgress.visibility = View.VISIBLE
                currentPage += 1
                isLoading = true
                loadMore(currentPage)
                isLoading=false
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


//        binding.scrollView.viewTreeObserver.addOnScrollChangedListener {
//            val view = binding.scrollView.getChildAt(binding.scrollView.childCount - 1)
//            val diff: Int = view.bottom - (binding.scrollView.height + binding.scrollView.scrollY)
//            Log.d(TAG, "onCreate: $diff")
//            Log.d(TAG, "onCreate: $totalPages")
//            Log.d(TAG, "onCreate: $currentPage")
//            if (diff == 0 && currentPage < totalPages) {
//                Log.d("TAG", "new:currentPage viewTreeObserver$currentPage")
//                currentPage += 1
//                binding.layoutProgress.loadingProgress.visibility = View.VISIBLE
//                loadMore(currentPage)
//            }
//        }

    }

    private fun getNotifications() {
        notifications.clear()
        viewModel.getNotifications().observe(this) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        if (it.data?.body()?.data != null) {
                            totalPages = it.data.body()?.data!!.last_page
                            notifications.addAll(it.data?.body()?.data!!.items)
                            binding.recNotifications.adapter =
                                NotificationsAdapter(this, notifications)
                            Log.d("TAG", "getNotifications: ${notifications.size}")
                            Log.d("TAG", "getNotifications:totalPages ${totalPages}")
                            Log.d("TAG", "getNotifications:currentPage ${currentPage}")
//                            if (notifications.isEmpty()) {
//                                binding.imgNoData.visibility = View.VISIBLE
//                                binding.recNotifications.visibility = View.GONE
//                            } else {
//                                binding.recNotifications.visibility = View.VISIBLE
//                                binding.imgNoData.visibility = View.GONE
//                            }
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
    private fun loadMore(currentPage: Int) {
        viewModel.getNotifications(currentPage)
            .observe(this) {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            binding.layoutProgress.loadingProgress.visibility = View.GONE

                            for (item in it.data?.body()?.data!!.items){
                                notifications.add(item)
                                binding.recNotifications.adapter?.notifyDataSetChanged()
                            }
//                            binding.recNotifications.adapter = NotificationsAdapter(this,notifications)
//                            binding.recNotifications.adapter?.notifyDataSetChanged()
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