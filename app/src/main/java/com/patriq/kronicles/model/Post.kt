package com.patriq.kronicles.model

class Post {

    private var postId: String = ""
    private var caption: String = ""
    private var publisher: String = ""
    private var postimage: String = ""
    private var category: String = ""

    constructor()
    constructor(
        postId: String,
        caption: String,
        publisher: String,
        postimage: String,
        category: String
    ) {
        this.postId = postId
        this.caption = caption
        this.publisher = publisher
        this.postimage = postimage
        this.category = category
    }

    fun getPostId(): String {
        return postId
    }

    fun getCategory(): String {
        return category
    }

    fun getpostimage(): String {
        return postimage
    }

    fun getpublisher(): String {
        return publisher
    }

    fun getcaption(): String {
        return caption
    }

    fun setPostId(postId: String) {
        this.postId = postId
    }

    fun setpostimage(postimage: String) {
        this.postimage = postimage
    }

    fun setpublisher(publisher: String) {
        this.publisher = publisher
    }

    fun setcaption(caption: String) {
        this.caption = caption
    }

    fun setCategory(category: String) {
        this.category = category
    }

}