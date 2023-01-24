package com.example.naturepalestinesociety.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.naturepalestinesociety.activities.ProfileActivity
import com.example.naturepalestinesociety.databinding.CustomStoriesRawBinding
import com.example.naturepalestinesociety.helpers.Constants
import com.example.naturepalestinesociety.helpers.loadImage
import com.example.naturepalestinesociety.models.User
import com.example.naturepalestinesociety.models.follow.Followers
import com.example.naturepalestinesociety.models.follow.Following



class StoriesAdapter(private val activity: Activity, private val list: List<User>) :
    RecyclerView.Adapter<StoriesAdapter.MyViewHolder>() {

//    var selectedItemPos = -1
//    var lastItemSelectedPos = -1


    inner class MyViewHolder(b: CustomStoriesRawBinding) :
        RecyclerView.ViewHolder(b.root) {
        val binding: CustomStoriesRawBinding = b
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val b: CustomStoriesRawBinding =
            CustomStoriesRawBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(b)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(
        holder: MyViewHolder,
        position: Int
    ) {

//        if (list[position].storyStatus == "Seen") {
//            Log.d(TAG, "onBindViewHolder: $position")
//            Log.d(TAG, "onBindViewHolder: ${list.size}")
//            list.moveAt(position, list.size)
//        }

//        if (position == 0) {
//            holder.binding.imgAddStory.visibility = View.VISIBLE
//        } else {
//            holder.binding.imgAddStory.visibility = View.GONE
//
//        }

        val data = list[position]

        holder.binding.apply {

            imgUser.loadImage(data.photo)

//            if (position == 0)
//                root.isSelected = true
//            txtUserName.text = data.userName
//            if (data.storyStatus == StoryStatus.New) {
//                imgUser.isSelected = true
//            } else if (data.storyStatus == StoryStatus.Seen) {
//                imgUser.isSelected = false
//            }
//            if (data.storyStatus == "New") {
//                imgUser.isSelected = true
//            } else if (data.storyStatus == "Seen") {
//                imgUser.isSelected = false
//            }
//            imgAddStory.setOnClickListener {
//                activity.startActivity(Intent(activity,AddStoryActivity::class.java))
//            }
        }


        holder.binding.root.setOnClickListener {
            val intent = Intent(activity,ProfileActivity::class.java)
            intent.putExtra(Constants.USER,data)
            activity.startActivity(intent)
        }


    }

    override fun getItemCount(): Int {
        return list.size
    }

//    private fun <T> MutableList<T>.moveAt(oldIndex: Int, newIndex: Int) {
//        val item = this[oldIndex]
//        removeAt(oldIndex)
//        if (oldIndex > newIndex) {
//            add(newIndex, item)
//        } else
//            add(newIndex - 1, item)
//    }

}