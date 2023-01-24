package com.example.naturepalestinesociety.network

import android.content.Context
import android.util.Log
import com.example.naturepalestinesociety.helpers.Constants
import com.example.naturepalestinesociety.helpers.SharedPreferencesApp
import com.example.naturepalestinesociety.helpers.getLang
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {

    // please add your own url

    fun Context.apiClient(): Retrofit {
        val gson = GsonBuilder()
            .setLenient()
            .create()
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        val client = OkHttpClient.Builder().addInterceptor {
            val requestBuilder = it.request().newBuilder()
                .addHeader(
                "x-localization",
                getLang() ?: "en"
            )
                .addHeader(
                "Accept",
                "*/*"
            ).addHeader(
                "Accept",
                "application/json"
            )
                .addHeader(
                "Content-type",
                "multipart/form-data; boundary=<calculated when request is sent>"
            )
//            requestBuilder.addHeader(
//                "Content-Language",
//                SharedPreferencesApp.getInstance(this).getText(Constants.LANG, "ar") ?: "en"
//            )
//            requestBuilder.addHeader(
//                "X-Client-Language",
//                SharedPreferencesApp.getInstance(this).getText(Constants.LANG, "ar") ?: "en"
//            )
//            requestBuilder.addHeader(
//                "X-Client-FCM-token",
//                SharedPreferencesApp.getInstance(this).getText("fcm", "ar") ?: "en"
//            )
            val token = SharedPreferencesApp.getInstance(this).getToken()
            Log.d("TAG", "apiClient: $token")
            if (token != null)
                requestBuilder.addHeader("Authorization", token)

            val request = requestBuilder.build()
            return@addInterceptor it.proceed(request)
        }
        client.callTimeout(120,TimeUnit.SECONDS)
        val retrofit =Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(client.build())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        return retrofit

    }

}