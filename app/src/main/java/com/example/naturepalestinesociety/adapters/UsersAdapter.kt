package com.example.naturepalestinesociety.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.naturepalestinesociety.activities.ChatActivity
import com.example.naturepalestinesociety.databinding.CustomUsersBinding
import com.example.naturepalestinesociety.helpers.Constants
import com.example.naturepalestinesociety.helpers.getLang
import com.example.naturepalestinesociety.helpers.loadImage
import com.example.naturepalestinesociety.models.follow.Followers
import com.example.naturepalestinesociety.models.follow.Following
import com.example.naturepalestinesociety.models.profile.Profile


class UsersAdapter(
    private val activity: Activity,
    private val list: List<Following>,

) :
    RecyclerView.Adapter<UsersAdapter.MyViewHolder>() {

    inner class MyViewHolder(b: CustomUsersBinding) :
        RecyclerView.ViewHolder(b.root) {
        val binding: CustomUsersBinding = b
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val b: CustomUsersBinding =
            CustomUsersBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(b)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(
        holder: MyViewHolder,
        position: Int
    ) {

        val data = list[position]

        holder.binding.apply {
            imgUser.loadImage(data.targets.photo)

            if (activity.getLang() == "en") {
                txtUserName.text = data.targets.name
            } else {
                txtUserName.text = data.targets.name
            }
            txtUserEmail.text = data.targets.email

        }

        holder.binding.root.setOnClickListener {
            val intent = Intent(activity, ChatActivity::class.java)
            intent.putExtra(Constants.USER, data.targets)

            activity.startActivity(intent)
        }


    }

    override fun getItemCount(): Int {
        return list.size
    }

}