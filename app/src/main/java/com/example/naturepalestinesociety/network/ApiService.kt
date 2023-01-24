package com.example.naturepalestinesociety.network

import com.example.naturepalestinesociety.models.AuthResponse
import com.example.naturepalestinesociety.models.BaseResponseArray
import com.example.naturepalestinesociety.models.BaseResponseObject
import com.example.naturepalestinesociety.models.User
import com.example.naturepalestinesociety.models.contacts.Contacts
import com.example.naturepalestinesociety.models.country.Country
import com.example.naturepalestinesociety.models.follow.Followers
import com.example.naturepalestinesociety.models.follow.Following
import com.example.naturepalestinesociety.models.follow.Follow
import com.example.naturepalestinesociety.models.login.Login
import com.example.naturepalestinesociety.models.login.Register
import com.example.naturepalestinesociety.models.login.RegisterResponse
import com.example.naturepalestinesociety.models.notifications.Notification
import com.example.naturepalestinesociety.models.post.PaginationResponse
import com.example.naturepalestinesociety.models.post.Post
import com.example.naturepalestinesociety.models.post.RemoveImage
import com.example.naturepalestinesociety.models.project.Project
import com.example.naturepalestinesociety.models.profile.Profile
import com.example.naturepalestinesociety.models.project.AddProject
import com.example.naturepalestinesociety.models.search.Search
import com.example.naturepalestinesociety.models.search.SearchUser
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST("login")
    suspend fun login(@Body data: Login): Response<AuthResponse<User>>

    @POST("register")
    suspend fun register(@Body data: Register): Response<AuthResponse<User>>

    @GET("profile")
    suspend fun getProfile(): Response<BaseResponseObject<Profile>>

    @GET("profile/{id}")
    suspend fun getProfile(@Path("id") id: Int): Response<BaseResponseObject<Profile>>

    @Multipart
    @POST("profile")
    suspend fun updateProfile(
        @Part("name_ar") name_ar: RequestBody? = null,
        @Part("name_en") name_en: RequestBody? = null,
        @Part("email") email: RequestBody? = null,
        @Part("password") password: RequestBody? = null,
        @Part("country_id") country_id: RequestBody? = null,
        @Part("bio_ar") bio_ar: RequestBody? = null,
        @Part("bio_en") bio_en: RequestBody? = null,
        @Part photo: MultipartBody.Part? = null,
    ): Response<BaseResponseObject<Profile>>

    @POST("projects")
    suspend fun addProject(@Body data: AddProject): Response<BaseResponseObject<Project>>

    @GET("projects")
    suspend fun getProjects(): Response<BaseResponseArray<Project>>

    @Multipart
    @POST("create_post")
    suspend fun addPost(
        @Part("title_ar") title_ar: RequestBody? = null,
        @Part("title_en") title_en: RequestBody? = null,
        @Part("description_ar") description_ar: RequestBody? = null,
        @Part("description_en") description_en: RequestBody? = null,
        @Part("lat") lat: RequestBody? = null,
        @Part("lang") lang: RequestBody? = null,
        @Part("project_id") project_id: RequestBody? = null,
        @Part images: MultipartBody.Part? = null,
    ): Response<BaseResponseObject<String>>

    @GET("posts")
    suspend fun getPosts(
        @Query("page") page: Int? = null
    ): Response<BaseResponseObject<PaginationResponse<Post>>>


    @GET("posts/{id}")
    suspend fun getPost(@Path("id") id: Int): Response<BaseResponseArray<Post>>

    @POST("delete_post/{id}")
    suspend fun deletePost(@Path("id") id: Int): Response<BaseResponseObject<String>>

    @Multipart
    @POST("update_post")
    suspend fun updatePost(
        @Part("title_ar") title_ar: RequestBody? = null,
        @Part("title_en") title_en: RequestBody? = null,
        @Part("description_ar") description_ar: RequestBody? = null,
        @Part("description_en") description_en: RequestBody? = null,
        @Part("lat") lat: RequestBody? = null,
        @Part("lang") lang: RequestBody? = null,
        @Part("project_id") project_id: RequestBody? = null,
        @Part("post_id") post_id: RequestBody? = null,
        @Part images: MultipartBody.Part? = null,
    ): Response<BaseResponseObject<String>>

    @POST("create_contact")
    suspend fun createContact(@Body data: Contacts): Response<BaseResponseObject<String>>


    @POST("create_following")
    suspend fun createFollowing(@Body data: Follow): Response<BaseResponseObject<String>>


    @GET("get_countries")
    suspend fun getCountries(): Response<BaseResponseArray<Country>>


    @GET("get_users")
    suspend fun getUsers(): Response<BaseResponseArray<User>>


    @GET("get_followers")
    suspend fun getFollowers(): Response<BaseResponseArray<Followers>>

    @GET("get_following")
    suspend fun getFollowing(): Response<BaseResponseArray<Following>>


    @GET("getfeatures")
    suspend fun getFeatures(): Response<BaseResponseArray<User>>

    @POST("unfollow")
    suspend fun unfollow(@Body data: Follow): Response<BaseResponseObject<String>>


    @POST("logout")
    suspend fun logout(): Response<BaseResponseObject<String>>


    @POST("removeimage")
    suspend fun removeImage(@Body data: RemoveImage): Response<BaseResponseObject<String>>


    @POST("search_users")
    suspend fun searchUsers(
        @Body data: Search,
        @Query("page") page: Int? = null
    ): Response<BaseResponseObject<PaginationResponse<User>>>


    @GET("get_notifications")
    suspend fun getNotifications(@Query("page") page: Int? = null): Response<BaseResponseObject<PaginationResponse<Notification>>>


//    @GET("/api/user/info")
//    suspend fun getUserProfile(): Response<UserProfileResponse>
//
//    @POST("/api/user/forgot-password")
//    suspend fun forgotPassword(@Body email: User.UserForgotPassword): Response<SimpleResponse>
//
//    @GET("api/user/logout")
//    suspend fun logout(): Response<SimpleResponse>
//
//    @POST("api/user/set-language")
//    suspend fun setLanguage(@Body language: User.SetLanguage): Response<SimpleResponse>
//
//    @POST("api/user/fcm")
//    suspend fun setFcm(@Body language: User.SetFcm): Response<SimpleResponse>
//
//
//
//    @GET("api/stores")
//    suspend fun getStores(): Response<StoresResponse>
//
//
//    @GET("api/user/home")
//    suspend fun getHome(): Response<HomeResponse>
//
//    @GET("api/product/category")
//    suspend fun getCategory(): Response<CategoryResponse>
//
//    @GET("api/product/category/{id}")
//    suspend fun getSubCategory(@Path("id") id: Int): Response<SubCategoryResponse>
//
//
//    @GET("api/products")
//    suspend fun getProductsFilter(
//        @Query("category_id") category_id: Int? = null,
//        @Query("name") name: String? = null,
//        @Query("brand_id") brand_id: Int? = null,
//        @Query("page") page: Int? = null
//    ): Response<ProductsFilterResponse>
//
//    @GET("api/products/{id}")
//    suspend fun getProductDetails(@Path("id") id: Int): Response<ProductDetailsResponse>
//
//    // -------------------------*********************-----------------------------------------------
//
//    //TODO :Response
//    @GET("api/user/add-product-to-favorite/{id}")
//    suspend fun addToFavorites(@Path("id") id: Int): Response<SimpleResponse>
//    //TODO :Response
//    @GET("api/user/favorites")
//    suspend fun getFavorites(): Response<GetFavoriteResponse>
//    //TODO :Response
//    @GET("/api/user/remove-product-from-favorite/{id}")
//    suspend fun removeFromFavorites(@Path("id") id: Int): Response<SimpleResponse>
//
//
//    // -------------------------*********************-----------------------------------------------
//
//    @GET("/api/user/cart/products")
//    suspend fun getCartProducts(): Response<CartResponse>
//
//    @GET("/api/user/cart/product/delete/{id}")
//    suspend fun deleteFromCart(@Path("id") id: Int): Response<DeleteFromCartResponse>
//
//    @POST("/api/user/cart/product/add-or-update")
//    suspend fun addToCartProduct(@Body cartProduct: CartModel.AddToCart): Response<AddToCartResponse>
//
//    @GET("api/brands")
//    suspend fun getBrands(): Response<BrandsResponse>
//
//    @GET("/api/user/shipping")
//    suspend fun getShippingAddress(): Response<GetShippingResponse>
//
//    @POST("/api/user/shipping/add")
//    suspend fun addShippingAddress(@Body data: ShippingModel.AddShippingAddress): Response<AddShippingResponse>
//
//    @POST("/api/user/shipping/delete")
//    suspend fun deleteShippingAddress(@Body data: ShippingModel.DeleteShippingAddress): Response<DeleteShippingResponse>
//
//    @POST("/api/user/shipping/update")
//    suspend fun updateShippingAddress(@Body data: ShippingModel.UpdateShippingAddress): Response<AddShippingResponse>
//
//
//    //TODO :Response
//    @POST("/api/user/order/add")
//    suspend fun addOrder(@Body data: OrderModel.AddOrder): Response<OrderDetailsResponse>
//
//    @GET("/api/user/orders")
//    suspend fun myOrders(): Response<MyOrdersResponse>
//
//    @GET("/api/user/orders/{id}")
//    suspend fun getOrderDetails(@Path("id") id: Int): Response<OrderDetailsResponse>
//
//    //TODO :Response
//    @POST("/api/user/order/rate")
//    suspend fun orderRate(@Body data: OrderModel.OrderRate): Response<OrderDetailsResponse>
//
//    @GET("/api/user/config")
//    suspend fun getConfig(): Response<ConfigResponse>


}