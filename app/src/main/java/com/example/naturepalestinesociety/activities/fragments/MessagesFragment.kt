package com.example.naturepalestinesociety.activities.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.naturepalestinesociety.R
import com.example.naturepalestinesociety.activities.ChatActivity
import com.example.naturepalestinesociety.activities.LoginActivity
import com.example.naturepalestinesociety.activities.NewMessagesActivity
import com.example.naturepalestinesociety.adapters.StoriesAdapter
import com.example.naturepalestinesociety.databinding.FragmentMessagesBinding
import com.example.naturepalestinesociety.helpers.Constants
import com.example.naturepalestinesociety.helpers.SharedPreferencesApp
import com.example.naturepalestinesociety.helpers.TAG
import com.example.naturepalestinesociety.helpers.toastError
import com.example.naturepalestinesociety.messages.ChatMessage
import com.example.naturepalestinesociety.messages.ChatSentItem
import com.example.naturepalestinesociety.messages.LatestMessagesRaw
import com.example.naturepalestinesociety.models.User
import com.example.naturepalestinesociety.models.follow.Followers
import com.example.naturepalestinesociety.models.follow.Following
import com.example.naturepalestinesociety.models.post.Post
import com.example.naturepalestinesociety.models.profile.Profile
import com.example.naturepalestinesociety.utils.Status
import com.example.naturepalestinesociety.viewmodel.MainViewModel
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder

class MessagesFragment : Fragment() {

    private var _binding: FragmentMessagesBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<MainViewModel>()

    private val adapter = GroupAdapter<GroupieViewHolder>()
    private val latestMessagesMap = HashMap<String, ChatMessage>()
    private var userId: String? = null
    private var userName: String? = null
//    private var profile: Profile? = null

    private var listUsers = mutableListOf<User>()

    //    private var post: Post? = null
    private var posts = mutableListOf<Post>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMessagesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        viewModel.init(requireContext())

        userId = SharedPreferencesApp.getInstance(requireContext()).getText(Constants.USER_ID, "")
            .toString()
        userName =
            SharedPreferencesApp.getInstance(requireContext()).getText(Constants.USER_NAME, "")

        verifyUserIsLoggedIn()

        binding.recChats.adapter = adapter
        listenForLatestMessages()

//        binding.recChats.addItemDecoration(
//            DividerItemDecoration(
//                requireContext(),
//                DividerItemDecoration.VERTICAL
//            )
//        )

        binding.imgNewChat.setOnClickListener {
            startActivity(Intent(requireContext(), NewMessagesActivity::class.java))
        }


        adapter.setOnItemClickListener { item, view ->
            val intent = Intent(view.context, ChatActivity::class.java)
            val row = item as LatestMessagesRaw
            val user: User = row.chatPartnerUser!!
            intent.putExtra(Constants.USER, user)
            startActivity(intent)
        }


        return root
    }


    private fun getUsers() {
        if (view != null) {
            viewModel.getUsers().observe(viewLifecycleOwner) {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            if (it.data?.body()?.data != null) {
                                listUsers.clear()
                                listUsers.addAll(it.data.body()?.data!!.distinctBy { it.id })
                                refreshRecyclerViewMessages(listUsers)
                            }
                        }
                        Status.ERROR -> {
                            Log.d(TAG, "getFollowing: ${it.message}")
                            requireContext().toastError(it.message)
                        }
                        Status.LOADING -> {

                        }
                    }
                }
            }
        }
    }

    private fun refreshRecyclerViewMessages(list: MutableList<User>) {
        adapter.clear()
        val collection = latestMessagesMap.values.sortedByDescending { it.timestamp }
        collection.forEach {
            adapter.add(LatestMessagesRaw(requireActivity(), it, list))
        }

    }


    private fun listenForLatestMessages() {
        val fromId = userId.toString()
        val ref = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId")
        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TAG, "onCreate:snapshot ${snapshot.getValue(ChatMessage::class.java)?.text}")
                val chatMessage = snapshot.getValue(ChatMessage::class.java) ?: return
                latestMessagesMap[snapshot.key!!] = chatMessage
                getUsers()
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java) ?: return
                latestMessagesMap[snapshot.key!!] = chatMessage
                getUsers()
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

    }

    private fun verifyUserIsLoggedIn() {
        if (userId == null || userId?.isEmpty() == true) {
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

}