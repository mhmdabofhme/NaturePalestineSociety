package com.example.naturepalestinesociety.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.res.ResourcesCompat
import com.example.naturepalestinesociety.helpers.Constants.CHANNEL_ID
import com.example.naturepalestinesociety.helpers.Constants.NOTIFICATION_BODY
import com.example.naturepalestinesociety.helpers.Constants.NOTIFICATION_IMAGE
import com.example.naturepalestinesociety.helpers.Constants.NOTIFICATION_TITLE
import com.example.naturepalestinesociety.helpers.TAG
import com.example.naturepalestinesociety.R
import com.example.naturepalestinesociety.activities.MainActivity
import com.example.naturepalestinesociety.helpers.Constants.NOTIFICATION
import com.example.naturepalestinesociety.helpers.Constants.NOTIFICATION_CREATED_AT
import com.example.naturepalestinesociety.models.notifications.NotificationModel
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.*

class FirebaseNotificationService : FirebaseMessagingService() {
    lateinit var notificationModel: NotificationModel

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "onMessageReceived: ${remoteMessage}")
        Log.d(TAG, "onMessageReceived:data ${remoteMessage.data}")
        Log.d(TAG, "onMessageReceived:notification body ${remoteMessage.notification?.body}")
        Log.d(TAG, "onMessageReceived:notification title ${remoteMessage.notification?.title}")
        if (remoteMessage.data.isNotEmpty()) {
            notificationModel = NotificationModel()
            val map = remoteMessage.data
            val image = map[NOTIFICATION_IMAGE] ?: "image"
            val title = map[NOTIFICATION_TITLE] ?: remoteMessage.notification?.title.toString()
            val body = map[NOTIFICATION_BODY] ?: remoteMessage.notification?.body.toString()
            val createdAt = map[NOTIFICATION_CREATED_AT] ?: "-1"
//            val screen = map[NOTIFICATION_SCREEN] ?: "Notification"

            Log.d(TAG, "onMessageReceived: $image")
            Log.d(TAG, "onMessageReceived:title $title")
            Log.d(TAG, "onMessageReceived:title $body")
            Log.d(TAG, "onMessageReceived:createdAt $createdAt")
//            Log.d(TAG, "onMessageReceived:screen $screen")

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) createOreoNotification(
//                image,
                title,
                body,
                createdAt,
//                screen
            ) else createNormalNotification(
//                image,
                title,
                body,
                createdAt,
//                screen
            )
        } else Log.d("TAG", "onMessageReceived: no data ")
        super.onMessageReceived(remoteMessage)
    }

    override fun onNewToken(s: String) {
        super.onNewToken(s)
    }

    private fun createNormalNotification(
//        image: String,
        title: String,
        body: String,
        createdAt: String,
//        screen: String
    ) {
//
//        val bitmap = if (Build.VERSION.SDK_INT >= P){
//            ImageDecoder.createSource(contentResolver, Uri.parse(image))
//        }else {
//            MediaStore.Images.Media.getBitmap(contentResolver,Uri.parse(image))
//        }
//
//
        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(this, CHANNEL_ID)
        builder.setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSmallIcon(R.drawable.image_2)
            .setAutoCancel(true)
            .setColor(ResourcesCompat.getColor(resources, R.color.primary, null))
            .setSound(uri)

//        Glide.with(applicationContext)
//            .asBitmap()
//            .load(image)
//            .into(object : CustomTarget<Bitmap>() {
//                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
//                    val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
//                    val builder: NotificationCompat.Builder =
//                        NotificationCompat.Builder(this@FirebaseNotificationService, CHANNEL_ID)
//                    builder.setContentTitle(title)
//                        .setContentText(body)
//                        .setPriority(NotificationCompat.PRIORITY_HIGH)
//                        .setSmallIcon(R.drawable.image_2)
//                        .setAutoCancel(true)
//                        .setColor(ResourcesCompat.getColor(resources, R.color.primary, null))
//                        .setSound(uri)
//                        .setLargeIcon(resource)
//                        .setStyle(
//                            NotificationCompat.BigPictureStyle().bigPicture(resource)
//                        )
//
//                    val intent = Intent(this@FirebaseNotificationService, MainActivity::class.java)
//                    notificationModel = NotificationModel(image, title, body, createdAt,screen)
//                    intent.putExtra(NOTIFICATION, notificationModel)
//                    val pendingIntent = PendingIntent.getActivity(
//                        this@FirebaseNotificationService,
//                        0,
//                        intent,
//                        PendingIntent.FLAG_ONE_SHOT
//                    )
//                    builder.setContentIntent(pendingIntent)
//                    val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
//                    manager.notify(Random().nextInt(85 - 65), builder.build())
//
//                }
//
//                override fun onLoadCleared(placeholder: Drawable?) {}
//                override fun onLoadFailed(errorDrawable: Drawable?) {
//                    super.onLoadFailed(errorDrawable)
//                }
//            })
//
        val intent = Intent(this, MainActivity::class.java)
        notificationModel = NotificationModel(/*image,*/ title, body, createdAt/*, screen*/)
        Log.d(TAG, "createNormalNotification: $notificationModel")
        Log.d(TAG, "createNormalNotification: ${notificationModel.body}")
        Log.d(TAG, "createNormalNotification: ${notificationModel.title}")
        intent.putExtra(NOTIFICATION, notificationModel)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        builder.setContentIntent(pendingIntent)
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(Random().nextInt(85 - 65), builder.build())
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun createOreoNotification(
//        image: String?,
        title: String?,
        body: String?,
        createdAt: String?,
//        screen: String?
    ) {


        Log.d(TAG, "createOreoNotification: $notificationModel")
        Log.d(TAG, "createOreoNotification: ${notificationModel.body}")
        Log.d(TAG, "createOreoNotification: ${notificationModel.title}")


//        Glide.with(applicationContext)
//            .asBitmap()
//            .load(image)
//            .into(object : CustomTarget<Bitmap>() {
//                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {

        val channel =
            NotificationChannel(
                CHANNEL_ID,
                "Message",
                NotificationManager.IMPORTANCE_HIGH
            )
        channel.setShowBadge(true)
        channel.enableLights(true)
        channel.enableVibration(true)
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
        val intent = Intent(this@FirebaseNotificationService, MainActivity::class.java)
        if (title != null && body != null) {
            notificationModel = NotificationModel(/*image,*/ title, body, createdAt/*, screen*/)
            intent.putExtra(NOTIFICATION, notificationModel)
        }
        val pendingIntent =
            PendingIntent.getActivity(
                this@FirebaseNotificationService,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )
        val notification: Notification =
            Notification.Builder(this@FirebaseNotificationService, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(body)

//            .setColor(ContextCompat.getColor(this, R.color.primary))
                .setColor(ResourcesCompat.getColor(resources, R.color.primary, null))
                .setSmallIcon(R.drawable.image_2)
//                            .setLargeIcon(resource) //largeIcon
//                            .setStyle(
//                                NotificationCompat.BigPictureStyle().bigPicture(resource)
//                            )
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()

        manager.notify(100, notification)


    }

}