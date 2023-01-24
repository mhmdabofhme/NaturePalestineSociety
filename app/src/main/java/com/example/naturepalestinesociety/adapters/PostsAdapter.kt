package com.example.naturepalestinesociety.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.naturepalestinesociety.R
import com.example.naturepalestinesociety.activities.PostDetailsActivity
import com.example.naturepalestinesociety.activities.ProfileActivity
import com.example.naturepalestinesociety.databinding.CustomPostBinding
import com.example.naturepalestinesociety.helpers.*
import com.example.naturepalestinesociety.models.post.Post
import java.util.*


class PostsAdapter(private val activity: Activity, private val list: MutableList<Post>) :
    RecyclerView.Adapter<PostsAdapter.MyViewHolder>() {


    inner class MyViewHolder(b: CustomPostBinding) :
        RecyclerView.ViewHolder(b.root) {
        val binding: CustomPostBinding = b
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val b: CustomPostBinding =
            CustomPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(b)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
        holder: MyViewHolder,
        position: Int
    ) {
        val data = list[position]

        holder.binding.apply {
            imgUser.loadImage(data.user.photo)


            Log.d("TAG", "onBindViewHolder: ${data.media[data.media.size-1].file_name}")
            imgPost.loadImage(data.media[data.media.size-1].file_name)
            txtPostTitle.text = data.title


            if (data.user.feature == 1) {
                txtVerified.visibility = View.VISIBLE
            } else {
                txtVerified.visibility = View.GONE
            }

            val postDate = data.created_at.apiFormat()

            txtUsername.text = data.user.name
            txtTimeObserved.text =
                "${activity.getString(R.string.observed)} ${postDate.toTimeFormated()}"

            txtTimeAgo.text = activity.findDifference(
                postDate.toDateTimeFormated(),
                Calendar.getInstance().time.toDateTimeFormated()
            ) + " " + activity.getString(R.string.ago)
        }

        holder.binding.imgUser.setOnClickListener {
            val intent = Intent(activity, ProfileActivity::class.java)
            intent.putExtra(Constants.USER, data.user)
            activity.startActivity(intent)
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(activity, PostDetailsActivity::class.java)
            intent.putExtra(Constants.POST, data)
            activity.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

}