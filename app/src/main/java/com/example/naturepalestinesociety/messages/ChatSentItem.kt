package com.example.naturepalestinesociety.messages

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.annotation.CallSuper
import com.example.naturepalestinesociety.helpers.loadImage
import com.example.naturepalestinesociety.R
import com.example.naturepalestinesociety.activities.ChatActivity
import com.example.naturepalestinesociety.helpers.findDifference
import com.example.naturepalestinesociety.helpers.focusImage
import com.example.naturepalestinesociety.helpers.toDateTimeFormated
import com.example.naturepalestinesociety.models.User
import com.example.naturepalestinesociety.models.profile.Profile
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.received_message.view.*
import kotlinx.android.synthetic.main.sent_message.view.*
import okhttp3.internal.notify
import java.sql.Timestamp
import java.util.*


class ChatSentItem(val activity: Activity,val message: ChatMessage, val user: Profile?) :
    Item<GroupieViewHolder>() {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {

//        if (message.text.toString().isNotEmpty()) {
//            viewHolder.itemView.txtSentMessage.text = message.text
//            viewHolder.itemView.txtSentMessage.visibility = View.VISIBLE
//        } else {
//            viewHolder.itemView.txtSentMessage.visibility = View.GONE
//        }
//
//        if (message.image.toString().isNotEmpty()) {
//            viewHolder.itemView.imgSentMessage.loadImage(message.image!!)
//            viewHolder.itemView.imgSentMessage.visibility = View.VISIBLE
//            viewHolder.itemView.imgSentMessage.setOnClickListener {
//                activity.focusImage(message.image)
//            }
//        } else {
//            viewHolder.itemView.imgSentMessage.visibility = View.GONE
//        }
//
//        Log.d("TAG", "user: ${user?.photo}")
//
//        if (user != null) {
//            viewHolder.itemView.imgSentUser.loadImage(user.photo)
//            viewHolder.itemView.txtSenderUsername.text = user.name
//        }
//
//
//        val stamp = Timestamp(message.timestamp * 1000)
//        val date = Date(stamp.time)
//
//        viewHolder.itemView.txtSenderMessageTime.text = activity.findDifference(
//            date.toDateTimeFormated(),
//            Calendar.getInstance().time.toDateTimeFormated()
//        )

//        Log.d("TAG", "bind:onChildChanged text ${message.text}")
//        Log.d("TAG", "bind:onChildChanged seen ${message.seen}")
//        Log.d("TAG", "bind:onChildChanged timestamp ${message.timestamp}")
//        Log.d("TAG", "bind:seen ${message.seen}")
//
//        Log.d("TAG", "update:seen ${message.seen}")
//        Log.d("TAG", "update:text ${message.text}")
//
//        if (message.seen) {
//            viewHolder.itemView.txtSentMessageSeen.text = activity?.getString(R.string.seen)
//        } else {
//            viewHolder.itemView.txtSentMessageSeen.text = activity?.getString(R.string.sent)
//        }

    }

//
//    fun update(seen: Boolean) {
//        Log.d("TAG", "update:Boolean $seen")
//        message.seen = true
//        message.updateSeen(seen)
////        val textView = this.activity.findViewById<TextView>(R.id.txtSentMessageSeen)
////        textView.text = this.activity.getString(R.string.new_message)
//        Log.d("TAG", "update:message.seen ${message.seen}")
////        this.notifyChanged()
//    }
//

    override fun getLayout(): Int {
        return R.layout.sent_message
    }

//    @CallSuper
//    fun notifyItemChanged(position: Int) {
//        parentDataObserver?.onItemChanged(this, position)
//    }

}

