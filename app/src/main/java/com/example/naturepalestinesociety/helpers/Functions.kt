package com.example.naturepalestinesociety.helpers

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.database.Cursor
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.provider.MediaStore
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.widget.ImageViewCompat
import com.bumptech.glide.Glide
import com.example.naturepalestinesociety.R
import com.example.naturepalestinesociety.databinding.DialogImageBinding
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


const val image =
    "https://media.istockphoto.com/id/1016761216/photo/portrait-concept.jpg?s=612x612&w=0&k=20&c=JsGhLiCeBZs7NeUY_3wjDlLfVkgDJcD9UCXeWobe7Ak="
const val TAG = "TAG"


fun setFullScreen(window: Window) {
    WindowCompat.setDecorFitsSystemWindows(window,false)
}

//fun setFullScreen(window: Window) {
//    window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
//    window.statusBarColor = Color.TRANSPARENT
//}

fun setLightStatusBar(window: Window, isLight: Boolean = true) {
    val wic = WindowInsetsControllerCompat(window, window.decorView)
    wic.isAppearanceLightStatusBars = isLight
}

fun Context.showSoftKeyboard(view: View) {
    if (view.requestFocus()) {
        val imm: InputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }
}

fun Activity.focusImage(image: String) {

    val dialog = Dialog(this)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    val dialogBinding = DialogImageBinding.inflate(layoutInflater)
    dialog.setContentView(dialogBinding.root)

    dialogBinding.imgDialog.loadImage(image)

    dialog.setCancelable(true)
    dialog.show()

}


fun ImageView.loadImage(imageUrl: String, img: Int? = null) {
    if (!TextUtils.isEmpty(imageUrl)) {
        Log.e("image url", imageUrl)
        Glide.with(this.context)
            .load(imageUrl/*.replace("http", "https")*/)
            .error(img ?: R.drawable.profile_place_holder)
//            .placeholder(R.drawable.no_image)
            .into(this)
    } else {
        setImageResource(img ?: R.drawable.no_image)
    }
}


fun String.isValidEmail(): Boolean {
    val regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$".toRegex()
    return this.matches(regex)
}


fun String.isPassword(): Boolean {
    return this.length >= 6
}


fun Context.toastError(message: String?) {
    this.toastMessage(message, R.drawable.ic_error_toast, Color.parseColor("#D20A16"))
}

fun Context.toastDone(message: String?) {
    this.toastMessage(message, R.drawable.ic_check_toast_done, Color.parseColor("#1C8F08"))
}


fun Context.toastWarning(message: String?) {
    this.toastMessage(message, R.drawable.ic_error_toast, Color.parseColor("#E9C700"))
}


fun Context.toastMessage(message: String?, icon: Int, color: Int) {
    val linearLayout = LinearLayout(this)
    linearLayout.orientation = LinearLayout.HORIZONTAL
    //Params TextView
    val txtToast = TextView(this)
    txtToast.layoutParams = LinearLayout.LayoutParams(
        ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )
    txtToast.setTextColor(Color.WHITE)
    //Params ImageView
    val imageToast = ImageView(this)
    val imageParams = LinearLayout.LayoutParams(70, 70)
    imageParams.marginEnd = 18
    imageToast.layoutParams = imageParams
    // params linearLayout
    val params: ViewGroup.LayoutParams = LinearLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )
    linearLayout.layoutParams = params
    linearLayout.setPadding(35, 20, 35, 20)
    linearLayout.gravity = Gravity.CENTER
    linearLayout.addView(imageToast)
    linearLayout.addView(txtToast)
    val toast = Toast(this)
    toast.duration = Toast.LENGTH_SHORT
    toast.view = linearLayout
    toast.show()
    txtToast.text = message
    if (icon != 0) imageToast.setImageResource(icon)
    val drawable = GradientDrawable()
    if (color != 0) drawable.setColor(color)
    drawable.cornerRadius = 50f
    linearLayout.background = drawable
}


//fun Activity.openFacebook(fb: String) {
//    try {
//        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("fb://facewebmodal/f?href=$fb"))
//        startActivity(intent)
//    } catch (e: java.lang.Exception) {
//        startActivity(
//            Intent(
//                Intent.ACTION_VIEW,
//                Uri.parse("http://www.facebook.com/$fb")
//            )
//        )
//    }
//
////    fb://facewebmodal/f?href=$fb
//}


fun Activity.openFacebook(facebookUrl: String) {
    var facebookUrl = facebookUrl
    try {
        var uri: Uri? = null
        val versionCode = packageManager
            .getPackageInfo("com.facebook.katana", 0).versionCode
        if (versionCode >= 3002850) {
            facebookUrl = facebookUrl.lowercase(Locale.getDefault()).replace("www.", "m.")
            if (!facebookUrl.startsWith("https")) {
                facebookUrl = "https://$facebookUrl"
            }
            uri = Uri.parse("fb://facewebmodal/f?href=$facebookUrl")
        } else {
            val pageID = facebookUrl.substring(facebookUrl.lastIndexOf("/"))
            uri = Uri.parse("fb://page$pageID")
        }
        Log.d("TAG", "startFacebook: uri = $uri")
    } catch (e: Throwable) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(facebookUrl)))
    }
}


fun Activity.openSnapchat(snapchat: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(snapchat))
        intent.setPackage("com.snapchat.android")
        startActivity(Intent.createChooser(intent, "Open Snapchat"))
    } catch (e: ActivityNotFoundException) {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(snapchat)
            )
        )
    }
}

fun Activity.openInstagram(instagram: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(instagram)
        intent.setPackage("com.instagram.android")
        startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(instagram)
            )
        )
    }
}

fun Activity.openYoutube(youtube: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(youtube)
        intent.setPackage("com.youtube.android")
        startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(youtube)
            )
        )
    }
}

fun Context.openTwitter(user_id: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(user_id)
//        intent.data = Uri.parse("twitter://user?screen_name=$user_id")
        intent.setPackage("com.twitter.android")
        startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(user_id)
            )
        )
    }
}

fun Activity.openPhoneCall(phone: String) {
    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
    this.startActivity(intent)
}


@SuppressLint("SetTextI18n")
fun Activity.showTimePicker(onTimeSelected: (String) -> Unit) {
    var hours = ""
    var minutes = ""
    var time = ""
    val mcurrentTime: Calendar = Calendar.getInstance()
    val hour: Int = mcurrentTime.get(Calendar.HOUR_OF_DAY)
    val minute: Int = mcurrentTime.get(Calendar.MINUTE)
    val mTimePicker: TimePickerDialog
    mTimePicker = TimePickerDialog(
        this,
        android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
        { timePicker: TimePicker?, selectedHour: Int, selectedMinute: Int ->
            if (selectedMinute < 10) {
                if (selectedHour < 10) {
                    hours = "0$selectedHour"
                } else {
                    hours = selectedHour.toString()
                }
                minutes = "0$selectedMinute"
            } else {
                if (selectedHour < 10) {
                    hours = "0$selectedHour"
                    minutes = selectedMinute.toString()
                } else {
                    hours = selectedHour.toString()
                    minutes = selectedMinute.toString()
                }
            }
            time = "$hours:$minutes"
            onTimeSelected.invoke(time)
        },
        hour,
        minute,
        true
    )
    mTimePicker.window!!.setBackgroundDrawableResource(android.R.color.transparent)
    mTimePicker.setTitle(R.string.selectDuration)
    mTimePicker.show()
}

fun Activity.showDatePicker(onDateSelected: (String) -> Unit) {
    val c = Calendar.getInstance()
    val year = c.get(Calendar.YEAR)
    val month = c.get(Calendar.MONTH)
    val day = c.get(Calendar.DAY_OF_MONTH)
    val dpd = DatePickerDialog(
        this,
        { view, year, monthOfYear, dayOfMonth ->
            var monthX = ""
            var dayX = ""
            if ((monthOfYear + 1) < 10) {
                monthX = "0${monthOfYear + 1}"
            } else {
                monthX = "$monthOfYear"
            }

            if (dayOfMonth < 10) {
                dayX = "0${dayOfMonth}"
            } else {
                dayX = "${dayOfMonth}"

            }
            onDateSelected.invoke("$year-${monthX}-$dayX")


        },
        year,
        month,
        day
    )

    dpd.show()

}

fun Activity.RTL() {
    this.changeLanguage(true)
    if (SharedPreferencesApp.getInstance(this)
            .getText(Constants.LANG, Locale.getDefault().language) == "ar"
    ) {
        this.window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL
    } else {
        this.window.decorView.layoutDirection = View.LAYOUT_DIRECTION_LTR
    }
}

fun Activity.changeLanguage(isActivity: Boolean) {
    val myLocale = Locale(
        SharedPreferencesApp.getInstance(this).getText(Constants.LANG, Locale.getDefault().language)
    )
    val config: Configuration = resources.configuration
    config.setLocale(myLocale)
    resources.updateConfiguration(config, resources.displayMetrics)
    window.decorView.layoutDirection = config.layoutDirection
    createConfigurationContext(config)

    if (!isActivity) {
        this.startActivity(Intent(this, this.javaClass))
        this.finish()
    }
}

fun Context.getLang(): String? {
    return SharedPreferencesApp.getInstance(this).getText(Constants.LANG, "en")
}

fun Context.saveLang(lang: String) {
    SharedPreferencesApp.getInstance(this).saveText(Constants.LANG, lang)
}


//fun ImageView.loadImage(imageUrl: String, img: Int? = null) {
//    if (!TextUtils.isEmpty(imageUrl)) {
//        Log.e("image url", imageUrl)
//        Glide.with(this.context)
//            .load(imageUrl/*.replace("http", "https")*/)
//            .error(img ?: R.drawable.no_image)
////            .placeholder(R.drawable.no_image)
//            .into(this)
//    } else {
//        setImageResource(img ?: R.drawable.no_image)
//    }
//}

fun EditText.afterTextChange(onTextChang: (Int, String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(
            charSequence: CharSequence,
            start: Int,
            count: Int,
            i2: Int
        ) {

        }

        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, count: Int) {
        }

        override fun afterTextChanged(editable: Editable) {
            onTextChang.invoke(this.toString().length, editable.toString())
        }
    })
}

fun Context.shareLink(uri: Uri?) {
    val iShare = Intent(Intent.ACTION_SEND)
    iShare.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    iShare.putExtra(Intent.EXTRA_TEXT, uri.toString())
    iShare.type = "text/plain"
    startActivity(iShare)
}

fun Context.getRealPathFromURI(uri: Uri?): String? {
    val proj = arrayOf(MediaStore.Images.Media.DATA)
    val cursor: Cursor? = contentResolver.query(uri!!, proj, null, null, null)
    cursor!!.moveToFirst()
    val idx: Int = cursor!!.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA)
    return cursor.getString(idx)
}

fun Uri.getRealPathFromUri(context: Context): String {
    var cursor: Cursor? = null
    return try {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        Log.d("TAG", "getRealPathFromUri: $this")
        cursor = context.contentResolver.query(this!!, proj, null, null, null)
        var column_index = 0
        if (cursor != null) {
            column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        }
        cursor!!.moveToFirst()
        cursor.getString(column_index)
    } finally {
        cursor?.close()
    }
}

fun ImageView.setTint(@ColorRes colorRes: Int) {
    ImageViewCompat.setImageTintList(
        this,
        ColorStateList.valueOf(ContextCompat.getColor(context, colorRes))
    )
}

fun Date?.toDateFormat(): String {
    val pattern = "yyyy-MM-dd'T'HH:mm:ss"
    return dateAsString(this, pattern)
}

fun dateAsString(date: Date?, pattern: String): String {
    var simpleDateFormat: SimpleDateFormat = SimpleDateFormat(pattern, Locale.getDefault())
    simpleDateFormat = SimpleDateFormat(pattern, Locale.getDefault())
    if (date != null)
        return simpleDateFormat.format(date)
    else
        throw NullPointerException("Date can't be null")
}

fun Date?.toTimeFormated(): String {
    val pattern = "LLL dd, yyyy . HH:mm aa"
    return dateAsString(this, pattern)
}


fun String.apiFormat(): Date {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ", Locale.US)
    return dateFormat.parse(this.replace("Z", "+0000000")) as Date
}

fun Date?.toDateTimeFormated(): String {
    val pattern = "dd-MM-yyyy HH:mm:ss"
    return dateAsString(this, pattern)
}


fun Date?.toSmallTime(): String {
    val pattern = "H:mm"
    return dateAsString(this, pattern)
}

@SuppressLint("SimpleDateFormat")
fun Activity.findDifference(
    start_date: String,
    end_date: String
): String {
    val sdf = SimpleDateFormat(
        "dd-MM-yyyy HH:mm:ss"
    )

    val d1 = sdf.parse(start_date)
    val d2 = sdf.parse(end_date)

    val difference_In_Time = d2.time - d1.time

    val difference_In_Seconds = ((difference_In_Time
            / 1000)
            % 60)
    val difference_In_Minutes = ((difference_In_Time
            / (1000 * 60))
            % 60)
    val difference_In_Hours = ((difference_In_Time
            / (1000 * 60 * 60))
            % 24)
    val difference_In_Years = (difference_In_Time
            / (1000L * 60 * 60 * 24 * 365))
    val difference_In_Days = ((difference_In_Time
            / (1000 * 60 * 60 * 24))
            % 365)

    return if (difference_In_Years != 0L) {
        "$difference_In_Years " + this.getString(R.string.y)
    } else if (difference_In_Days != 0L) {
        "$difference_In_Days " + this.getString(R.string.day)
    } else if (difference_In_Hours != 0L) {
        "$difference_In_Hours " + this.getString(R.string.hour)
    } else if (difference_In_Minutes != 0L) {
        "$difference_In_Minutes " + this.getString(R.string.min)
    } else if (difference_In_Seconds != 0L) {
        "$difference_In_Seconds " + this.getString(R.string.sec)
    } else "now"


}

//fragmentManager.beginTransaction()
//        .replace(R.id.container, TabFragment.newInstance())
//        .addToBackStack(BACK_STACK_ROOT_TAG)
//        .commit();
//    }
//

