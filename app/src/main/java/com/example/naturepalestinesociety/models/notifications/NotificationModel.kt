package com.example.naturepalestinesociety.models.notifications

//data class NotificationModel(
//    val image: String,
//    val title: String,
//    val body: String,
//    val createdAt: String
//) : java.io.Serializable {
//}

class NotificationModel : java.io.Serializable {

//    var image: String? = null
    var title: String? = null
    var body: String? = null
    var createdAt: String? = null
//    var screen: String? = null

    constructor() {}

    constructor(
        /*image: String?,*/ title: String, body: String, createdAt: String?/*, screen: String?*/
    ) {
//        this.image = image
        this.title = title
        this.body = body
        this.createdAt = createdAt
//        this.screen = screen
    }

}