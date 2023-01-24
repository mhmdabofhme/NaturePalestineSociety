package com.example.naturepalestinesociety.messages

import android.app.Activity
import android.util.Log
import android.view.View
import com.example.naturepalestinesociety.R
import com.example.naturepalestinesociety.activities.ChatActivity
import com.example.naturepalestinesociety.helpers.*
import com.example.naturepalestinesociety.models.User
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.custom_chats.view.*
import kotlinx.android.synthetic.main.received_message.view.*
import java.sql.Timestamp
import java.util.*

class ChatReceivedItem(val activity: Activity, val message: ChatMessage, val user: User) :
    Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
//        if (message.text.toString().isNotEmpty()) {
//            viewHolder.itemView.txtReceivedMessage.text = message.text
//            viewHolder.itemView.txtReceivedMessage.visibility = View.VISIBLE
//        } else {
//            viewHolder.itemView.txtReceivedMessage.visibility = View.GONE
//        }
//
//        if (message.image.toString().isNotEmpty()) {
//            viewHolder.itemView.imgReceivedMessage.loadImage(message.image!!)
//            viewHolder.itemView.imgReceivedMessage.visibility = View.VISIBLE
//            viewHolder.itemView.imgReceivedMessage.setOnClickListener {
//                activity.focusImage(message.image)
//            }
//        } else {
//            viewHolder.itemView.imgReceivedMessage.visibility = View.GONE
//        }
//
//        viewHolder.itemView.imgReceivedUser.loadImage(user.photo)
//        viewHolder.itemView.txtReceiverUsername.text = user.name
//
//        val stamp = Timestamp(message.timestamp * 1000)
//        val date = Date(stamp.time)
//
//        viewHolder.itemView.txtReceivedMessageTime.text = activity.findDifference(
//            date.toDateTimeFormated(),
//            Calendar.getInstance().time.toDateTimeFormated()
//        )


    }


    override fun getLayout(): Int {
        return R.layout.received_message
    }
}
