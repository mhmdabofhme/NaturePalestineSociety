package com.example.naturepalestinesociety.activities.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnScrollChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.naturepalestinesociety.activities.NotificationsActivity
import com.example.naturepalestinesociety.activities.SearchActivity
import com.example.naturepalestinesociety.adapters.PostsAdapter
import com.example.naturepalestinesociety.adapters.StoriesAdapter
import com.example.naturepalestinesociety.databinding.FragmentHomeBinding
import com.example.naturepalestinesociety.helpers.toastError
import com.example.naturepalestinesociety.models.User
import com.example.naturepalestinesociety.models.follow.Following
import com.example.naturepalestinesociety.models.post.Post
import com.example.naturepalestinesociety.utils.Status
import com.example.naturepalestinesociety.viewmodel.MainViewModel


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<MainViewModel>()

    private var posts = mutableListOf<Post>()


    private val pageStart: Int = 1
    private var totalPages: Int = 0
    private var currentPage: Int = pageStart

    private var listFollowing = mutableListOf<Following>()
    private var following = mutableListOf<User>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        viewModel.init(requireContext())

        getFollowing()
        getPosts()

        binding.imgSearch.setOnClickListener {
            startActivity(Intent(requireContext(), SearchActivity::class.java))
        }


        binding.imgNotifications.setOnClickListener {
            startActivity(Intent(requireContext(), NotificationsActivity::class.java))
        }


        binding.scrollView.viewTreeObserver.addOnScrollChangedListener {
            val view = binding.scrollView.getChildAt(binding.scrollView.childCount - 1)
            val diff: Int = view.bottom - (binding.scrollView.height + binding.scrollView.scrollY)
            if (diff == 0 && currentPage < totalPages) {
                Log.d("TAG", "new:currentPage viewTreeObserver$currentPage")
                currentPage += 1
                binding.layoutProgress.loadingProgress.visibility = View.VISIBLE
                loadMore(currentPage)
            }
        }

        return root
    }


    private fun getFollowing() {
        following.clear()
        listFollowing.clear()
        viewModel.getFollowing().observe(viewLifecycleOwner) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        if (it.data?.body()?.data != null) {
                            listFollowing.addAll(it.data.body()?.data!!)
                            getFeatures()
                        }
                    }
                    Status.ERROR -> {
                        Log.d("TAG", "getFollowing: ${it.message}")
                        requireContext().toastError(it.message)
                    }
                    Status.LOADING -> {

                    }
                }
            }
        }
    }

    private fun getFeatures() {
        following.clear()
        viewModel.getFeatures().observe(viewLifecycleOwner) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        if (it.data?.body()?.data != null) {
                            following.addAll(it.data.body()?.data!!)
                            for (follow in listFollowing) {
                                following.add(follow.targets)
                            }
                            binding.recStories.adapter =
                                StoriesAdapter(requireActivity(), following.distinctBy { it.id })
                        }
                    }
                    Status.ERROR -> {
                        Log.d("TAG", "getFollowing: ${it.message}")
                        requireContext().toastError(it.message)
                    }
                    Status.LOADING -> {

                    }
                }
            }
        }
    }

    private fun getPosts() {
        posts.clear()
        viewModel.getPosts().observe(viewLifecycleOwner) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        if (it.data?.body()?.data != null) {
                            totalPages = it.data.body()?.data!!.last_page
                            posts.addAll(it.data.body()?.data!!.items)
                            binding.recPosts.adapter = PostsAdapter(requireActivity(), posts)
                        }
                    }
                    Status.ERROR -> {
                        Log.d("TAG", "getPosts: ${it.message}")
                        requireContext().toastError(it.message)
                    }
                    Status.LOADING -> {

                    }
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadMore(currentPage: Int) {
        viewModel.getPosts(page = currentPage)
            .observe(requireActivity()) {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            binding.layoutProgress.loadingProgress.visibility = View.GONE
                            for (item in it.data?.body()?.data!!.items) {
                                posts.add(item)
                                binding.recPosts.adapter?.notifyDataSetChanged()
                            }
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

//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }


}