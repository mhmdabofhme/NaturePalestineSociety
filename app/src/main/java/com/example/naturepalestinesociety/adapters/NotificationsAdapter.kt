package com.example.naturepalestinesociety.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.naturepalestinesociety.activities.ChatActivity
import com.example.naturepalestinesociety.databinding.CustomNotificationBinding
import com.example.naturepalestinesociety.databinding.CustomUsersBinding
import com.example.naturepalestinesociety.helpers.Constants
import com.example.naturepalestinesociety.helpers.getLang
import com.example.naturepalestinesociety.helpers.loadImage
import com.example.naturepalestinesociety.models.follow.Followers
import com.example.naturepalestinesociety.models.follow.Following
import com.example.naturepalestinesociety.models.notifications.Notification
import com.example.naturepalestinesociety.models.profile.Profile


class NotificationsAdapter(
    private val activity: Activity,
    private val list: MutableList<Notification>
) :
    RecyclerView.Adapter<NotificationsAdapter.MyViewHolder>() {

    inner class MyViewHolder(b: CustomNotificationBinding) :
        RecyclerView.ViewHolder(b.root) {
        val binding: CustomNotificationBinding = b
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val b: CustomNotificationBinding =
            CustomNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(b)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(
        holder: MyViewHolder,
        position: Int
    ) {

        val data = list[position]

        holder.binding.apply {
            txtSender.text= data.data.title
            txtMessage.text = data.data.data
            txtTimeAgo.text = data.created_at
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