package com.example.naturepalestinesociety.activities

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import androidx.activity.viewModels
import com.example.naturepalestinesociety.R
import com.example.naturepalestinesociety.databinding.ActivitySettingsBinding
import com.example.naturepalestinesociety.databinding.DialogAddProjectBinding
import com.example.naturepalestinesociety.databinding.DialogLanguageBinding
import com.example.naturepalestinesociety.helpers.*
import com.example.naturepalestinesociety.utils.Status
import com.example.naturepalestinesociety.viewmodel.MainViewModel

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private val viewModel by viewModels<MainViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setFullScreen(window)
        setLightStatusBar(window, true)
        viewModel.init(this)

        binding.imgBack.setOnClickListener { finish() }
        binding.txtBack.setOnClickListener { finish() }

        binding.layoutMyAccount.setOnClickListener { finish() }

        binding.layoutPrivacy.setOnClickListener {
            startActivity(Intent(this, WebViewActivity::class.java))
        }

        binding.layoutContactUs.setOnClickListener {
            startActivity(Intent(this, ContactUsActivity::class.java))
        }

        binding.layoutLogout.setOnClickListener {
            logout()
        }

        binding.layoutLanguage.setOnClickListener {
            showDialog()
        }


    }


    private fun showDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val dialogBinding = DialogLanguageBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)

        Log.d(TAG, "showDialog: ${getLang()}")

        if (getLang() == "en") {
            dialogBinding.btnEnglish.isChecked = true
        } else {
            dialogBinding.btnArabic.isChecked = true
        }

        dialogBinding.btnArabic.setOnClickListener {
            if (getLang() == "en") {
                Log.d("TAG", "showDialog: en")
                saveLang("ar")
                Log.d("TAG", "showDialog: ${getLang()}")
                changeLanguage(true)
                startActivity(Intent(this, MainActivity::class.java))
                finishAffinity()
            }
        }
        dialogBinding.btnEnglish.setOnClickListener {
            if (getLang() == "ar") {
                Log.d("TAG", "showDialog: ar")
                saveLang("en")
                changeLanguage(true)
                startActivity(Intent(this, MainActivity::class.java))
                finishAffinity()

            }
        }
        dialog.show()
    }

    private fun logout() {
        viewModel.logout().observe(this) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        Log.d(TAG, "createFollowing: ${it.data?.body()?.data}")
                        if (it.data?.body()?.data != null) {
                            SharedPreferencesApp.getInstance(this).clear()
                            startActivity(Intent(this, LoginActivity::class.java))
                            finishAffinity()
                        }
                    }
                    Status.ERROR -> {
                        Log.d(TAG, "createFollowing: ${it.message}")
                        toastError(it.message)
                    }
                    Status.LOADING -> {

                    }
                }
            }
        }
    }
}