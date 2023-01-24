package com.example.naturepalestinesociety.helpers

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.util.Log
import com.example.naturepalestinesociety.models.User
import com.google.gson.Gson
import java.util.*

/**
 * todo: Created by Fawzi Nofal on 3/29/2021 1:56 PM.
 */
class SharedPreferencesApp(context: Context) {
    private var preferences: SharedPreferences =
        context.getSharedPreferences("FindUp", MODE_PRIVATE)
    private var editor: SharedPreferences.Editor = preferences.edit()

    fun saveText(Key: String?, value: String?) {
        editor.putString(Key, value).apply()
    }

    fun saveUser(user: User?) {
        editor.putString(Constants.USER, Gson().toJson(user)).apply()
    }
//
    fun getUser(): User? {
        return Gson().fromJson(preferences.getString(Constants.USER, "{}"), User::class.java)
    }

    fun saveToken(value: String?) {
        editor.putString(Constants.TOKEN, "Bearer $value").apply()
    }

    fun saveLatitude(Value: String?) {
        editor.putString(Constants.LATITUDE, Value).apply()
    }

    fun saveLongitude(Value: String?) {
        editor.putString(Constants.LONGITUDE, Value).apply()
    }

    fun saveNumber(Key: String?, value: Int) {
        editor.putInt(Key, value).apply()
    }

    fun saveBoolean(Key: String?, value: Boolean) {
        editor.putBoolean(Key, value).apply()
    }

    fun getText(Key: String, defaultValue: String?): String? {
//        var defaultValue = defaultValue
//        if (Key == Constants.LANG) {
//            defaultValue = null
//        }
        var data = preferences.getString(Key, defaultValue)
//        if ((Key == Constants.LANG) and (data == null)) {
//            data = Locale.getDefault().language
//        }
//        Log.d("TAG", "getText: $Key" + data)
        return data
    }

    fun getToken(): String? {
        return preferences.getString(Constants.TOKEN, null)
    }

    fun getLatitude(): String? {
        return preferences.getString(Constants.LATITUDE, null)
    }

    fun getLongitude(): String? {
        return preferences.getString(Constants.LONGITUDE, null)
    }

    fun getNumber(Key: String?, defaultValue: Int): Int {
        return preferences.getInt(Key, defaultValue)
    }

    fun getBoolean(Key: String?, defaultValue: Boolean): Boolean {
        return preferences.getBoolean(Key, defaultValue)
    }

    fun clear() {
        val lang = getText(Constants.LANG, "en")
//        val IS_LANG = getBoolean(Constants.IS_LANG, false)
        editor.clear()

        editor.remove(Constants.USER)
        editor.remove(Constants.LANG)
        editor.remove(Constants.IS_LOGGED_IN)
        editor.remove(Constants.TOKEN)
        editor.remove(Constants.LATITUDE)
        editor.remove(Constants.LONGITUDE)
        editor.remove(Constants.USER)
        editor.remove(Constants.USER_ID)
        editor.remove(Constants.USER_NAME)
        editor.remove(Constants.USER_EMAIL)
        editor.commit()
//        saveBoolean(Constants.INTRO, true)
        saveText(Constants.LANG, lang)
//        saveBoolean(Constants.IS_LANG, IS_LANG)
    }

    companion object {
        fun getInstance(context: Context): SharedPreferencesApp {
            return SharedPreferencesApp(context)
        }
    }

}