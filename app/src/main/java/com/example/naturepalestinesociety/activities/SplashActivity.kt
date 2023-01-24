package com.example.naturepalestinesociety.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.animation.AnimationUtils
import com.example.naturepalestinesociety.R
import com.example.naturepalestinesociety.databinding.ActivitySplashBinding
import com.example.naturepalestinesociety.helpers.*

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setFullScreen(window)
        setLightStatusBar(window, true)

        val animation = AnimationUtils.loadAnimation(this, R.anim.scale)
        binding.imgLogo.startAnimation(animation)

        val language = SharedPreferencesApp.getInstance(this).getText(Constants.LANG, "")
        Log.d("TAG", "language splash: $language")
        val token = SharedPreferencesApp.getInstance(this).getToken()
        Log.d("TAG", "token splash: $token")
        val fcmToken =
            SharedPreferencesApp.getInstance(this).getText(Constants.FCM_TOKEN, "").toString()
        Log.d("TAG", "fcmToken splash: $fcmToken")

        Handler().postDelayed({
            this.RTL()
            if (SharedPreferencesApp(this).getToken() != null && fcmToken.isNotEmpty()) {
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
            }
            finishAffinity()
        }, 1000)

    }
}