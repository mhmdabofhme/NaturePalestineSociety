package com.example.naturepalestinesociety.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.naturepalestinesociety.R
import com.example.naturepalestinesociety.adapters.MessagesAdapter
import com.example.naturepalestinesociety.databinding.ActivityChatBinding
import com.example.naturepalestinesociety.helpers.*
import com.example.naturepalestinesociety.messages.ChatMessage
import com.example.naturepalestinesociety.models.User
import com.example.naturepalestinesociety.models.notifications.NotificationModel
import com.example.naturepalestinesociety.viewmodel.MainViewModel
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.sent_message.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private val viewModel by viewModels<MainViewModel>()

    private var user: User? = null

    private var adapter = MessagesAdapter()
    private var messages = mutableListOf<ChatMessage>()

    private var imageUri: Uri? = null
    private var imagePath: String? = null
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setLightStatusBar(window, true)
        viewModel.init(this)

        userId = SharedPreferencesApp.getInstance(this).getText(Constants.USER_ID, "").toString()
        Log.d(TAG, "userId: $userId")

        binding.imgBack.setOnClickListener { finish() }


        binding.recMessages.adapter = adapter

        user = intent.getSerializableExtra(Constants.USER) as User

        binding.imgUser.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra(Constants.USER, user)
            startActivity(intent)
        }

        binding.txtTitle.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra(Constants.USER, user)
            startActivity(intent)
        }

        binding.txtTitle.text = user?.name
        binding.imgUser.loadImage(user?.photo!!)

        listenForMessages()

        binding.imgSend.setOnClickListener {
            if (binding.editMessage.text.isNotEmpty()) {
                performSendMessage()
            }
        }

        binding.imgAttach.setOnClickListener {
            ImagePicker.with(this)
                .compress(1024)
                .maxResultSize(1080, 1080)
                .start()
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun listenForMessages() {
        val fromId = userId.toString()
        val toId = user?.id

        val reference = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")

        reference.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java)
                if (chatMessage != null) {
                    messages.add(chatMessage)
                    adapter = MessagesAdapter(this@ChatActivity, messages, user)
                    binding.recMessages.adapter = adapter
                    adapter.notifyDataSetChanged()
                    binding.recMessages.scrollToPosition(adapter.itemCount - 1)
                }

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java)
                if (chatMessage != null && user != null) {
                    messages.find { it.id == chatMessage.id && it.seen!=chatMessage.seen}?.seen = true

                    adapter = MessagesAdapter(this@ChatActivity, messages/*,profile*/, user)
                    binding.recMessages.adapter = adapter
                    adapter.notifyDataSetChanged()
                    binding.recMessages.scrollToPosition(adapter.itemCount - 1)
                }

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })


    }

    private fun performSendMessage() {
        var text: String? = null
        if (binding.editMessage.text.toString().isNotEmpty()) {
            text = binding.editMessage.text.toString()
        }
        val fromId = userId.toString()
        val toId = user?.id.toString()

        val reference =
            FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()
        val toReference =
            FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()

        val chatMessage =
            ChatMessage(
                reference.key!!,
                text,
                imagePath,
                fromId,
                toId,
                false,
                System.currentTimeMillis() / 1000
            )

        reference.setValue(chatMessage)
            .addOnSuccessListener {
                binding.recMessages.scrollToPosition(adapter.itemCount - 1)
                binding.editMessage.text.clear()
            }

        toReference.setValue(chatMessage)
            .addOnSuccessListener {
                binding.recMessages.scrollToPosition(adapter.itemCount - 1)
                binding.editMessage.text.clear()
            }

        val latestMessagesRef =
            FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
        latestMessagesRef.setValue(chatMessage)

        val latestMessagesToRef =
            FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")
        latestMessagesToRef.setValue(chatMessage)

        val myUser = SharedPreferencesApp.getInstance(this).getUser() as User
        val title = myUser.name + " " + getString(R.string.sent_you_message)
//        val title = getString(R.string.new_message)
        val body = chatMessage.text ?: getString(R.string.image)
        val currentTime = Calendar.getInstance().time.toTimeFormated()
        addNotification(NotificationModel(/*myUser.photo,*/ title, body, currentTime/*, "screen"*/), toId)

    }


    private fun getUserToken(notificationModel: NotificationModel) {

        val to = JSONObject()
        val data = JSONObject()
        Log.d(TAG, "getUserToken: ${user?.fcm_token}")
        try {
//            data.put(Constants.NOTIFICATION_IMAGE, notificationModel.image)
            data.put(Constants.NOTIFICATION_TITLE, notificationModel.title)
            data.put(Constants.NOTIFICATION_BODY, notificationModel.body)
            data.put(Constants.NOTIFICATION_CREATED_AT, notificationModel.createdAt)
//            data.put(Constants.NOTIFICATION_SCREEN, notificationModel.screen)
            to.put("to", user?.fcm_token)
            to.put("data", data)
            sendNotification(to)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun sendNotification(to: JSONObject) {
        val request =
            object :
                JsonObjectRequest(Request.Method.POST, Constants.NOTIFICATION_URL, to, { response ->
                    Log.d(
                        "notification",
                        "sendNotification: $response"
                    )
                }, { error ->
                    Log.e("TAG notification ", "sendNotification: $error")
                    Log.e("TAG notification ", "sendNotification: ${error.networkResponse}")
                    Log.e("TAG notification ", "sendNotification: ${error.message}")
                    Log.e("TAG notification ", "sendNotification: ${error.localizedMessage}")
                }
                ) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Authorization"] = "key=${Constants.SERVER_KEY}"
                    headers["Content-Type"] = "application/json"
                    return headers
                }

                override fun getBodyContentType(): String {
                    return "application/json"
                }
            }


        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        request.retryPolicy = DefaultRetryPolicy(
            30000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        requestQueue.add(request)
    }

    private fun addNotification(notificationModel: NotificationModel, id: String) {
        Log.d(TAG, "addNotification:userId: $userId")

        val reference =
            FirebaseDatabase.getInstance().getReference("/user-notifications/$id").push()

        reference.setValue(notificationModel)
            .addOnSuccessListener {
                Log.d(TAG, "addNotification: ${reference.key}")
                getUserToken(notificationModel)
            }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                imageUri = data?.data!!
                uploadImageToFirebase()
            }
            ImagePicker.RESULT_ERROR -> {
                Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            }
            else -> {
                Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun uploadImageToFirebase() {
        if (imageUri != null) {
            Log.d(TAG, "uploadImageToFirebase: in")
            val progressDialog = ProgressDialog(this)
            progressDialog.setTitle(getString(R.string.uploading))
            progressDialog.show()
            val filename = UUID.randomUUID().toString()
            val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
            ref.putFile(imageUri!!).addOnSuccessListener { uri ->
                Log.d("TAG", "uploadImageToFirebase: image uploaded ${uri.metadata?.path}")
                ref.downloadUrl.addOnSuccessListener {
                    Log.d("TAG", "uploadImageToFirebase:File location $uri")
                    progressDialog.dismiss()
                    imagePath = it.toString()
                    performSendMessage()
                }
            }.addOnFailureListener {
                progressDialog.dismiss()
                Toast.makeText(
                    this,
                    getString(R.string.failed_upload_image),
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
                .addOnProgressListener { taskSnapshot ->
                    val progress =
                        100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount
                    progressDialog.setMessage(getString(R.string.uploaded) + progress.toInt() + "%")
                }
        }
    }

}

