package com.example.naturepalestinesociety.activities.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.naturepalestinesociety.R
import com.example.naturepalestinesociety.activities.EditProfileActivity
import com.example.naturepalestinesociety.activities.SettingsActivity
import com.example.naturepalestinesociety.adapters.MyPostsAdapter
import com.example.naturepalestinesociety.adapters.PostsAdapter
import com.example.naturepalestinesociety.databinding.DialogDeletePostBinding
import com.example.naturepalestinesociety.databinding.DialogImageBinding
import com.example.naturepalestinesociety.databinding.FragmentProfileBinding
import com.example.naturepalestinesociety.helpers.*
import com.example.naturepalestinesociety.models.post.Post
import com.example.naturepalestinesociety.models.project.Project
import com.example.naturepalestinesociety.models.profile.Profile
import com.example.naturepalestinesociety.utils.Status
import com.example.naturepalestinesociety.viewmodel.MainViewModel

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<MainViewModel>()
    private var user: Profile? = null
    private var projects = mutableListOf<Project>()
    private var posts = mutableListOf<Post>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root
        viewModel.init(requireContext())


        getProfile()


        binding.spinnerProjects.setOnSpinnerItemSelectedListener<String> { oldIndex, oldItem, newIndex, newItem ->
            if (newItem == getString(R.string.all)) {
                binding.recPosts.adapter = PostsAdapter(requireActivity(), posts)
            } else {
                val list = mutableListOf<Post>()
                for (post in posts) {
                    if (newItem == post.project.name) {
                        list.add(post)
                    }
                }
                binding.recPosts.adapter = PostsAdapter(requireActivity(), list)
            }
        }

        binding.txtSettings.setOnClickListener {
            startActivity(Intent(requireContext(), SettingsActivity::class.java))
        }

        binding.txtEditProfile.setOnClickListener {
            startActivity(Intent(requireContext(), EditProfileActivity::class.java))
        }

        return root
    }

    private fun getProfile() {
        posts.clear()
        viewModel.getProfile().observe(viewLifecycleOwner) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        val image: String? = it.data?.body()?.data?.photo
                        binding.imgUser.loadImage(
                            image.toString(),
                            R.drawable.ic_baseline_account_box_24
                        )
                        if (it.data?.body()?.data != null) {
                            user = it.data.body()?.data
                            val name: String? = user?.name
                            val bio: String? = user?.bio
                            val country: String? = user?.country
                            if (bio != null) {
                                binding.txtAboutInfo.text = bio
                            } else {
                                binding.txtAboutInfo.text = getString(R.string.add_bio)
                            }
                            binding.txtUsername.text = name
                            binding.txtCountry.text = country
                            binding.imgUser.loadImage(
                                image.toString(),
                                R.drawable.ic_baseline_account_box_24
                            )
                            posts.addAll(it.data.body()?.data?.posts!!)
                            getProjects(posts)
                            binding.txtObservations.text = posts.size.toString()

                            binding.recPosts.adapter = MyPostsAdapter(requireActivity(), posts) {
                                showDialog(it)
                            }
                            if(posts.isEmpty()){
                                binding.imgNoData.visibility = View.VISIBLE
                                binding.layoutPosts.visibility = View.GONE
                            }else{
                                binding.layoutPosts.visibility = View.VISIBLE
                                binding.imgNoData.visibility = View.GONE
                            }

                        }

                    }
                    Status.ERROR -> {
                        Log.d(TAG, "getProfile: ${it.message}")
                        requireContext().toastError(it.message)
                    }
                    Status.LOADING -> {

                    }
                }
            }
        }
    }

    private fun refresh() {
        posts.clear()
        viewModel.getProfile().observe(viewLifecycleOwner) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        if (it.data?.body()?.data != null) {

                            posts.addAll(it.data.body()?.data?.posts!!)
                            getProjects(posts)
                            binding.txtObservations.text = posts.size.toString()

                            binding.recPosts.adapter = MyPostsAdapter(requireActivity(), posts) {
                                showDialog(it)
                            }

                            if(posts.isEmpty()){
                                binding.imgNoData.visibility = View.VISIBLE
                                binding.layoutPosts.visibility = View.GONE
                            }else{
                                binding.layoutPosts.visibility = View.VISIBLE
                                binding.imgNoData.visibility = View.GONE
                            }

                        }

                    }
                    Status.ERROR -> {
                        Log.d(TAG, "getProfile: ${it.message}")
                        requireContext().toastError(it.message)
                    }
                    Status.LOADING -> {

                    }
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showDialog(post: Post) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val dialogBinding = DialogDeletePostBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)

        dialogBinding.btnNo.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.btnYes.setOnClickListener {
            viewModel.deletePost(post.id).observe(viewLifecycleOwner) {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            if (it.data?.body()?.data != null) {
                                requireContext().toastDone(getString(R.string.post_deleted))
                                refresh()
                                dialog.dismiss()
                            }
                        }
                        Status.ERROR -> {
                            Log.d("TAG", "showDialog: ${it.message}")
                            requireContext().toastError(it.message)
                        }
                        Status.LOADING -> {
                        }
                    }
                }
            }

        }
        dialog.setCancelable(true)
        dialog.show()
    }

    private fun getProjects(posts:List<Post>) {
        for (post in posts) {
            projects.add(post.project)
        }
        val list = mutableListOf(getString(R.string.all))
        for (project in projects.distinct()) {
            list.add(project.name)
        }
        binding.spinnerProjects.setItems(list)

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}