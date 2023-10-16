package com.patriq.kronicles.model

class Comment {
    private var comment: String = ""
    private var publisher: String = ""
    private var post: String = ""
    private var hashmapKey: String = ""

    constructor()

    constructor(comment: String, publisher: String, post: String, hashmapKey: String) {
        this.comment = comment
        this.publisher = publisher
        this.post = post
        this.hashmapKey = hashmapKey
    }

    fun getComment(): String {
        return comment
    }

    fun getPublisher(): String {
        return publisher
    }

    fun getPostId(): String {
        return post
    }

    fun gethashmapKey(): String {
        return hashmapKey
    }

    fun setComment(comment: String) {
        this.comment = comment
    }

    fun setPublisher(publisher: String) {
        this.publisher = publisher
    }

    fun setPostId(post: String) {
        this.post = post
    }

    fun sethashmapKey(hashmapKey: String) {
        this.hashmapKey = hashmapKey
    }
}