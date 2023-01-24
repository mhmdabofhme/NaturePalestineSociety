package com.example.naturepalestinesociety.messages

//import com.google.firebase.auth.FirebaseAuth
import android.annotation.SuppressLint
import android.app.Activity
import android.icu.util.TimeZone.SystemTimeZoneType
import android.os.Build
import android.text.format.DateUtils
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelLazy
import androidx.lifecycle.ViewModelProvider
import com.example.naturepalestinesociety.R
import com.example.naturepalestinesociety.helpers.*
import com.example.naturepalestinesociety.models.User
import com.example.naturepalestinesociety.models.follow.Following
import com.example.naturepalestinesociety.viewmodel.MainViewModel
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.custom_chats.view.*
import java.sql.Timestamp
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class LatestMessagesRaw(
    private val activity: Activity,
    private val chatMessage: ChatMessage,
    private val listUsers: List<User>
) :
    Item<GroupieViewHolder>() {

    var chatPartnerUser: User? = null

    @SuppressLint("SimpleDateFormat")
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {

//        val userEmail = SharedPreferencesApp.getInstance(activity).getText(Constants.USER_EMAIL,"0")
//
//        val chatPartnerId = if (chatMessage.fromId == userEmail) {
//            chatMessage.toId
//        } else {
//            chatMessage.fromId
//        }

//        if (listUsers.isNotEmpty()) {
//            Log.d(TAG, "bind:id ${listUsers[position].id}")
//            Log.d(TAG, "bind:targets id ${listUsers[position].targets.id }")
//            Log.d(TAG, "bind:name_en ${listUsers[position].targets.name }")
//            Log.d(TAG, "bind:targets ${listUsers[position].targets}")
//            chatPartnerUser = listUsers[position].targets
//        }

        val stamp = Timestamp(chatMessage.timestamp * 1000)
        val date = Date(stamp.time)

        viewHolder.itemView.txtLatestMessageTime.text = activity.findDifference(
            date.toDateTimeFormated(),
            Calendar.getInstance().time.toDateTimeFormated()
        )

        if (chatMessage.text != null && chatMessage.text.isNotEmpty())
            viewHolder.itemView.txtLatestMessage.text = chatMessage.text
        else viewHolder.itemView.txtLatestMessage.text = activity.getString(R.string.image)

        val chatPartnerId = if (chatMessage.fromId == SharedPreferencesApp.getInstance(activity)
                .getText(Constants.USER_ID, "").toString()
        ) {
            chatMessage.toId
        } else {
            chatMessage.fromId
        }


        for (user in listUsers) {
            if (user.id.toString() == chatPartnerId) {
                chatPartnerUser = user
                viewHolder.itemView.imgLatestMessageUser.loadImage(chatPartnerUser!!.photo)
                viewHolder.itemView.txtLatestMessageUserName.text = chatPartnerUser!!.name
            }
        }


        if (chatMessage.toId == SharedPreferencesApp.getInstance(activity)
                .getText(Constants.USER_ID, "").toString()) {
            if (chatMessage.seen) {
                viewHolder.itemView.imgNew.visibility = View.GONE
                viewHolder.itemView.txtLatestMessage.setTextColor(ContextCompat.getColor(activity,R.color.secondary_text))
            } else {
                viewHolder.itemView.imgNew.visibility = View.VISIBLE
                viewHolder.itemView.txtLatestMessage.setTextColor(ContextCompat.getColor(activity,R.color.primary))
            }
        }else{
            viewHolder.itemView.imgNew.visibility = View.GONE
            viewHolder.itemView.txtLatestMessage.setTextColor(ContextCompat.getColor(activity,R.color.secondary_text))
        }



//        val ref = FirebaseDatabase.getInstance().getReference("/users/$chatPartnerId")
//
//        ref.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                chatPartnerUser = snapshot.getValue(User::class.java) ?: return
//                viewHolder.itemView.imgLatestMessageUser.loadImage(chatPartnerUser!!.profileImageUrl)
//                viewHolder.itemView.txtLatestMessageUserName.text = chatPartnerUser!!.username
//
//            }
//
//            override fun onCancelled(error: DatabaseError) {

//            }
//        })
    }

    override fun getLayout(): Int {
        return R.layout.custom_chats
    }
}