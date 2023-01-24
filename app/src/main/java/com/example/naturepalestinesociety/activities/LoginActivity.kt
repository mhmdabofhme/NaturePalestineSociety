package com.example.naturepalestinesociety.activities

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.Window
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.naturepalestinesociety.R
import com.example.naturepalestinesociety.databinding.ActivityLoginBinding
import com.example.naturepalestinesociety.databinding.BottomSheetPasswordBinding
import com.example.naturepalestinesociety.helpers.*
import com.example.naturepalestinesociety.models.User
import com.example.naturepalestinesociety.models.country.Country
import com.example.naturepalestinesociety.models.login.Login
import com.example.naturepalestinesociety.models.login.Register
import com.example.naturepalestinesociety.models.login.UserToken
import com.example.naturepalestinesociety.utils.Status
import com.example.naturepalestinesociety.viewmodel.MainViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.custom_chats.view.*


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    private var showLoginPassword = false
    private var showRegisterPassword = false
    private val viewModel by viewModels<MainViewModel>()
    private var countries = mutableListOf<Country>()
    var countryId = ""



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setLightStatusBar(window, true)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.statusBarColor = ContextCompat.getColor(this, R.color.bg_dark)

        viewModel.init(this)


        passwordEye()
        setShadow()
        getCountries()

        binding.spinnerRegisterCountry.setOnSpinnerItemSelectedListener<String> { oldIndex, oldItem, newIndex, newItem ->
            Log.d(TAG, "spinnerRegisterCountry: $newItem")
            for (country in countries!!) {
                if (country.name == newItem) {
                    countryId = country.id.toString()
                    Log.d(TAG, "spinnerRegisterCountry:countryId ${countryId}")
                }
            }
        }

        binding.txtAlreadyHaveAccount.setOnClickListener {
            binding.layoutLogin.visibility = View.VISIBLE
            binding.layoutRegister.visibility = View.GONE
        }

        binding.btnRegister.setOnClickListener {
            binding.layoutLogin.visibility = View.GONE
            binding.layoutRegister.visibility = View.VISIBLE
        }

        binding.btnRegisterLogin.setOnClickListener {
            if (isValidRegister()) {
                generateFcmToken(Constants.REGISTER)

            }
        }

        binding.btnLogin.setOnClickListener {
            if (isValidLogin()) {
                generateFcmToken(Constants.LOGIN)
            }
        }

        binding.txtForgotPassword.setOnClickListener {
            showBottomSheet()
        }

        binding.editEmail.afterTextChange { i, s ->
            if (i > 0) {
                if (s.isValidEmail()) {
                    binding.imgCheck.setImageResource(R.drawable.check_circle)
                    binding.imgCheck.setTint(R.color.green)
                    binding.editEmail.background =
                        ContextCompat.getDrawable(this, R.drawable.shape_edit_text)
                    binding.txtEmailError.visibility = View.GONE
                }
            }
        }

        binding.editRegisterEmail.afterTextChange { i, s ->
            if (i > 0) {
                if (s.isValidEmail()) {
                    binding.imgRegisterCheck.setImageResource(R.drawable.check_circle)
                    binding.imgRegisterCheck.setTint(R.color.green)
                    binding.editRegisterEmail.background =
                        ContextCompat.getDrawable(this, R.drawable.shape_edit_text)
                    binding.txtRegisterEmailError.visibility = View.GONE
                }
            }
        }


    }

    private fun setShadow() {
        binding.txtWelcomeBack.setShadowLayer(
            4F,
            0F,
            6F,
            ContextCompat.getColor(this, R.color.black_40)
        )
        binding.txtCreateAccount.setShadowLayer(
            4F,
            0F,
            6F,
            ContextCompat.getColor(this, R.color.black_40)
        )
    }

    private fun passwordEye() {
        binding.imgPassword.setOnClickListener {
            showLoginPassword = !showLoginPassword
            if (showLoginPassword) {
                binding.editPassword.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
            } else {
                binding.editPassword.transformationMethod =
                    PasswordTransformationMethod.getInstance()
            }
        }
        binding.imgRegisterPassword.setOnClickListener {
            showRegisterPassword = !showRegisterPassword
            if (showRegisterPassword) {
                binding.editRegisterPassword.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
            } else {
                binding.editRegisterPassword.transformationMethod =
                    PasswordTransformationMethod.getInstance()
            }
        }

    }


    private fun showBottomSheet() {

        val dialog = BottomSheetDialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val dialogBinding = BottomSheetPasswordBinding.inflate(layoutInflater)

        dialog.setContentView(dialogBinding.root)
        dialogBinding.imgCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.btnResend.setOnClickListener {

        }
        dialog.setCancelable(true)
        dialog.show()

    }

    private fun login(data: Login) {
        viewModel.login(data).observe(this) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        if (it.data?.body()?.data != null) {
                            val token: String? = it.data?.body()?.token
                            Log.d("token", "login: $token")
                            val user: User = it.data?.body()?.data!!
                            SharedPreferencesApp(this).saveUser(user)
                            SharedPreferencesApp(this).saveToken(token)
                            SharedPreferencesApp(this).saveText(
                                Constants.USER_ID,
                                it.data?.body()?.data?.id.toString()
                            )
                            SharedPreferencesApp(this).saveText(
                                Constants.USER_EMAIL,
                                it.data?.body()?.data?.email
                            )
                            SharedPreferencesApp(this).saveText(
                                Constants.USER_NAME,
                                it.data?.body()?.data?.name
                            )
                            startActivity(Intent(this, MainActivity::class.java))
                            finishAffinity()

                        } else {
                            Log.d(TAG, "login: ${it.message}")
                            toastError(getString(R.string.error_login))
                        }
                    }
                    Status.ERROR -> {
                        Log.d(TAG, "login: ${it.message}")
                        toastError(it.message)
                    }
                    Status.LOADING -> {

                    }
                }
            }
        }
    }

    private fun register(data: Register) {
        viewModel.register(data).observe(this) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        if (it.data?.body()?.data != null) {
                            val token: String? = it.data.body()?.token
                            Log.d("token", "register: $token")

                            val user: User = it.data.body()?.data!! as User

                            SharedPreferencesApp(this).saveUser(user)
                            SharedPreferencesApp(this).saveToken(token)
                            SharedPreferencesApp(this).saveText(
                                Constants.USER_ID,
                                user.id.toString()
                            )
                            SharedPreferencesApp(this).saveText(
                                Constants.USER_EMAIL,
                                user.email
                            )
                            SharedPreferencesApp(this).saveText(
                                Constants.USER_NAME,
                                user.name
                            )
                            startActivity(Intent(this, MainActivity::class.java))
                            finishAffinity()

                        } else {
                            Log.d(TAG, "register: ${it.message}")

                            toastError(getString(R.string.error_register))
                        }
                    }
                    Status.ERROR -> {
                        Log.d(TAG, "register: ${it.message}")
                        toastError(getString(R.string.error_register))
                    }
                    Status.LOADING -> {
                    }
                }
            }
        }
    }

    private fun generateFcmToken(status: String) {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val fcmToken = task.result.toString()
                    SharedPreferencesApp.getInstance(this).saveText(Constants.FCM_TOKEN, fcmToken)
                    Log.d("TAG", "onComplete: $fcmToken")
                    if (status == Constants.LOGIN) {
                        login(
                            Login(
                                binding.editEmail.text.toString(),
                                binding.editPassword.text.toString(),
                                fcmToken
                            )
                        )
                    } else if (status == Constants.REGISTER) {
                        register(
                            Register(
                                binding.editRegisterFullName.text.toString(),
                                binding.editRegisterFullName.text.toString(),
                                binding.editRegisterEmail.text.toString(),
                                binding.editRegisterPassword.text.toString(),
                                countryId,
                                fcmToken
                            )
                        )
                    }
                }
            }

    }

    private fun getCountries() {
        viewModel.getCountries().observe(this) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        if (it.data?.body()?.data != null) {
                            countries.addAll(it.data.body()?.data!!)
                            var list = mutableListOf<String>()
                            for (country in countries) {
                                list.add(country.name)
                            }
                            binding.spinnerRegisterCountry.setItems(list)
                        }
                    }
                    Status.ERROR -> {
                        Log.d(TAG, "getCountries: ${it.message}")
                        toastError(it.message)
                    }
                    Status.LOADING -> {

                    }
                }
            }
        }
    }


    private fun isValidLogin(): Boolean {
        val email = binding.editEmail.text.toString().trim()
        val password = binding.editPassword.text.toString().trim()

        if (email.isEmpty()) {
            binding.imgCheck.setImageResource(R.drawable.ic_error)
            binding.txtEmailError.apply {
                visibility = View.VISIBLE
                text = getString(R.string.email_field_is_required)
            }
            binding.imgCheck.setTint(R.color.RED)
            binding.editEmail.setHintTextColor(ContextCompat.getColor(this, R.color.RED))

            binding.editEmail.background =
                ContextCompat.getDrawable(this, R.drawable.shape_red_border)

        } else if (!email.isValidEmail()) {
            binding.imgCheck.setImageResource(R.drawable.ic_error)
            binding.imgCheck.setTint(R.color.secondary_text)
            binding.txtEmailError.apply {
                visibility = View.VISIBLE
                text = getString(R.string.enter_valid_email)
            }
            binding.editEmail.background =
                ContextCompat.getDrawable(this, R.drawable.shape_red_border)
        } else if (password.isEmpty()) {
            binding.imgPassword.setImageResource(R.drawable.ic_error)
            binding.txtPasswordError.apply {
                visibility = View.VISIBLE
                text = getString(R.string.password_field_is_required)
            }
            binding.editEmail.setHintTextColor(ContextCompat.getColor(this, R.color.RED))
            binding.editPassword.background =
                ContextCompat.getDrawable(this, R.drawable.shape_red_border)

        } else if (!password.isPassword()) {
            binding.imgPassword.setImageResource(R.drawable.eye)
            binding.txtPasswordError.apply {
                visibility = View.VISIBLE
                text = getString(R.string.enter_valid_password)
            }
            binding.editPassword.background =
                ContextCompat.getDrawable(this, R.drawable.shape_red_border)
        }



        return if (email.isValidEmail() && password.isPassword()) {
            binding.imgCheck.setImageResource(R.drawable.check_circle)
            binding.imgCheck.setTint(R.color.green)
            binding.imgPassword.setImageResource(R.drawable.eye)
            binding.txtEmailError.visibility = View.GONE
            binding.txtPasswordError.visibility = View.GONE
            binding.editEmail.background =
                ContextCompat.getDrawable(this, R.drawable.shape_edit_text)
            binding.editPassword.background =
                ContextCompat.getDrawable(this, R.drawable.shape_edit_text)
            true
        } else false

    }

    private fun isValidRegister(): Boolean {
        Log.d(TAG, "isValidRegister: ${countryId}")
        val fullName = binding.editRegisterFullName.text.toString().trim()
        val email = binding.editRegisterEmail.text.toString().trim()
        val password = binding.editRegisterPassword.text.toString().trim()


        if (fullName.isNotEmpty()) {
            binding.txtRegisterFullNameError.apply {
                visibility = View.GONE
                text = getString(R.string.full_name_field_is_required)
            }
            binding.editRegisterFullName.setHintTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.secondary_text
                )
            )
            binding.editRegisterFullName.background =
                ContextCompat.getDrawable(this, R.drawable.shape_edit_text)
        }

        if (fullName.isEmpty()) {
            binding.txtRegisterFullNameError.apply {
                visibility = View.VISIBLE
                text = getString(R.string.full_name_field_is_required)
            }
            binding.editRegisterFullName.setHintTextColor(ContextCompat.getColor(this, R.color.RED))
            binding.editRegisterFullName.background =
                ContextCompat.getDrawable(this, R.drawable.shape_red_border)

        } else if (email.isEmpty()) {
            binding.imgRegisterCheck.setImageResource(R.drawable.ic_error)
            binding.txtRegisterEmailError.apply {
                visibility = View.VISIBLE
                text = getString(R.string.email_field_is_required)
            }
            binding.imgRegisterCheck.setTint(R.color.RED)
            binding.editRegisterEmail.setHintTextColor(ContextCompat.getColor(this, R.color.RED))

            binding.editRegisterEmail.background =
                ContextCompat.getDrawable(this, R.drawable.shape_red_border)

        } else if (!email.isValidEmail()) {
            binding.imgRegisterCheck.setImageResource(R.drawable.ic_error)
            binding.imgRegisterCheck.setTint(R.color.secondary_text)
            binding.txtRegisterEmailError.apply {
                visibility = View.VISIBLE
                text = getString(R.string.enter_valid_email)
            }
            binding.editRegisterEmail.background =
                ContextCompat.getDrawable(this, R.drawable.shape_red_border)
        } else if (password.isEmpty()) {
            binding.imgRegisterPassword.setImageResource(R.drawable.ic_error)
            binding.txtRegisterPasswordError.apply {
                visibility = View.VISIBLE
                text = getString(R.string.password_field_is_required)
            }
            binding.editRegisterEmail.setHintTextColor(ContextCompat.getColor(this, R.color.RED))
            binding.editRegisterPassword.background =
                ContextCompat.getDrawable(this, R.drawable.shape_red_border)

        } else if (!password.isPassword()) {
            binding.imgRegisterPassword.setImageResource(R.drawable.eye)
            binding.txtRegisterPasswordError.apply {
                visibility = View.VISIBLE
                text = getString(R.string.enter_valid_password)
            }
            binding.editRegisterPassword.background =
                ContextCompat.getDrawable(this, R.drawable.shape_red_border)
        } else if (countryId.isEmpty()) {
            binding.spinnerRegisterCountry.error =
                getString(R.string.country) + " " + getString(R.string.is_required)

        }


        return if (email.isValidEmail() && password.isPassword() && countryId.isNotEmpty() && fullName.isNotEmpty()) {
            binding.imgRegisterCheck.setImageResource(R.drawable.check_circle)
            binding.imgRegisterCheck.setTint(R.color.green)
            binding.imgRegisterPassword.setImageResource(R.drawable.eye)
            binding.txtRegisterFullNameError.visibility = View.GONE
            binding.txtRegisterEmailError.visibility = View.GONE
            binding.txtRegisterPasswordError.visibility = View.GONE
            binding.editRegisterFullName.background =
                ContextCompat.getDrawable(this, R.drawable.shape_edit_text)
            binding.editRegisterEmail.background =
                ContextCompat.getDrawable(this, R.drawable.shape_edit_text)
            binding.editRegisterPassword.background =
                ContextCompat.getDrawable(this, R.drawable.shape_edit_text)
            true
        } else false
    }


}