package com.example.naturepalestinesociety.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.naturepalestinesociety.R
import com.example.naturepalestinesociety.databinding.ReceivedMessageBinding
import com.example.naturepalestinesociety.databinding.SentMessageBinding
import com.example.naturepalestinesociety.helpers.*
import com.example.naturepalestinesociety.messages.ChatMessage
import com.example.naturepalestinesociety.models.User
import com.example.naturepalestinesociety.models.profile.Profile
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.sql.Timestamp
import java.util.*


class MessagesAdapter(
    activity: Activity,
    messages: MutableList<ChatMessage>,
    user: User?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    constructor() : this(Activity(), mutableListOf<ChatMessage>(),null)

    private lateinit var messages: MutableList<ChatMessage>
    private val activity: Activity
    private val ITEM_SENT = 1
    private val ITEM_RECEIVE = 2

    private var user: User?

    inner class SentMsgHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var binding: SentMessageBinding = SentMessageBinding.bind(itemView)
    }

    inner class ReceivedMsgHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var binding: ReceivedMessageBinding = ReceivedMessageBinding.bind(itemView)
    }
//
//    @SuppressLint("NotifyDataSetChanged")
//    fun addMessage(message: ChatMessage) {
//        messages.add(messages.size - 1, message)
//        notifyDataSetChanged()
//    }

    init {
        this.activity = activity
        this.messages = messages
        this.user = user
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ITEM_SENT) {
            val view: View =
                LayoutInflater.from(activity).inflate(R.layout.sent_message, parent, false)
            SentMsgHolder(view)
        } else {
            val view: View =
                LayoutInflater.from(activity).inflate(R.layout.received_message, parent, false)
            ReceivedMsgHolder(view)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val messages = messages[position]
        return if (SharedPreferencesApp.getInstance(activity)
                .getText(Constants.USER_ID, "") == messages.fromId
        ) {
            ITEM_SENT
        } else {
            return ITEM_RECEIVE
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]

        if (holder.javaClass == SentMsgHolder::class.java) {
            val viewHolder = holder as SentMsgHolder
            viewHolder.binding.apply {

                if (message.text.toString().isNotEmpty()) {
                    txtSentMessage.text = message.text
                    txtSentMessage.visibility = View.VISIBLE
                } else {
                    txtSentMessage.visibility = View.GONE
                }

                if (message.image.toString().isNotEmpty()) {
                    imgSentMessage.loadImage(message.image!!)
                    imgSentMessage.visibility = View.VISIBLE
                    imgSentMessage.setOnClickListener {
                        activity.focusImage(message.image)
                    }
                } else {
                    imgSentMessage.visibility = View.GONE
                }

                val stamp = Timestamp(message.timestamp * 1000)
                val date = Date(stamp.time)
                txtSentMessageTime.text = date.toSmallTime()


                Log.d(TAG, "onBindViewHolder: ${message.seen}")

                if (message.seen) {
                    imgSeen.setImageResource(R.drawable.ic_read)
                } else {
                    imgSeen.setImageResource(R.drawable.ic_sent)
                }


            }

        }

        if (holder.javaClass == ReceivedMsgHolder::class.java) {
            val viewHolder = holder as ReceivedMsgHolder
            viewHolder.binding.apply {

                if (message.text.toString().isNotEmpty()) {
                    txtReceivedMessage.text = message.text
                    txtReceivedMessage.visibility = View.VISIBLE
                } else {
                    txtReceivedMessage.visibility = View.GONE
                }

                if (message.image.toString().isNotEmpty()) {
                    imgReceivedMessage.loadImage(message.image!!)
                    imgReceivedMessage.visibility = View.VISIBLE
                    imgReceivedMessage.setOnClickListener {
                        activity.focusImage(message.image)
                    }
                } else {
                    imgReceivedMessage.visibility = View.GONE
                }

                val stamp = Timestamp(message.timestamp * 1000)
                val date = Date(stamp.time)


                txtReceivedMessageTime.text = date.toSmallTime()


            }


            val filteredList: List<ChatMessage> =
                messages.filter { !it.seen && it.id == message.id }

            for (item in filteredList) {
                Log.d(TAG, "onBindViewHolder: filteredList ${item.id}")
                Log.d(TAG, "onBindViewHolder: filteredList ${item.text}")
                updateSentMessage(item)
                updateLatestMessage(item)

            }
        }

    }

    override fun getItemCount(): Int {
        return messages.size
    }

    private fun updateSentMessage(item: ChatMessage) {
        val reference = FirebaseDatabase.getInstance()
            .getReference("/user-messages/${item.fromId}/${item.toId}/${item.id}")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java)
                if (chatMessage != null) {
                    if (chatMessage.fromId == user?.id.toString()) {
                        Log.d(TAG, "chatMessage onDataChange: true")
                        val hashMap: MutableMap<String, Any> = HashMap()
                        hashMap["seen"] = true
                        snapshot.ref.updateChildren(hashMap)
                    } else {
                        Log.d(TAG, "chatMessage onDataChange: false")
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "onCancelled: ${error.message}")
            }
        })
    }

    private fun updateLatestMessage(item: ChatMessage) {
        val reference = FirebaseDatabase.getInstance()
            .getReference("/latest-messages/${item.toId}/${item.fromId}")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java)
                if (chatMessage != null) {
                    if (chatMessage.id == item.id) {
                        Log.d(TAG, "chatMessage onDataChange: true")
                        val hashMap: MutableMap<String, Any> = HashMap()
                        hashMap["seen"] = true
                        snapshot.ref.updateChildren(hashMap)
                    } else {
                        Log.d(TAG, "chatMessage onDataChange: false")
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "onCancelled: ${error.message}")
            }
        })
    }


}