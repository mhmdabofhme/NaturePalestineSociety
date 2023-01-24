package com.example.naturepalestinesociety.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.naturepalestinesociety.models.contacts.Contacts
import com.example.naturepalestinesociety.models.follow.Follow
import com.example.naturepalestinesociety.models.login.Login
import com.example.naturepalestinesociety.models.login.Register
import com.example.naturepalestinesociety.models.post.RemoveImage
import com.example.naturepalestinesociety.models.project.AddProject
import com.example.naturepalestinesociety.models.search.Search
import com.example.naturepalestinesociety.network.ApiService
import com.example.naturepalestinesociety.network.ApiClient.apiClient
import com.example.naturepalestinesociety.utils.Resource
import kotlinx.coroutines.Dispatchers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File


class MainViewModel : ViewModel {
    private var apiService: ApiService? = null

    constructor()

    constructor(apiService: ApiService) {
        this.apiService = apiService
    }

    fun init(context: Context) {
        apiService = context.apiClient().create(ApiService::class.java)
    }

    fun login(data: Login) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = apiService?.login(data)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }

    fun register(data: Register) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = apiService?.register(data)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }

    //    fun forgotPassword(email: User.UserForgotPassword) = liveData(Dispatchers.IO) {
//        emit(Resource.loading(data = null))
//        try {
//            emit(Resource.success(data = apiService?.forgotPassword(email)))
//        } catch (exception: Exception) {
//            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
//        }
//    }
//
//    fun logout() = liveData(Dispatchers.IO) {
//        emit(Resource.loading(data = null))
//        try {
//            emit(Resource.success(data = apiService?.logout()))
//        } catch (exception: Exception) {
//            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
//        }
//    }
//
//    fun setLanguage(language: User.SetLanguage) = liveData(Dispatchers.IO) {
//        emit(Resource.loading(data = null))
//        try {
//            emit(Resource.success(data = apiService?.setLanguage(language)))
//        } catch (exception: Exception) {
//            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
//        }
//    }
//
//    fun setFcm(fcm: User.SetFcm) = liveData(Dispatchers.IO) {
//        emit(Resource.loading(data = null))
//        try {
//            emit(Resource.success(data = apiService?.setFcm(fcm)))
//        } catch (exception: Exception) {
//            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
//        }
//    }
//
    fun getProfile() = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = apiService?.getProfile()))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }

    fun updateUserProfile(
        name_ar: String? = null,
        name_en: String? = null,
        email: String? = null,
        password: String? = null,
        country_id: String? = null,
        bio_ar: String? = null,
        bio_en: String? = null,
        photo: File? = null,
    ) = liveData(Dispatchers.IO)
    {
        emit(Resource.loading(data = null))
        try {
            val body: MultipartBody.Part?
            val mediaType = "multipart/form-data".toMediaTypeOrNull()
            if (photo != null) {
                val requestFile: RequestBody = photo.asRequestBody(mediaType)
                body = MultipartBody.Part.createFormData("photo", photo.name, requestFile)
            } else {
                body = null
            }
            emit(
                Resource.success(
                    data = apiService?.updateProfile(
                        name_ar = name_ar?.toRequestBody(),
                        name_en = name_en?.toRequestBody(),
                        email = email?.toRequestBody(),
                        password = password?.toRequestBody(),
                        country_id = country_id?.toRequestBody(),
                        bio_ar = bio_ar?.toRequestBody(),
                        bio_en = bio_en?.toRequestBody(),
                        photo = body
                    )
                )
            )
        } catch (exception: java.lang.Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }


    fun getProjects() = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = apiService?.getProjects()))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }

    fun addProject(data: AddProject) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = apiService?.addProject(data)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }


    fun getPosts(page: Int? = null) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = apiService?.getPosts(page)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }

    fun getPost(id: Int) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = apiService?.getPost(id)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }

    fun getProfile(id: Int) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = apiService?.getProfile(id)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }

    fun deletePost(id: Int) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = apiService?.deletePost(id)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }


    fun addPost(
        title_ar: String? = null,
        title_en: String? = null,
        description_ar: String? = null,
        description_en: String? = null,
        lat: String? = null,
        lang: String? = null,
        project_id: String? = null,
        images: File? = null,
    ) = liveData(Dispatchers.IO)
    {
        emit(Resource.loading(data = null))
        try {
            val body: MultipartBody.Part?
            val mediaType = "multipart/form-data".toMediaTypeOrNull()
            if (images != null) {
                val requestFile: RequestBody = images.asRequestBody(mediaType)
                body = MultipartBody.Part.createFormData("images[]", images.name, requestFile)
            } else {
                body = null
            }
            emit(
                Resource.success(
                    data = apiService?.addPost(
                        title_ar = title_ar?.toRequestBody(),
                        title_en = title_en?.toRequestBody(),
                        description_ar = description_ar?.toRequestBody(),
                        description_en = description_en?.toRequestBody(),
                        lat = lat?.toRequestBody(),
                        lang = lang?.toRequestBody(),
                        project_id = project_id?.toRequestBody(),
                        images = body
                    )
                )
            )
        } catch (exception: java.lang.Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }


    fun updatePost(
        title_ar: String? = null,
        title_en: String? = null,
        description_ar: String? = null,
        description_en: String? = null,
        lat: String? = null,
        lang: String? = null,
        project_id: String? = null,
        post_id: String? = null,
        images: File? = null,
    ) = liveData(Dispatchers.IO)
    {
        emit(Resource.loading(data = null))
        try {
            val body: MultipartBody.Part?
            val mediaType = "multipart/form-data".toMediaTypeOrNull()
            if (images != null) {
                val requestFile: RequestBody = images.asRequestBody(mediaType)
                body = MultipartBody.Part.createFormData("images[]", images.name, requestFile)
            } else {
                body = null
            }
            emit(
                Resource.success(
                    data = apiService?.updatePost(
                        title_ar = title_ar?.toRequestBody(),
                        title_en = title_en?.toRequestBody(),
                        description_ar = description_ar?.toRequestBody(),
                        description_en = description_en?.toRequestBody(),
                        lat = lat?.toRequestBody(),
                        lang = lang?.toRequestBody(),
                        project_id = project_id?.toRequestBody(),
                        post_id = post_id?.toRequestBody(),
                        images = body
                    )
                )
            )
        } catch (exception: java.lang.Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }


    fun createContact(data: Contacts) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = apiService?.createContact(data)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }


    fun createFollowing(data: Follow) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = apiService?.createFollowing(data)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }

    fun getCountries() = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = apiService?.getCountries()))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }

    fun getUsers() = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = apiService?.getUsers()))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }


    fun getFollowers() = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = apiService?.getFollowers()))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }


    fun getFeatures() = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = apiService?.getFeatures()))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }


    fun getFollowing() = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = apiService?.getFollowing()))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }

    fun unfollow(data: Follow) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = apiService?.unfollow(data)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }

    fun logout() = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = apiService?.logout()))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }

    fun searchUsers(data: Search, page: Int? = null) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = apiService?.searchUsers(data, page)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }

    fun removeImage(data: RemoveImage) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = apiService?.removeImage(data)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }

    fun getNotifications(page: Int? = null) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = apiService?.getNotifications(page)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }

}